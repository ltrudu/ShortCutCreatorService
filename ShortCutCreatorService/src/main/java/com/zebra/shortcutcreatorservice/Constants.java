package com.zebra.shortcutcreatorservice;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constants {
    public final static String TAG = "ShortcutCreatorService";

    public static final String SHORTCUT_RECEIVER_ACTION = "android.app.action.SHORTCUT_CREATOR_SERVICE_CREATE";

    public static final String ACCESSIBITLITY_SERVICE_CLASS_NAME = "com.zebra.shortcutcreatorservice.ShortCutCreatorService";

    // Shortcut receiver parameters
    public static final String SHORTCUT_UUID_PREFIX = "shortcut_uuid_prefix";
    public static final String SHORTCUT_COMPONENT_PACKAGE = "shortcut_component_package";
    public static final String SHORTCUT_COMPONENT_ACTIVITY = "shortcut_component_activity";
    public static final String SHORTCUT_SHORT_LABEL = "shortcut_short_label";
    public static final String SHORTCUT_LONG_LABEL = "shortcut_long_label";
    public static final String SHORTCUT_ICON_PATH = "shortcut_icon_path";

    public static final Set<String> EXCLUDED_KEYS = new HashSet<>(Arrays.asList(
            SHORTCUT_UUID_PREFIX,
            SHORTCUT_COMPONENT_PACKAGE,
            SHORTCUT_COMPONENT_ACTIVITY,
            SHORTCUT_SHORT_LABEL,
            SHORTCUT_LONG_LABEL,
            SHORTCUT_ICON_PATH
    ));
}
