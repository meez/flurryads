package com.meez.flurryads
{
	
import flash.events.EventDispatcher;
import flash.events.StatusEvent;
import flash.external.ExtensionContext;
import flash.system.Capabilities;

public class FlurryAds extends EventDispatcher 
{
  
	/** Extension context */
	private var context:ExtensionContext;
	
	/** Create new FlurryAds */
	public function FlurryAds():void 
	{
        try
        {
            this.context = ExtensionContext.createExtensionContext("com.meez.FlurryAds", null);
        }
        catch (e:Error)
        {
            trace(e);
        }
        if (!hasContext())
        {
            trace("[FlurryAds] Cannot create FlurryAds extension.");
            return;
        }
		this.context.addEventListener(StatusEvent.STATUS, onStatusEvent);
	}

	/** dispose */
	public function dispose():void
	{
        if (!hasContext())
            return;
            
		this.context.removeEventListener(StatusEvent.STATUS, onStatusEvent);
		this.context.dispose();
        this.context = null;
	}
    
    /** is supported */
	public static function isSupported():Boolean
	{
		if (!isAndroid())
        {
            return false;
        }
        
        return true;
	}
    
	// Native functions
    
    /** Show a Interstitial video ad
     * @param adSpace       Ad Space ID (@see https://dev.flurry.com/angular.do?page=/appSpot/inventory/adSpaces/listAdSpaces)
     * @param playerId      ID of current user (to award for watching video)
     * @param sessionRef    Meez Session ref
     * @param testMode      Set testMode to true to guarantee video appearance. *IMPORTANT* Be sure this is *false* in release app
     */
    public function showVideoAd(adSpace:String, playerId:String, sessionRef:String, testMode:Boolean=false):void
    {
        if (!hasContext())
            return;
            
        this.context.call("showVideoAd", adSpace, playerId, sessionRef, testMode);
    }
    
    // Implementation
    
    /** is android device */
	private static function isAndroid():Boolean
	{
		return Capabilities.manufacturer.indexOf('Android') > -1;
	}
    
    /** Context is ready */
    private function hasContext():Boolean
    {
        return this.context != null;
    }
	
	// Events
	
	/** On status event sent from Native Extension context */
	private function onStatusEvent(e:StatusEvent):void
	{
		var type:String = e.code;
		var reason:String = e.level;
        var code:Number = 0;
        try
        {
            // Error messages are sent from ane in format code:reason (e.g. 20:no-fill )
            var codeReason:Array = reason.split(":");
            if (codeReason.length==2)
            {
                var c:Number = parseFloat(codeReason[0]);
                if (!isNaN(c))
                    code=c;
            }
        }
        catch (e:Error)
        {
            trace("[FlurryAds] Could not split event reason into code:message");
            trace(e);
        }
        
        dispatchEvent(new FlurryAdsEvent(type, reason, code));
	}
	
}
	
}