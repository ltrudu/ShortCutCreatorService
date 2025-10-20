# AppOverLockScreenTest

This app demonstrate how to use the read button of Zebra's equipped devices (HC25 for example) to wake up the screen and open an activity on top of the lock screen.

## How to install

First, go to the StageNow folder and process the two StageNow barcodes on the device.

EnableAllWakeUpSource : Will enable all buttons as wake up source to wake up the device when it is in sleep mode

Set_REAR_BUTTON_To_Default_Value : Will set the KeyMapping of the rear button to default value (Buton_L1 -> Keycode 102), so the accessibility service will be able to catch when it is pushed.

Install the application on the device.

Go to Settings -> Accessibility -> Dowloaded Apps -> AppOverLockScreenTest

Enable the application (do not enable the shortcut as we won't use it)

Now lock the screen with the power button, and push the rear button to wake up, the push the rear button to open the AppOverLockScreenTest MainActivity.
