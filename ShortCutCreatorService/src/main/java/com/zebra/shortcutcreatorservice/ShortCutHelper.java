package com.zebra.shortcutcreatorservice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public class ShortCutHelper {

    public static void createWithExtrasShortcut(
            Context context,
            String shortcutId,
            String shortLabel,
            String longLabel,
            String iconPath,
            String componentPackage,
            String componentActivity,
            Bundle extras) {

        // Check if the Android version supports App Shortcuts (API 25/7.1 or newer)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            Toast.makeText(context, "App Shortcuts are not supported on this Android version.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 0. Get the system service
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

        // Safety check: ensure manager is available and max shortcuts limit is not exceeded
        if (shortcutManager == null || shortcutManager.getDynamicShortcuts().size() >= shortcutManager.getMaxShortcutCountPerActivity()) {
            // Optional: Log an error or handle the limit case
            Toast.makeText(context, "Shortcut limit reached or manager unavailable.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Define the Intent
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Explicitly set the Component (Activity) to launch
        // Replace 'TargetActivity.class' with the actual Activity class in your app
        intent.setComponent(new ComponentName(componentPackage, componentActivity));

        // Add flags to ensure the app is launched correctly
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // 2. Attach the Qualified Parameters (Intent Extras)
        if(extras != null && !extras.isEmpty())
        {
            intent.putExtras(extras);
        }

        // 3. Create shortcut icon
        Icon shortcutIcon;
        File iconFile = null;
        if(iconPath != null && iconPath.isEmpty() == false)
        {
            iconFile = new File(iconPath);
        }

        if (iconFile !=null && iconFile.exists()) {
            // Use the icon from the specified file path
            shortcutIcon = Icon.createWithFilePath(iconPath);
        } else {
            // Fallback: If the file doesn't exist, use a default resource icon
            // R.drawable.ic_default_icon must exist in your project's drawable folder!
            shortcutIcon = Icon.createWithResource(context, R.drawable.ic_launcher_foreground);
        }

        // 4. Build the ShortcutInfo object
        ShortcutInfo shortcut = new ShortcutInfo.Builder(context, shortcutId)
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setIcon(shortcutIcon)
                .setIntent(intent) // Intent with ComponentName and Extras is attached here
                .build();

        // 5. Publish (set or update) the dynamic shortcut
        try {
            if(shortcutManager.isRequestPinShortcutSupported())
            {
                shortcutManager.requestPinShortcut(shortcut, null);
            }
            else {
                shortcutManager.setDynamicShortcuts(Collections.singletonList(shortcut));
            }
            // Optional: Show success message or log
            //Toast.makeText(context, "Shortcut created for: " + shortcutId, Toast.LENGTH_SHORT).show();
            Log.d(Constants.TAG, "Shortcut created for: " + shortcutId);
        } catch (Exception e) {
            // Handle potential SecurityException or other issues
            Toast.makeText(context, "Failed to create shortcut.", Toast.LENGTH_SHORT).show();
        }
    }
}