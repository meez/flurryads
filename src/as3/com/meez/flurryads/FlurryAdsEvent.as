package com.meez.flurryads 
{
	
import flash.events.Event;

/** FlurryAdsEvent */
public class FlurryAdsEvent extends Event 
{
	// Definitions
	
	/** Event Types */
    public static const VIDEO_FETCHED:String="videoFetched";
    public static const VIDEO_DISPLAYED:String="videoDisplayed";
    public static const VIDEO_COMPLETE:String="videoComplete";
    public static const VIDEO_CLOSED:String="videoClosed";
    public static const VIDEO_CLICKED:String="videoClicked";
    
    /** Error Event Types */
    public static const VIDEO_ERROR:String="videoError";
	
    // Instance vars
    
    /** Reason */
    public var reason:String;
    
    /** Code */
    public var code:Number;

	// Public Methods
	
	/** Create new FlurryAdsEvent */
	public function FlurryAdsEvent(type:String, reason:String, code:Number=0) 
	{ 
        this.reason = reason;
        this.code = code;
		super(type);
	} 
	
	/** clone */
	public override function clone():Event 
	{ 
		return new FlurryAdsEvent(type, reason, code);
	} 
	
	/** To String */
	public override function toString():String 
	{ 
		return formatToString("FlurryAdsEvent", "type", "reason", "code", "bubbles", "cancelable", "eventPhase"); 
	}
	
}
	
}