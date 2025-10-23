package com.zebra.shortcutcreatorservice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.net.URL;
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
            Uri data,
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

        // 2. Attach the Qualified Parameters (Intent Extras) and eventual data
        if (extras != null && !extras.isEmpty()) {
            intent.putExtras(extras);
        }

        // 2.1 Attach data if some were passed to the shortcut service intent
        if (data != null)
        {
            intent.setData(data);
        }

        // 3. Create shortcut icon
        Icon shortcutIcon = null;
        File iconFile = null;
        if (iconPath != null && iconPath.isEmpty() == false) {
            iconFile = new File(iconPath);
        }

        Bitmap iconBitmap = null;
        if (iconFile != null && iconFile.exists()) {
            // Use the icon from the specified file path
            iconBitmap = BitmapFactory.decodeFile(iconPath);
        }

        if(iconBitmap != null)
        {
            int desiredSize = (int) context.getResources().getDimension(android.R.dimen.app_icon_size);
            Bitmap finalBitmap = Bitmap.createScaledBitmap(iconBitmap, desiredSize, desiredSize, true);

            // 3. Create the Icon from the Bitmap
            // Use createWithAdaptiveBitmap to prevent Android badge overlay
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                shortcutIcon = Icon.createWithAdaptiveBitmap(finalBitmap);
            } else {
                shortcutIcon = Icon.createWithBitmap(finalBitmap);
            }
        }

        if(shortcutIcon == null)
        {
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

    /**
     * Adds a custom badge to the bottom-right corner of an icon bitmap.
     * This allows you to add your own badge instead of the default Android one.
     *
     * @param icon The main icon bitmap
     * @param badgeBitmap The badge to overlay (or null to draw a default colored badge)
     * @param badgeColor The color for the default badge background (if badgeBitmap is null)
     * @return Bitmap with the badge applied
     */
    public static Bitmap addCustomBadge(Bitmap icon, Bitmap badgeBitmap, int badgeColor) {
        // Create a mutable copy of the icon
        Bitmap result = icon.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);

        int iconSize = result.getWidth();
        int badgeSize = iconSize / 3; // Badge is 1/3 the size of the icon

        if (badgeBitmap != null) {
            // Use the provided badge bitmap
            Bitmap scaledBadge = Bitmap.createScaledBitmap(badgeBitmap, badgeSize, badgeSize, true);

            // Draw badge in bottom-right corner
            int left = iconSize - badgeSize;
            int top = iconSize - badgeSize;
            canvas.drawBitmap(scaledBadge, left, top, null);
        } else {
            // Draw a default circular badge with the specified color
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            // Draw badge background circle
            paint.setColor(badgeColor);
            paint.setStyle(Paint.Style.FILL);

            int centerX = iconSize - badgeSize / 2;
            int centerY = iconSize - badgeSize / 2;
            int radius = badgeSize / 2;

            canvas.drawCircle(centerX, centerY, radius, paint);

            // Draw white border around badge
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(badgeSize * 0.1f);
            canvas.drawCircle(centerX, centerY, radius, paint);
        }

        return result;
    }

    /**
     * Adds a custom badge with an icon from a file path to the bottom-right corner.
     *
     * @param icon The main icon bitmap
     * @param badgeIconPath File path to the badge icon image
     * @return Bitmap with the badge applied
     */
    public static Bitmap addCustomBadgeFromPath(Bitmap icon, String badgeIconPath) {
        Bitmap badgeBitmap = null;
        if (badgeIconPath != null && !badgeIconPath.isEmpty()) {
            File badgeFile = new File(badgeIconPath);
            if (badgeFile.exists()) {
                badgeBitmap = BitmapFactory.decodeFile(badgeIconPath);
            }
        }
        return addCustomBadge(icon, badgeBitmap, Color.GREEN);
    }

    /**
     * Adds a custom badge with an icon resource to the bottom-right corner.
     *
     * @param context Application context
     * @param icon The main icon bitmap
     * @param badgeResourceId Resource ID of the badge drawable (e.g., R.drawable.custom_badge)
     * @return Bitmap with the badge applied
     */
    public static Bitmap addCustomBadgeFromResource(Context context, Bitmap icon, int badgeResourceId) {
        Bitmap badgeBitmap = BitmapFactory.decodeResource(context.getResources(), badgeResourceId);
        return addCustomBadge(icon, badgeBitmap, Color.GREEN);
    }
}