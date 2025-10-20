package com.zebra.shortcutcreatorservice;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class ShortCutCreatorService extends AccessibilityService {

    private static final String TAG = "ShortCutCreatorService";

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
    }
}
