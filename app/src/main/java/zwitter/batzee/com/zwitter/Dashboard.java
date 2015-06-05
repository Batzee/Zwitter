package zwitter.batzee.com.zwitter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import retrofit.http.GET;
import retrofit.http.Query;
import zwitter.batzee.com.zwitter.com.batzee.zwitter.adapters.SlideDrawerAdapter;
import zwitter.batzee.com.zwitter.com.batzee.zwitter.adapters.ViewPagerAdapter;

/**
 * Created by adh on 5/20/2015.
 */
public class Dashboard extends AppCompatActivity  {

    Utils uTils;
    SharedPreferences.Editor credentialStore;
    SharedPreferences prefs;

    String sessionToken;
    String sessionSecret;
    String userName;
    String userID;

    //Titles And Icons For Our Navigation Drawer List
    String TITLES[] = {"Home","Logout"};
    int ICONS[] = {R.drawable.minihome,R.drawable.dna};
    //header data
    String NAME = "Zwitter";
    String EMAIL = "help@zwitter.com";
    int PROFILE = R.drawable.ztweet;
    String profileURL = "";

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Time line", "Search"};
    int Numboftabs =2;
    TwitterSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

       // session = Twitter.getSessionManager() .getActiveSession();

        uTils = new Utils();
        prefs = getSharedPreferences(uTils.SharedPrefName, MODE_PRIVATE);
        credentialStore = prefs.edit();

        userName = prefs.getString(uTils.SessionUsername, "");
        userID = prefs.getString(uTils.SessionUserID, "");
        sessionToken = prefs.getString(uTils.SessionToken, "");
        sessionSecret = prefs.getString(uTils.SessionSecret, "");

        setUpTabs();
        setUpDrawerAction();
        setFloatingActionButton();

     //   getUserImage();

    }

    private void getUserImage() {



        new MyTwitterApiClient(session).getUsersService().show(12L, null, true,
                new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        Log.d("twittercommunity", "user's profile url is "
                                + result.data.profileImageUrlHttps);
                        EMAIL = result.data.profileImageUrlHttps;
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.d("twittercommunity", "exception is " + exception);
                    }
                });
    }

    private void setUpTabs() {

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

    }

    private void setUpDrawerAction() {

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new SlideDrawerAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        final GestureDetector mGestureDetector = new GestureDetector(Dashboard.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {

                return true;
            }

        });


        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    Drawer.closeDrawers();

                    if (recyclerView.getChildPosition(child) == 2) {
                        logOutAction();
                    } else {
                        Toast.makeText(Dashboard.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //when drawer Opens
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //When drawer closed
            }

        };
        // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();
    }

    private void logOutAction() {

        Twitter.getInstance();
        Twitter.logOut();

        credentialStore.putString(uTils.SessionToken, "");
        credentialStore.putString(uTils.SessionSecret, "");
        credentialStore.commit();

        Toast.makeText(Dashboard.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
        finish();
        Intent login =  new Intent(Dashboard.this,MainActivity.class);
        startActivity(login);
    }

    private void setFloatingActionButton() {

        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.ztweetlogo);
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setBackgroundDrawable(R.drawable.action_selector)
                .build();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tweet =  new Intent(Dashboard.this,Tweet.class);
                startActivity(tweet);
            }
        });

        /*
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
       // repeat many times:
        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageResource(R.drawable.home);
        SubActionButton tweetButton = itemBuilder.setContentView(itemIcon).build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(tweetButton)
                .attachTo(actionButton)
                .build();

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tweet =  new Intent(Dashboard.this,Tweet.class);
                startActivity(tweet);
            }
        });
        */
    }

    class MyTwitterApiClient extends TwitterApiClient {
        public MyTwitterApiClient(TwitterSession session) {
            super(session);
        }

        public UsersService getUsersService() {

            return getService(UsersService.class);
        }
    }

    interface UsersService {
        @GET("/1.1/users/show.json")
        void show(@Query("user_id") Long userId,
                  @Query("screen_name") String screenName,
                  @Query("include_entities") Boolean includeEntities,
                  Callback<User> cb);
    }
}
