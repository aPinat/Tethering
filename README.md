# Tethering

Activates ethernet tethering on Android 12, since the OxygenOS 12 upgrade has removed the option in the settings app for me.

Uses the `tethering` system service and reflection to access the hidden [TetheringManager.java](https://android.googlesource.com/platform/packages/modules/Connectivity/+/refs/heads/master/Tethering/common/TetheringLib/src/android/net/TetheringManager.java) class.

Since methods are tagged as `@SystemApi`, the app needs to be installed as system app to work or Android will block their execution.
