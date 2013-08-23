package com.sunlightlabs.android.congress.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.sunlightlabs.android.congress.R;
import com.sunlightlabs.android.congress.Settings;

/**
 * Helper class to manage Google Analytics tracking. All calls to log a page view or event should go through this class,
 * as it centralizes the check on whether the user has opted out of tracking altogether.
 * 
 * All calls to log a page view or an event are dispatched immediately.
 */

public class Analytics {
	
	//TODO:
	//  * one-off URLs -> events (e.g. about)
	//  * remove tracker object from Footer altogether?
	//  * custom variables on events
	//  * is entry tracking impossible with EasyTracker?
	
	// custom variable slots (can range from 1-5)
	public static final int CUSTOM_VERSION_SLOT = 2;
	public static final String CUSTOM_VERSION_NAME = "Version";
	
	public static final int CUSTOM_ORIGINAL_CHANNEL = 3;
	public static final String CUSTOM_ORIGINAL_CHANNEL_NAME = "Original Channel";
	public static final String ORIGINAL_CHANNEL_PREFERENCE = "original_distribution_channel";
	
	public static final int CUSTOM_MARKET_CHANNEL = 4;
	public static final String CUSTOM_MARKET_CHANNEL_NAME = "Market Channel";
	
	// for use in investigating intents for entry sources
	public static final String EXTRA_ENTRY_FROM = "com.sunlightlabs.android.congress.utils.ENTRY_FROM";
	
	// types of entry into the application
	public static final String ENTRY_MAIN = "main";
	public static final String ENTRY_SHORTCUT = "shortcut";
	public static final String ENTRY_NOTIFICATION = "notification";
	
	// categories of events
	public static final String EVENT_FAVORITE = "favorites";
	public static final String EVENT_NOTIFICATION = "notifications";
	public static final String EVENT_LEGISLATOR = "legislator";
	public static final String EVENT_BILL = "bill";
	public static final String EVENT_ANALYTICS = "analytics";
	public static final String EVENT_ENTRY = "entry";
	
	// event values
	public static final String FAVORITE_ADD_LEGISLATOR = "add_legislator";
	public static final String FAVORITE_REMOVE_LEGISLATOR = "remove_legislator";
	public static final String FAVORITE_ADD_BILL = "add_bill";
	public static final String FAVORITE_REMOVE_BILL = "remove_bill";
	public static final String NOTIFICATION_ADD = "subscribe";
	public static final String NOTIFICATION_REMOVE = "unsubscribe";
	public static final String LEGISLATOR_CALL = "call";
	public static final String LEGISLATOR_WEBSITE = "website";
	public static final String LEGISLATOR_DISTRICT = "district";
	public static final String LEGISLATOR_CONTACTS = "contacts";
	public static final String BILL_SHARE = "share";
	public static final String BILL_TEXT = "text";
	public static final String BILL_OPENCONGRESS = "opencongress";
	public static final String BILL_GOVTRACK = "govtrack";
	public static final String BILL_UPCOMING = "upcoming";
	public static final String BILL_UPCOMING_MORE = "upcoming_more";
	public static final String ANALYTICS_DISABLE = "disable";
	
	 
	public static EasyTracker trackerFor(Activity activity) {
		return EasyTracker.getInstance(activity);
	}
	
	// begin the tracker for an activity - should be called only once per activity instance 
	public static EasyTracker start(Activity activity) {
		if (analyticsEnabled(activity)) {
			Log.i(Utils.TAG, "[Analytics] Tracker starting for " + activity.getLocalClassName());
			EasyTracker tracker = EasyTracker.getInstance(activity);
			tracker.activityStart(activity);
			return tracker;
		} else
			return null;
	}
	
	
	public static void event(Activity activity, String category, String action, String label) {
		EasyTracker tracker = trackerFor(activity);
		if (tracker != null && analyticsEnabled(activity)) {

//			tracker = attachCustomVars(activity, tracker);			
			Log.i(Utils.TAG, "[Analytics] Tracking event - category: " + category + ", action: " + action + ", label: " + label);
			
			tracker.send(
				MapBuilder.createEvent(category, action, label, null).build()
			);
		}
	}
	
//	public static GoogleAnalyticsTracker attachCustomVars(Activity activity, GoogleAnalyticsTracker tracker) {
//		Resources res = activity.getResources();
//		String appVersion = res.getString(R.string.app_version);
//		tracker.setCustomVar(CUSTOM_VERSION_SLOT, CUSTOM_VERSION_NAME, appVersion, SCOPE_PAGE);
//		String originalChannel = Utils.getStringPreference(activity, ORIGINAL_CHANNEL_PREFERENCE);
//		tracker.setCustomVar(CUSTOM_ORIGINAL_CHANNEL, CUSTOM_ORIGINAL_CHANNEL_NAME, originalChannel, SCOPE_PAGE);
//		String marketChannel = res.getString(R.string.market_channel);
//		tracker.setCustomVar(CUSTOM_MARKET_CHANNEL, CUSTOM_MARKET_CHANNEL_NAME, marketChannel, SCOPE_PAGE);
//		return tracker;
//	}
	
