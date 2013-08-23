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
	//  * remove tracker from event calls, look up tracker as needed from fragment
	
	// scope constants as used in Google Analytics
//	public static final int SCOPE_VISITOR = 1;
//	public static final int SCOPE_SESSION = 2;
//	public static final int SCOPE_PAGE = 3;
	
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
	
//	private static final String FRAGMENT_TAG = "com.sunlightlabs.android.congress.utils.Analytics.analytics";
	
	// wrapper fragment for a GoogleAnalyticsTracker, manages its own state across activity restarts
//	public static class TrackerFragment extends Fragment {
//		public EasyTracker tracker;
//		
//		@Override
//		public void onDetach() {
//			super.onDetach();
//			Analytics.stop(tracker);
//		}
//		
//		public TrackerFragment() {}
//	}
	
	// simple convenience function for when a fragment wants to log a URL
	// don't need to worry about making sure it only happens once, because Fragments manage their own state
	// in a way that activities do not.
	// TODO: is this needed?
//	public static void track(Fragment fragment, String url) {
//		if (fragment.isAdded()) {
//			GoogleAnalyticsTracker tracker = start(fragment.getActivity());
//			page(fragment.getActivity(), tracker, url);
//			stop(tracker);
//		}
//	}
	
	// Convenience function.
	// Will take care of logging the pageview, and then *not* logging it if the activity restarts because of a screen flip.
	// Uses a fragment's presence as a marker of whether the initial pageview has been tracked already,
	// and uses it as a holder for the tracker itself, returning it and replacing it when the activity changes.
	// Finally, also uses its lifecycle as a way to automatically stop the tracker.
	
//	public static TrackerFragment track(FragmentActivity activity, String url) {
//		if (analyticsEnabled(activity)) {
//			
//			// I initialize the tracker here and call start(), instead of inside the fragment using callbacks,
//			// so that we can log the inaugural pageview here, where we know whether it's the first time it happened
//			FragmentManager manager = activity.getSupportFragmentManager();
//			TrackerFragment fragment = (TrackerFragment) manager.findFragmentByTag(FRAGMENT_TAG);
//			if (fragment == null) {
//				fragment = new TrackerFragment();
//				fragment.setRetainInstance(true);
//				manager.beginTransaction().add(fragment, FRAGMENT_TAG).commit();
//				
//				// initialize the tracker immediately, so it can be used by the caller
//				fragment.tracker = start(activity);
//				
//				// if this was used as a convenience function in an activity, 
//				// log the pageview only once, when the fragment is first created
//				//TODO: this can safely be removed, we don't do URLs now
////				if (url != null)
////					page(activity, fragment.tracker, url);
//			} 
//			
//			// if this was used as a convenience function in an activity, reassign and restart 
//			// the tracker since we'll assume that this is the first call after the activity was restarted
//			else if (url != null) 
//				fragment.tracker = start(activity);
//			
//			// otherwise, leave the fragment alone, nothing to do here
//			
//			return fragment;
//		} else
//			return null;
//	}
	 
	public static EasyTracker trackerFor(Activity activity) {
		return EasyTracker.getInstance(activity);
	}
	
	// begin the tracker for an activity - should be called only once per activity instance 
	public static EasyTracker start(Activity activity) {
		if (analyticsEnabled(activity)) {
			Log.i(Utils.TAG, "[Analytics] Tracker starting for " + activity.getLocalClassName());
			EasyTracker tracker = EasyTracker.getInstance(activity);
			tracker.activityStart(activity);
//			GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
//			String code = activity.getResources().getString(R.string.ga_trackingId);
//			tracker.startNewSession(code, activity);
			return tracker;
		} else
			return null;
	}
	
	
