# ShortCutCreationService

WIP: This app (if it works) will allow the users to create shortcuts for any apps using StageNow, ADB or an EMM.

To create a shortcut, install this service, go to settings, enable it in accessibility.

Some examples:
----------------------------
adb shell am broadcast -a android.app.action.SHORTCUT_CREATOR_SERVICE_CREATE --es shortcut_uuid_prefix IWPrefix --es shortcut_component_package com.android.chrome --es shortcut_component_activity com.google.android.apps.chrome.Main --es shortcut_short_label "Google" --es shortcut_icon_path /storage/emulated/0/Documents/icon.png -d "http://www.amazon.com"

This command will create a shortcut to Google Chrome and pass the url as data to Chrome

----------------------------
adb shell am broadcast -a android.app.action.SHORTCUT_CREATOR_SERVICE_CREATE --es shortcut_uuid_prefix IWPrefix --es shortcut_component_package com.zebra.immersivewebview --es shortcut_component_activity com.zebra.immersivewebview.MainActivity --es shortcut_short_label "Google" --es URL "https://www.google.com" --ei text_zoom 200 --es shortcut_icon_path /storage/emulated/0/Documents/icon.png

This command will create a shortcut that open the Mainactivity of the app com.zebra.immersivewebview and pass the following arguments to it: --es URL "https://www.google.com" --ei text_zoom 150


