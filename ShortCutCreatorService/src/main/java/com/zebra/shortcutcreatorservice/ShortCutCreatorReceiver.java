package com.zebra.shortcutcreatorservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;
import java.util.UUID;

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
        Map<String, Object> extras = null;
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
            longLabel = longLabel;
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

        // Get extras if available and add them in a map
        if(intent.hasExtra(Constants.SHORTCUT_BUNDLE_EXTRAS))
        {
            bundleExtras = intent.getBundleExtra(Constants.SHORTCUT_BUNDLE_EXTRAS);
        }

        ShortCutHelper.createWithExtrasShortcut(context,
                shortcutID,
                shortLabel,
                longLabel,
                iconPath,
                componentPackage,
                componentActivity,
                bundleExtras);
    }
}
