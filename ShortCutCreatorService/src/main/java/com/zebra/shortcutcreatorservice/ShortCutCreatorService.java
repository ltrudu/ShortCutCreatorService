package com.zebra.shortcutcreatorservice;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.core.content.ContextCompat;

public class ShortCutCreatorService extends AccessibilityService {

    private static final String TAG = "ShortCutCreatorService";

    ShortCutCreatorReceiver shortCutCreatorReceiver = null;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // This method is called for accessibility events, but key events are handled in onKeyEvent
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted");
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "ShortCutCreatorService connected");
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
