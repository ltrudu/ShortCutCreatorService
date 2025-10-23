package com.zebra.shortcutcreatorservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.zebra.shortcutcreatorservice.Constants.EXCLUDED_KEYS;

public class ShortCutCreatorReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constants.SHORTCUT_RECEIVER_ACTION.equals(intent.getAction())) {
            // Update shared preferences with managed configuration values
            createShortcut(context, intent);
        }
    }

    private void createShortcut(Context context, Intent intent)
    {
        String uuidprefix = null;
        String shortLabel = null;
        String longLabel = null;
        String iconPath = null;
        String componentPackage = null;
        String componentActivity = null;
        Uri data = null;
        Bundle bundleExtras = null;

        // Get shortLabel if provided in intent
        if (intent.hasExtra(Constants.SHORTCUT_SHORT_LABEL)) {
            shortLabel = intent.getStringExtra(Constants.SHORTCUT_SHORT_LABEL);
        }

        if(shortLabel == null || shortLabel.isEmpty()){
            Toast.makeText(context, "Can\'t create a shortcut without short label.", Toast.LENGTH_LONG).show();
            Log.e(Constants.TAG, "Can\'t create a shortcut without short label.");
            return;
        }

         // Get component package if provided in intent
        if (intent.hasExtra(Constants.SHORTCUT_COMPONENT_PACKAGE)) {
            componentPackage = intent.getStringExtra(Constants.SHORTCUT_COMPONENT_PACKAGE);
        }

        if(componentPackage == null || componentPackage.isEmpty()){
            Toast.makeText(context, "Can\'t create a shortcut without a compoment package.", Toast.LENGTH_LONG).show();
            Log.e(Constants.TAG, "Can\'t create a shortcut without a compoment package.");
            return;
        }

        // Get component activity if provided in intent
        if (intent.hasExtra(Constants.SHORTCUT_COMPONENT_ACTIVITY)) {
            componentActivity = intent.getStringExtra(Constants.SHORTCUT_COMPONENT_ACTIVITY);
        }

        if(componentActivity == null || componentActivity.isEmpty()){
            Toast.makeText(context, "Can\'t create a shortcut without a compoment activity.", Toast.LENGTH_LONG).show();
            Log.e(Constants.TAG, "Can\'t create a shortcut without a compoment activity.");
            return;
        }

        // Update long label value if provided in intent
        if (intent.hasExtra(Constants.SHORTCUT_LONG_LABEL)) {
            longLabel = intent.getStringExtra(Constants.SHORTCUT_LONG_LABEL);
        }

        if(longLabel == null || longLabel.isEmpty())
        {
            longLabel = shortLabel;
        }

        // Get UUID Prefix if available
        if (intent.hasExtra(Constants.SHORTCUT_UUID_PREFIX)) {
            uuidprefix = intent.getStringExtra(Constants.SHORTCUT_UUID_PREFIX);
        }

        if(uuidprefix == null || uuidprefix.isEmpty())
        {
            uuidprefix = "ZebraShortCut_";
        }

        // Create UUID
        UUID uuid = UUID.randomUUID();
        String shortcutID = uuidprefix + uuid.toString();

        // Get icon path if available
        if (intent.hasExtra(Constants.SHORTCUT_ICON_PATH)) {
            iconPath = intent.getStringExtra(Constants.SHORTCUT_ICON_PATH);
        }

        // Check if the shortcut has data to pass to the target app
        if(intent.hasExtra(Constants.SHORTCUT_DATA))
        {
            String urlString = intent.getStringExtra(Constants.SHORTCUT_DATA);
            if(urlString != null && urlString.isEmpty() == false)
            {
                try {
                    data = Uri.parse(urlString);
                } catch (NullPointerException e) {
                    // Handle case where urlString might be null or invalid
                    e.printStackTrace();
                    data = null;
                }
            }
        }

        // Get extras if available
        bundleExtras = extractAndFilterExtras(intent);

        ShortCutHelper.createWithExtrasShortcut(context,
                shortcutID,
                shortLabel,
                longLabel,
                iconPath,
                componentPackage,
                componentActivity,
                data,
                bundleExtras);

    }

    private Bundle extractAndFilterExtras(Intent sourceIntent) {
        // 2. Get the original Bundle of extras
        Bundle sourceExtras = sourceIntent.getExtras();

        // Initialize the new, filtered Bundle
        Bundle filteredBundle = new Bundle();

        if (sourceExtras == null) {
            return filteredBundle;
        }

        // 3. Iterate and filter
        Set<String> keys = sourceExtras.keySet();
        for (String key : keys) {

            // 4. Use the Set's contains() method for fast filtering
            if (!EXCLUDED_KEYS.contains(key)) {
                Object value = sourceExtras.get(key);

                // 1. Check for null value
                if (value == null) {
                    continue;
                }

                // 2. Explicit Type Checking and Casting (covers most common types)
                if (value instanceof String) {
                    filteredBundle.putString(key, (String) value);
                } else if (value instanceof Integer) {
                    filteredBundle.putInt(key, (int) value);
                } else if (value instanceof Boolean) {
                    filteredBundle.putBoolean(key, (boolean) value);
                } else if (value instanceof Byte) {
                    filteredBundle.putByte(key, (byte) value);
                } else if (value instanceof Character) {
                    filteredBundle.putChar(key, (char) value);
                } else if (value instanceof Double) {
                    filteredBundle.putDouble(key, (double) value);
                } else if (value instanceof Float) {
                    filteredBundle.putFloat(key, (float) value);
                } else if (value instanceof Long) {
                    filteredBundle.putLong(key, (long) value);
                } else if (value instanceof Short) {
                    filteredBundle.putShort(key, (short) value);
                } else if (value instanceof Parcelable) {
                    // IMPORTANT: Use putParcelable for custom objects
                    filteredBundle.putParcelable(key, (Parcelable) value);
                } else if (value instanceof Serializable) {
                    // Use putSerializable for objects implementing Serializable
                    filteredBundle.putSerializable(key, (Serializable) value);
                } else if (value instanceof Bundle) {
                    // Recursively add Bundles
                    filteredBundle.putBundle(key, (Bundle) value);
                }
                // Add array and list types for full coverage
                else if (value instanceof String[]) {
                    filteredBundle.putStringArray(key, (String[]) value);
                } else if (value instanceof ArrayList<?>) {
                    // Note: Generics require careful handling. This covers ArrayList<String>
                    if (value instanceof ArrayList) {
                        filteredBundle.putStringArrayList(key, (ArrayList<String>) value);
                    }
                }
            }
        }

        return filteredBundle;
    }
}
