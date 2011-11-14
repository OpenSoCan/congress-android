package com.sunlightlabs.android.congress;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.sunlightlabs.android.congress.fragments.LegislatorProfileFragment;
import com.sunlightlabs.android.congress.fragments.NewsListFragment;
import com.sunlightlabs.android.congress.fragments.TweetsFragment;
import com.sunlightlabs.android.congress.fragments.YouTubeFragment;
import com.sunlightlabs.android.congress.utils.Analytics;
import com.sunlightlabs.android.congress.utils.Database;
import com.sunlightlabs.android.congress.utils.TitlePageAdapter;
import com.sunlightlabs.android.congress.utils.Utils;
import com.sunlightlabs.congress.models.Legislator;

public class LegislatorPager extends FragmentActivity {
	private Legislator legislator;
	private String tab;
	private Database database;
	private Cursor cursor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pager_titled);
		
		Bundle extras = getIntent().getExtras();
		legislator = (Legislator) extras.getSerializable("legislator");
		tab = extras.getString("tab");
		
		Analytics.track(this, "/legislator?bioguide_id=" + legislator.id);
		
		setupDatabase();
		setupControls();
		setupPager();	
	}
	
	private void setupPager() {
		TitlePageAdapter adapter = new TitlePageAdapter(this);
		adapter.add("info", R.string.tab_profile, LegislatorProfileFragment.create(legislator));
		adapter.add("news", R.string.tab_news, NewsListFragment.forLegislator(legislator));
		
		if (legislator.twitter_id != null && !legislator.twitter_id.equals(""))
			adapter.add("tweets", R.string.tab_tweets, TweetsFragment.create(legislator));
		
		if (legislator.youtube_url != null && !legislator.youtube_url.equals(""))
			adapter.add("videos", R.string.tab_videos, YouTubeFragment.create(legislator));
		
		if (tab != null) adapter.selectPage(tab);
	}
	
	private void setupDatabase() {
		database = new Database(this);
		database.open();
		cursor = database.getLegislator(legislator.getId());
		startManagingCursor(cursor);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}
	
	public void setupControls() {
		String titledName = legislator.titledName();
		Utils.setTitle(this, titledName);
		if (titledName.length() >= 23)
			Utils.setTitleSize(this, 19);
		
		Utils.setActionButton(this, R.id.action_1, R.drawable.star_off, new View.OnClickListener() {
			public void onClick(View v) { 
				toggleDatabaseFavorite(); 
			}
		});
		
		toggleFavoriteStar(cursor.getCount() == 1);
	}

	private void toggleFavoriteStar(boolean enabled) {
		if (enabled)
			Utils.setActionIcon(this, R.id.action_1, R.drawable.star_on);
		else
			Utils.setActionIcon(this, R.id.action_1, R.drawable.star_off);
	}

	private void toggleDatabaseFavorite() {
		String id = legislator.getId();
		cursor.requery();
		if (cursor.getCount() == 1) {
			if (database.removeLegislator(id) != 0) {
				toggleFavoriteStar(false);
				Analytics.removeFavoriteLegislator(this, id);
			} else
				Utils.alert(this, "Problem unstarring legislator.");
		} else {
			if (database.addLegislator(legislator) != -1) {
				toggleFavoriteStar(true);
				Analytics.addFavoriteLegislator(this, id);
			} else
				Utils.alert(this, "Problem starring legislator.");
		}
	}
}