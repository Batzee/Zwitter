package zwitter.batzee.com.zwitter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import zwitter.batzee.com.zwitter.com.batzee.zwitter.adapters.NavDrawerListAdapter;
import zwitter.batzee.com.zwitter.com.batzee.zwitter.models.NavDrawerItem;

/**
 * Created by adh on 5/20/2015.
 */
public class Dashboard extends Activity {

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

    TwitterSession session;

    String sessionToken;
    String sessionSecret;
    String userName;
    String userID;

    private ArrayList<NavDrawerItem> navDrawaerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        TwitterFactory factory = new TwitterFactory();


        uTils = new Utils();
        prefs =  getSharedPreferences(uTils.SharedPrefName, MODE_PRIVATE);
        credentialStore = prefs.edit();

        userName = prefs.getString(uTils.SessionUsername, "");
        userID = prefs.getString(uTils.SessionUserID, "");
        sessionToken = prefs.getString(uTils.SessionToken, "");
        sessionSecret = prefs.getString(uTils.SessionSecret, "");



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
               // createATweet();
                new RetrieveFeedTask().execute();
            }
        });

/*
        session = Twitter.getSessionManager().getActiveSession();
        TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;

        String name =session.getUserName();
        Toast.makeText(Dashboard.this,"Welcome "+name, Toast.LENGTH_SHORT);

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
        StatusesService statusesService = twitterApiClient.getStatusesService();
*/
    }

    public void selectedItem(int position){

        if(position == 1 ){
            Toast.makeText(Dashboard.this, "LogOut", Toast.LENGTH_SHORT);
            Log.d("LIST CLICK", position + "");

           // Twitter.getInstance();
           // Twitter.logOut();

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

    class RetrieveFeedTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;

        protected Integer doInBackground(String... urls) {

                AccessToken a = new AccessToken(sessionToken, sessionSecret);
                Twitter twitter = new TwitterFactory().getInstance();
                twitter.setOAuthConsumer(uTils.ApiKey, uTils.ApiSecret);
                twitter.setOAuthAccessToken(a);
            try {
                twitter.updateStatus("This Tweet is generated from my Own Twitter app Zwitter - App is in testing Phase");
                Toast.makeText(Dashboard.this, "Successfully Tweeted", Toast.LENGTH_SHORT).show();
            } catch (twitter4j.TwitterException e) {
                e.printStackTrace();
                Toast.makeText(Dashboard.this, "Tweet Failed", Toast.LENGTH_SHORT).show();
            }
            return 1;
            }

        }

}
