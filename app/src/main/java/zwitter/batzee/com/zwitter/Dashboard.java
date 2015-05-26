package zwitter.batzee.com.zwitter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.ArrayList;

import zwitter.batzee.com.zwitter.com.batzee.zwitter.adapters.NavDrawerListAdapter;
import zwitter.batzee.com.zwitter.com.batzee.zwitter.models.NavDrawerItem;

/**
 * Created by adh on 5/20/2015.
 */
public class Dashboard extends ActionBarActivity {

    Utils uTils;
    SharedPreferences.Editor credentialStore;
    SharedPreferences prefs;

    ImageButton Zweet;
    TwitterApiClient twitterApiClient;
    StatusesService statusService;

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private DrawerLayout mdrawerLayout;
    private ListView mDrawerList;

    private ArrayList<NavDrawerItem> navDrawaerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        //twitterApiClient = TwitterCore.getInstance().getApiClient(Twitter.getSessionManager().getActiveSession());

        /*
        twitterApiClient = TwitterCore.getInstance().getApiClient();
        statusService = twitterApiClient.getStatusesService();
*/
/*
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        session = Twitter.getSessionManager().getActiveSession();
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
        StatusesService statusesService = twitterApiClient.getStatusesService();
*/
        /*
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        String name =session.getUserName();
        Toast.makeText(Dashboard.this,"Welcome "+name, Toast.LENGTH_SHORT);
        */

        uTils = new Utils();
        prefs =  getSharedPreferences(uTils.SharedPrefName, MODE_PRIVATE);
        credentialStore = prefs.edit();

        Zweet = (ImageButton) findViewById(R.id.zweetButton);

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

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedItem(position);
            }
        });

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
    }

    public void selectedItem(int position){

        if(position == 1 ){
            Toast.makeText(Dashboard.this, "LogOut", Toast.LENGTH_SHORT);
            Log.d("LIST CLICK", position + "");
/*
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();
            Twitter.getSessionManager().clearActiveSession();
            Twitter.logOut();
  */


            Twitter.getInstance();
            Twitter.logOut();

            credentialStore.putString(uTils.SessionToken, "");
            credentialStore.putString(uTils.SessionSecret, "");
            credentialStore.commit();

            finish();
            Intent login =  new Intent(Dashboard.this,MainActivity.class);
            startActivity(login);

        }
        else if(position == 0){
            Toast.makeText(Dashboard.this,"Home",Toast.LENGTH_SHORT);
            Log.d("LIST CLICK", position + "");
        }


    }

    public void createATweet(){

        statusService.update("Hello from Zwitter",null,null,null,null,"hello2",true,true, new Callback<com.twitter.sdk.android.core.models.Tweet>() {
            @Override
            public void success(Result<com.twitter.sdk.android.core.models.Tweet> result) {
              Log.d("TWEET", "POSTED");
                Toast.makeText(Dashboard.this, "Tweet Sent", Toast.LENGTH_SHORT);
            }

        public void failure(TwitterException exception) {
             Log.d("TWEET", "FAILED");
            exception.printStackTrace();
            Toast.makeText(Dashboard.this, "Tweet Failed", Toast.LENGTH_SHORT);
           }
         }
        );
    }
}
