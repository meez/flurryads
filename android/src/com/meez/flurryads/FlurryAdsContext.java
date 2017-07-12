package com.meez.flurryads;

import android.annotation.TargetApi;

import android.os.Build;
import android.util.Log;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;
import com.flurry.android.ads.FlurryAdTargeting;

import java.util.HashMap;
import java.util.Map;

// Flurry SDK 7.x.x requires a minimum Android API level 10.
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class FlurryAdsContext extends FREContext implements FlurryAdInterstitialListener
{
    // Definitions

    // States

    // Instance Vars

    /** Current state */
    private FlurryAdInterstitial videoAdInterstitial;

    // Public methods

    /** Create a new FlurryAdsContext */
    public FlurryAdsContext()
    {
        Log.d(Extension.TAG, "FlurryAdsContext()");
    }

    /**
     * Dispose
     * Called from AIR/actionscript on mobile app shutdown.
     */
    @Override
    public void dispose()
    {
        Log.d(Extension.TAG, "FlurryAdsContext.dispose()");
        Extension.context = null;
    }

    /**
     * Registers AS function name to Java Function Class
     */
    @Override
    public Map<String, FREFunction> getFunctions()
    {
        Log.d(Extension.TAG, "FlurryAdsContext.getFunctions");

        Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
        functionMap.put("showVideoAd", new ShowVideoAdFunction());

        return functionMap;
    }

    // Implementation

    /** Show Interstitial video ad
     * @param adPlacement   Ad Placement Id
     * @param playerId      Current Meez Player id
     * @param sessionRef    Meez session ref
     * @param testMode      Should ads be requested in test mode
     */
    public void showVideoAd(String adPlacement, String playerId, String sessionRef, boolean testMode)
    {
        Log.d(Extension.TAG, "showVideoAd("+adPlacement+", "+playerId+", "+sessionRef+", "+testMode+")");

        // Ensure API Level 10
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1)
        {
            sendError("videoError", "500:Device API level (" + Build.VERSION.SDK_INT + ") less than required level (" + Build.VERSION_CODES.GINGERBREAD_MR1 + ")");
            return;
        }

        if (this.videoAdInterstitial!=null && this.videoAdInterstitial.isReady())
        {
            Log.d(Extension.TAG, "video ad ready. displaying.");
            this.videoAdInterstitial.displayAd();
        }
        else
        {
            Log.d(Extension.TAG, "Creating new video ad");
            this.videoAdInterstitial = new FlurryAdInterstitial(this.getActivity(), adPlacement);
            this.videoAdInterstitial.setTargeting(getAdTargeting(playerId, sessionRef, testMode));
            this.videoAdInterstitial.setListener(this);
            this.videoAdInterstitial.fetchAd();
        }
    }

    /** Get Ad Targeting */
    protected FlurryAdTargeting getAdTargeting(String userId, String sessionRef, boolean testMode)
    {
        HashMap<String, String> cookies = new HashMap<String, String>();
        cookies.put("player_id", userId);
        cookies.put("session_ref", sessionRef);
        FlurryAdTargeting adTargeting = new FlurryAdTargeting();
        adTargeting.setUserCookies(cookies);
        adTargeting.setEnableTestAds(testMode);

        return adTargeting;
    }

    // Messaging

    /** Send Event (to Actionscript) */
    protected void sendEvent(String type)
    {
        Log.d(Extension.TAG, "Sending Event(" + type + ")");
        dispatchEventWithReason(type, "");
    }
    /** Send Error (to Actionscript) */
    protected void sendError(String type, String msg)
    {
        Log.w(Extension.TAG, "Sending Error(" + type + ", " + msg + ")");
        dispatchEventWithReason(type, msg);
    }

    /** Dispatch event back to ANE Actionscript
     * @param type		type of Flash Event (e.g 'loadComplete') (@see FlurryAdsEvent.as)
     * @param reason	reason for event incl. event code if it exists (e.g. '1001 no fill')
     */
    protected void dispatchEventWithReason(String type, String reason)
    {
        // Dispose has been called
        if (Extension.context==null)
        {
            Log.w(Extension.TAG, "Dispatching event (" + type + ", " + reason + ") after dispose()");
            return;
        }

        try
        {
            dispatchStatusEventAsync(type, reason);
        }
        catch (Throwable t)
        {
            Log.e(Extension.TAG, "Could not send event ("+type+", "+reason+")", t);
        }
    }

    // FlurryAdInterstitialListener implementation

    @Override
    public void onFetched(FlurryAdInterstitial flurryAdInterstitial)
    {
        // This method will be called when the ad has been received from the server
        Log.d(Extension.TAG, "Flurry Ad fetched");

        flurryAdInterstitial.displayAd();
        sendEvent("videoFetched");
    }

    @Override
    public void onRendered(FlurryAdInterstitial flurryAdInterstitial)
    {
        // This method will be called when ad is successfully rendered.
        Log.d(Extension.TAG, "Flurry Ad rendered");
    }

    @Override
    public void onDisplay(FlurryAdInterstitial flurryAdInterstitial)
    {
        // This method will be called when the user has opened the ad.
        Log.d(Extension.TAG, "Flurry Ad displayed");

        sendEvent("videoDisplayed");
    }

    @Override
    public void onClose(FlurryAdInterstitial flurryAdInterstitial)
    {
        // This method will be called when the user dismisses the current Ad .
        Log.d(Extension.TAG, "Flurry Ad closed");

        sendEvent("videoClosed");
    }

    @Override
    public void onAppExit(FlurryAdInterstitial flurryAdInterstitial)
    {
        // This method will be called when the user is leaving the application after following
        // events associated with the current Ad in the provided Ad Space name.
        Log.d(Extension.TAG, "App Exit");

        flurryAdInterstitial.destroy();
    }

    @Override
    public void onClicked(FlurryAdInterstitial flurryAdInterstitial)
    {
        // This method will be called when the user has clicked on the ad.
        Log.d(Extension.TAG, "Flurry Ad clicked");

        sendEvent("videoClicked");
    }

    @Override
    public void onVideoCompleted(FlurryAdInterstitial flurryAdInterstitial)
    {
        // This method is present only in case the ad served is a video clip and adspace is marked as client side rewarded, or rewarded.
        // Make sure to not destroy the ad object in onStop as it will prevent this callback form fireing
        Log.d(Extension.TAG, "Flurry Ad completed");

        sendEvent("videoComplete");
    }

    @Override
    public void onError(FlurryAdInterstitial flurryAdInterstitial, FlurryAdErrorType flurryAdErrorType, int i)
    {
        // This method will be called when fetch, render or click failed.
        // Fetch error 20 indicates no fill (additional error codes/info: https://developer.yahoo.com/flurry/docs/faq/faqpublisher/android/)

        Log.d(Extension.TAG, "Flurry Ad error: " + flurryAdErrorType + " " + i);

        String msg = i+":"+flurryAdErrorType.toString();
        sendError("videoError", msg);
    }

    // Nested Function Classes

    /** Base FlurryAds Function */
    abstract class FlurryAdsFunction implements FREFunction
    {
        // Instance vars

        protected String name;

        public  FlurryAdsFunction(String name)
        {
            this.name=name;
        }

        @Override
        public FREObject call(FREContext freContext, FREObject[] freObjects)
        {
            try
            {
                int res = execute(freObjects);
                return  FREObject.newObject(res);
            }
            catch (Throwable t)
            {
                Log.e(Extension.TAG, "[" + this.name + "] Could not retrieve passed FREObject params", t);
                sendError("videoError", "500:"+t.getMessage());
                return  null;
            }
        }

        // Implementation

        /** Execute */
        protected abstract int execute(FREObject[] params) throws Exception;
    }

    /** Show Video Ad Function */
    class ShowVideoAdFunction extends  FlurryAdsFunction
    {
        public ShowVideoAdFunction()
        {
            super("ShowVideoAdFunction");
        }

        @Override protected int execute(FREObject[] params) throws Exception
        {
            String adPlacement = params[0].getAsString();
            String playerId = params[1].getAsString();
            String sessionRef = params[2].getAsString();
            boolean testMode = params[3].getAsBool();
            showVideoAd(adPlacement, playerId, sessionRef, testMode);
            return  0;
        }

    }

}