//	public static void page(FragmentActivity activity, String page) {
//		page(activity, page, true);
//	}
//	
//	public static void page(FragmentActivity activity, String page, boolean checkEntry) {
//		page(activity, trackerFor(activity), page, checkEntry);
//	}
//	
//	public static void page(Activity activity, EasyTracker tracker, String page) {
//		page(activity, tracker, page, true);
//	}
//	
//	public static void page(Activity activity, EasyTracker tracker, String page, boolean checkEntry) {
//		if (tracker != null && analyticsEnabled(activity)) {
//			
//			if (checkEntry) {
//				String source = entrySource(activity);
//				if (source != null) {
//					Log.i(Utils.TAG, "[Analytics] Marking next page view as an entry to the app of type: " + source);
//					
//					markEntry(activity, tracker, source);
//				}
//			}
//			
//			// TODO: custom vars to hits
////			tracker = attachCustomVars(activity, tracker);
//			
//			Log.i(Utils.TAG, "[Analytics] Tracking page - " + page);
//			//tracker.setDebug(true);
//			tracker.setAnonymizeIp(true);
//			tracker.trackPageView(page);
//			tracker.dispatch();
//		}
//	}
		
	public static void event(Activity activity, String category, String action, String label) {
		event(activity, trackerFor(activity), category, action, label);
	}
	
	public static void event(Activity activity, EasyTracker tracker, String category, String action, String label) {
		if (tracker != null && analyticsEnabled(activity)) {
			// TODO: custom variables on events
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
		event(activity, trackerFor(activity), EVENT_FAVORITE, FAVORITE_ADD_LEGISLATOR, bioguideId);
	}
	
	public static void removeFavoriteLegislator(Activity activity, String bioguideId) {
		event(activity, trackerFor(activity), EVENT_FAVORITE, FAVORITE_REMOVE_LEGISLATOR, bioguideId);
	}
	
	public static void addFavoriteBill(Activity activity, String billId) {
		event(activity, trackerFor(activity), EVENT_FAVORITE, FAVORITE_ADD_BILL, billId);
	}
	
	public static void removeFavoriteBill(Activity activity, String billId) {
		event(activity, trackerFor(activity), EVENT_FAVORITE, FAVORITE_REMOVE_BILL, billId);
	}
	
	public static void subscribeNotification(Activity activity, EasyTracker tracker, String subscriber) {
		event(activity, tracker, EVENT_NOTIFICATION, NOTIFICATION_ADD, subscriber);
	}
	
	public static void unsubscribeNotification(Activity activity, EasyTracker tracker, String subscriber) {
		event(activity, tracker, EVENT_NOTIFICATION, NOTIFICATION_REMOVE, subscriber);
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
		event(activity, trackerFor(activity), EVENT_BILL, BILL_SHARE, billId);
	}
	
	public static void billText(Activity activity, String billId) {
		event(activity, trackerFor(activity), EVENT_BILL, BILL_TEXT, billId);
	}
	
	public static void billOpenCongress(Activity activity, String billId) {
		event(activity, trackerFor(activity), EVENT_BILL, BILL_OPENCONGRESS, billId);
	}
	
	public static void billGovTrack(Activity activity, String billId) {
		event(activity, trackerFor(activity), EVENT_BILL, BILL_GOVTRACK, billId);
	}
	
	public static void billUpcomingMore(Activity activity, String sourceType) {
		event(activity, trackerFor(activity), EVENT_BILL, BILL_UPCOMING_MORE, sourceType);
	}
	
	public static void billUpcoming(Activity activity, String billId) {
		event(activity, trackerFor(activity), EVENT_BILL, BILL_UPCOMING, billId);
	}
	
	public static void markEntry(Activity activity, EasyTracker tracker, String source) {
		event(activity, tracker, EVENT_ENTRY, source, source);
	}
	
	public static void analyticsDisable(Activity activity) {
		event(activity, trackerFor(activity), EVENT_ANALYTICS, ANALYTICS_DISABLE, "");
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
	
	// TODO: I think this is dead, with EasyTracker?
	public static Intent passEntry(Activity activity, Intent intent) {
		String action = activity.getIntent().getAction();
		if (action != null && action.equals(Intent.ACTION_MAIN)) {
			intent.setAction(Intent.ACTION_MAIN);
			intent.putExtra(Analytics.EXTRA_ENTRY_FROM, activity.getIntent().getStringExtra(Analytics.EXTRA_ENTRY_FROM));
		}
		
		return intent;
	}
	
}