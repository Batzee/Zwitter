package zwitter.batzee.com.zwitter;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.ArrayList;

import zwitter.batzee.com.zwitter.com.batzee.zwitter.adapters.NavDrawerListAdapter;
import zwitter.batzee.com.zwitter.com.batzee.zwitter.models.NavDrawerItem;

/**
 * Created by adh on 5/20/2015.
 */
public class Dashboard extends ActionBarActivity {

    ImageButton Zweet;
    TwitterApiClient twitterApiClient;
    StatusesService statusService;

    private DrawerLayout mdrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;


    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawaerItems;
    private NavDrawerListAdapter adapter;

    android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        actionBar = getSupportActionBar();



        //twitterApiClient = TwitterCore.getInstance().getApiClient(Twitter.getSessionManager().getActiveSession());
        twitterApiClient = TwitterCore.getInstance().getApiClient();
        statusService = twitterApiClient.getStatusesService();

        Zweet = (ImageButton) findViewById(R.id.zweetButton);

        Zweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent tweet =  new Intent(Dashboard.this, Tweet.class);
                startActivity(tweet);
                */
                createATweet();
            }
        });

        mTitle = mDrawerTitle = getTitle();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mdrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawaerItems = new ArrayList<NavDrawerItem>();

        navDrawaerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawaerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));

        navMenuIcons.recycle();

        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawaerItems);
        mDrawerList.setAdapter(adapter);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

/*
        mDrawerToggle = new ActionBarDrawerToggle(this, mdrawerLayout, actionBar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }

    };

*/
        mdrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
           // displayView(0);
        }



    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        boolean drawerOpen = mdrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title){
        mTitle = title;
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
      //  mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
      //  mDrawerToggle.onConfigurationChanged(newConfig);
    }

*/

    public void createATweet(){

        statusService.update("Hello from Zwitter",null,null,null,null,"hello2",true,true, new Callback<com.twitter.sdk.android.core.models.Tweet>() {
            @Override
            public void success(Result<com.twitter.sdk.android.core.models.Tweet> result) {
              Log.d("TWEET", "POSTED");
            }

        public void failure(TwitterException exception) {
             Log.d("TWEET", "FAILED");
           }
         }
        );
    }
}