	public static void stop(Activity activity) {
		EasyTracker tracker = EasyTracker.getInstance(activity);
		if (tracker != null) {
			Log.i(Utils.TAG, "[Analytics] Tracker stopping for " + activity.getLocalClassName());
			tracker.activityStop(activity);
		}
	}
	
	public static boolean analyticsEnabled(Activity activity) {
		boolean debugDisabled = activity.getResources().getString(R.string.debug_disable_analytics).equals("true");
		boolean userEnabled = Utils.getBooleanPreference(activity, Settings.ANALYTICS_ENABLED_KEY, Settings.ANALYTICS_ENABLED_DEFAULT);
		return (!debugDisabled && userEnabled);
	}
	
	public static void addFavoriteLegislator(Activity activity, String bioguideId) {
		event(activity, EVENT_FAVORITE, FAVORITE_ADD_LEGISLATOR, bioguideId);
	}
	
	public static void removeFavoriteLegislator(Activity activity, String bioguideId) {
		event(activity, EVENT_FAVORITE, FAVORITE_REMOVE_LEGISLATOR, bioguideId);
	}
	
	public static void addFavoriteBill(Activity activity, String billId) {
		event(activity, EVENT_FAVORITE, FAVORITE_ADD_BILL, billId);
	}
	
	public static void removeFavoriteBill(Activity activity, String billId) {
		event(activity, EVENT_FAVORITE, FAVORITE_REMOVE_BILL, billId);
	}
	
	public static void subscribeNotification(Activity activity, String subscriber) {
		event(activity, EVENT_NOTIFICATION, NOTIFICATION_ADD, subscriber);
	}
	
	public static void unsubscribeNotification(Activity activity, String subscriber) {
		event(activity, EVENT_NOTIFICATION, NOTIFICATION_REMOVE, subscriber);
	}
	
	public static void legislatorCall(Activity activity, String bioguideId) {
		event(activity, EVENT_LEGISLATOR, LEGISLATOR_CALL, bioguideId);
	}
	
	public static void legislatorWebsite(Activity activity, String bioguideId) {
		event(activity, EVENT_LEGISLATOR, LEGISLATOR_WEBSITE, bioguideId);
	}
	
	public static void legislatorDistrict(Activity activity, String bioguideId) {
		event(activity, EVENT_LEGISLATOR, LEGISLATOR_DISTRICT, bioguideId);
	}
	
	public static void legislatorContacts(Activity activity, String bioguideId) {
		event(activity, EVENT_LEGISLATOR, LEGISLATOR_CONTACTS, bioguideId);
	}
	
	public static void billShare(Activity activity, String billId) {
		event(activity, EVENT_BILL, BILL_SHARE, billId);
	}
	
	public static void billText(Activity activity, String billId) {
		event(activity, EVENT_BILL, BILL_TEXT, billId);
	}
	
	public static void billOpenCongress(Activity activity, String billId) {
		event(activity, EVENT_BILL, BILL_OPENCONGRESS, billId);
	}
	
	public static void billGovTrack(Activity activity, String billId) {
		event(activity, EVENT_BILL, BILL_GOVTRACK, billId);
	}
	
	public static void billUpcomingMore(Activity activity, String sourceType) {
		event(activity, EVENT_BILL, BILL_UPCOMING_MORE, sourceType);
	}
	
	public static void billUpcoming(Activity activity, String billId) {
		event(activity, EVENT_BILL, BILL_UPCOMING, billId);
	}
	
	public static void markEntry(Activity activity, String source) {
		event(activity, EVENT_ENTRY, source, source);
	}
	
	public static void analyticsDisable(Activity activity) {
		event(activity, EVENT_ANALYTICS, ANALYTICS_DISABLE, "");
	}
	
	/** Utility function for discerning an entry source from an activity's Intent. */ 
//	public static String entrySource(Activity activity) {
//		Intent intent = activity.getIntent();
//		String action = intent.getAction();
//		boolean main = action != null && action.equals(Intent.ACTION_MAIN);
//		if (main) {
//			String source = ENTRY_MAIN;
//			Bundle extras = intent.getExtras();
//			if (extras != null) {
//				String extra = extras.getString(EXTRA_ENTRY_FROM);
//				if (extra != null)
//					source = extra;
//			}
//			return source;
//		} else
//			return null;
//	}
	
	public static Intent passEntry(Activity activity, Intent intent) {
		String action = activity.getIntent().getAction();
		if (action != null && action.equals(Intent.ACTION_MAIN)) {
			intent.setAction(Intent.ACTION_MAIN);
			intent.putExtra(Analytics.EXTRA_ENTRY_FROM, activity.getIntent().getStringExtra(Analytics.EXTRA_ENTRY_FROM));
		}
		
		return intent;
	}
	
}