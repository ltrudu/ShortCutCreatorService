package com.zebra.shortcutcreatorservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.core.content.ContextCompat;

import java.util.List;

public class ShortCutCreatorService extends AccessibilityService {

    private static final String TAG = "ShortCutCreatorService";
    private static final String SHORTCUT_DIALOG_TITLE = "ShortCutCreatorService";

    private boolean autoClickEnabled = false;

    ShortCutCreatorReceiver shortCutCreatorReceiver = null;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return;
        }

        int eventType = event.getEventType();

        // Listen for window state changes and window content changes
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                // Check if this is the shortcut creation dialog
                if (isShortcutCreationDialog(rootNode)) {
                    Log.d(TAG, "Shortcut creation dialog detected");

                    if (autoClickEnabled) {
                        // Find and click the confirmation button
                        boolean clicked = findAndClickConfirmButton(rootNode);
                        if (clicked) {
                            Log.d(TAG, "Successfully auto-clicked the confirmation button");
                        } else {
                            Log.w(TAG, "Could not find or click the confirmation button");
                        }
                    }
                }
                rootNode.recycle();
            }
        }
    }

    /**
     * Checks if the current window is the shortcut creation dialog
     */
    private boolean isShortcutCreationDialog(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return false;
        }

        // Look for the dialog title "ShortCutCreatorService"
        List<AccessibilityNodeInfo> titleNodes = rootNode.findAccessibilityNodeInfosByText(SHORTCUT_DIALOG_TITLE);
        if (titleNodes != null && !titleNodes.isEmpty()) {
            for (AccessibilityNodeInfo node : titleNodes) {
                node.recycle();
            }
            return true;
        }

        return false;
    }

    /**
     * Finds and clicks the confirmation button in the shortcut creation dialog.
     * The button text varies by language, so we use the resource-id instead.
     */
    private boolean findAndClickConfirmButton(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return false;
        }

        // Strategy 1: Look for button by resource ID
        // The "Add to home screen" button typically has the ID "android:id/button1" or similar
        List<AccessibilityNodeInfo> buttonNodes = rootNode.findAccessibilityNodeInfosByViewId("android:id/button1");
        if (buttonNodes != null && !buttonNodes.isEmpty()) {
            for (AccessibilityNodeInfo buttonNode : buttonNodes) {
                if (buttonNode.isClickable() && buttonNode.isEnabled()) {
                    boolean clicked = buttonNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    buttonNode.recycle();
                    if (clicked) {
                        Log.d(TAG, "Clicked button via resource ID android:id/button1");
                        cleanupNodes(buttonNodes);
                        return true;
                    }
                }
                buttonNode.recycle();
            }
        }

        // Strategy 2: Recursively search for clickable buttons
        // This is a fallback in case the resource ID doesn't work
        List<AccessibilityNodeInfo> allButtons = findAllClickableButtons(rootNode);
        if (allButtons != null && !allButtons.isEmpty()) {
            // We want the second button (index 1) which is typically the positive action button
            // First button (index 0) is usually "Cancel"
            if (allButtons.size() >= 2) {
                AccessibilityNodeInfo confirmButton = allButtons.get(1);
                if (confirmButton.isClickable() && confirmButton.isEnabled()) {
                    boolean clicked = confirmButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    if (clicked) {
                        Log.d(TAG, "Clicked button via recursive search (second button)");
                        cleanupNodes(allButtons);
                        return true;
                    }
                }
            } else if (allButtons.size() == 1) {
                // If there's only one button, click it
                AccessibilityNodeInfo singleButton = allButtons.get(0);
                if (singleButton.isClickable() && singleButton.isEnabled()) {
                    boolean clicked = singleButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    if (clicked) {
                        Log.d(TAG, "Clicked single button found");
                        cleanupNodes(allButtons);
                        return true;
                    }
                }
            }
            cleanupNodes(allButtons);
        }

        return false;
    }

    /**
     * Recursively finds all clickable button nodes in the view hierarchy
     */
    private List<AccessibilityNodeInfo> findAllClickableButtons(AccessibilityNodeInfo node) {
        List<AccessibilityNodeInfo> buttons = new java.util.ArrayList<>();
        if (node == null) {
            return buttons;
        }

        // Check if this node is a clickable button
        CharSequence className = node.getClassName();
        if (className != null && className.toString().equals("android.widget.Button") && node.isClickable()) {
            buttons.add(node);
        }

        // Recursively search child nodes
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                List<AccessibilityNodeInfo> childButtons = findAllClickableButtons(child);
                buttons.addAll(childButtons);
            }
        }

        return buttons;
    }

    /**
     * Helper method to recycle a list of AccessibilityNodeInfo objects
     */
    private void cleanupNodes(List<AccessibilityNodeInfo> nodes) {
        if (nodes != null) {
            for (AccessibilityNodeInfo node : nodes) {
                if (node != null) {
                    node.recycle();
                }
            }
        }
    }

    /**
     * Enable or disable auto-clicking of the confirmation button
     */
    public void setAutoClickEnabled(boolean enabled) {
        this.autoClickEnabled = enabled;
        Log.d(TAG, "Auto-click enabled: " + enabled);
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted");
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "ShortCutCreatorService connected");

        // Enable auto-click by default
        autoClickEnabled = true;

        if(shortCutCreatorReceiver == null)
        {
            shortCutCreatorReceiver = new ShortCutCreatorReceiver();
            IntentFilter shortcutFilter = new IntentFilter();
            shortcutFilter.addAction(Constants.SHORTCUT_RECEIVER_ACTION);
            ContextCompat.registerReceiver(this, shortCutCreatorReceiver, shortcutFilter, ContextCompat.RECEIVER_EXPORTED);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "ShortCutCreatorService unbinded");
        if(shortCutCreatorReceiver != null)
        {
            unregisterReceiver(shortCutCreatorReceiver);
            shortCutCreatorReceiver = null;
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "ShortCutCreatorService rebinded");
        if(shortCutCreatorReceiver == null)
        {
            shortCutCreatorReceiver = new ShortCutCreatorReceiver();
            IntentFilter shortcutFilter = new IntentFilter();
            shortcutFilter.addAction(Constants.SHORTCUT_RECEIVER_ACTION);
            ContextCompat.registerReceiver(this, shortCutCreatorReceiver, shortcutFilter, ContextCompat.RECEIVER_EXPORTED);
        }
        super.onRebind(intent);
    }
}
