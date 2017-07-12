package com.meez.flurryads;

import android.util.Log;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

public class Extension implements FREExtension
{
    /** Logging tag */
	public static final String TAG = "[FlurryAds]";

	/** Native extension context */
	public static FlurryAdsContext context;

	/** Create the context (AS to Java). */
	public FREContext createContext(String extId)
	{
		Log.d(TAG, "FlurryAds.createContext()");
		return context = new FlurryAdsContext();
	}

	/** Dispose */
	public void dispose()
	{
		Log.d(TAG, "FlurryAds.dispose");

        // Context is disposed within FlurryAdsContext
	}

	/** Initialize the context. */
	public void initialize()
    {
        Log.d(TAG, "FlurryAds.initialize");
        // nothing happening here.
    }

}
