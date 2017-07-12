# flurryads
ANE for Flurry Ads

Information on [setting up rewarded video ads](https://developer.yahoo.com/flurry/docs/publisher/gettingstarted/appcirclerewardedads/#server-side-rewarded-ads)

Required activity in App Descriptor within `<application>` element:

```
<activity 
  android:name="com.flurry.android.FlurryFullscreenTakeoverActivity"
  android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize" />
```

This ane depends on Flurry Analytics ANE (com.sticksports.nativeExtensions.Flurry). Flurry analytics must be initialized before calls to FlurryAds can be made.
