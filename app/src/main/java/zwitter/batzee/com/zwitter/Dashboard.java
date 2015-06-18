package zwitter.batzee.com.zwitter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

import zwitter.batzee.com.zwitter.com.batzee.zwitter.adapters.SlideDrawerAdapter;
import zwitter.batzee.com.zwitter.com.batzee.zwitter.adapters.ViewPagerAdapter;

/**
 * Created by adh on 5/20/2015.
 */
public class Dashboard extends AppCompatActivity  {

    Config uTils;
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
    int PROFILE = R.drawable.ztweet;    //String profileURL = "" ;
    String profileURL;
    String backgroundImage;

    int followers;
    int following;
    int favs;

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

    ActivityOptions opts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);


        if(isNetworkAvailable()){

        }
        else{
            noDataAlert();
        }

        uTils = new Config();
        prefs = getSharedPreferences(uTils.SharedPrefName, MODE_PRIVATE);
        credentialStore = prefs.edit();

        userName = prefs.getString(uTils.SessionUsername, "");
        userID = prefs.getString(uTils.SessionUserID, "");
        sessionToken = prefs.getString(uTils.SessionToken, "");
        sessionSecret = prefs.getString(uTils.SessionSecret, "");
        profileURL = prefs.getString(uTils.SessionProfileImage, "https://pbs.twimg.com/profile_images/378800000532546226/dbe5f0727b69487016ffd67a6689e75a_400x400.jpeg");
        NAME = prefs.getString(uTils.namefromServer, "Zwitter");
        EMAIL = prefs.getString(uTils.descfromServer, "help@zwitter.com");
        followers = prefs.getInt(uTils.Follower, 0);
        following =  prefs.getInt(uTils.Following, 0);
        favs= prefs.getInt(uTils.Favs, 0);


        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        setUpTabs();
        setFloatingActionButton();
        setUpDrawerAction();

        Window window = getWindow();
        View v = window.getDecorView();
        opts = ActivityOptions.makeScaleUpAnimation(v, v.getWidth(), v.getHeight(),v.getWidth(), v.getHeight());

    }


    private void setUpTabs() {

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging te Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Cushtom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        Log.d("Tabs", "Set");

    }

    private void setUpDrawerAction() {

        Log.d("Recycle Adapter", "Set");

        LinearLayoutManager layoutManager = new LinearLayoutManager(Dashboard.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);// Letting the system know that the list objects are of fixed size
        mAdapter = new SlideDrawerAdapter(TITLES,ICONS,userName,EMAIL,profileURL, followers, following, favs);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        mRecyclerView.setAdapter(mAdapter);

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
                        Log.d("LogOut","Trigger 1");
                        logOutAction();
                    }
                    else if(recyclerView.getChildPosition(child) == 1){
                       // Drawer.closeDrawers();
                    }else {
                       // Toast.makeText(Dashboard.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
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
        Log.d("LogOut","Trigger 2");
        logOutAlert();

    }

    private void logOut(){
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
        final FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setBackgroundDrawable(R.drawable.action_selector)
                .build();
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tweet = new Intent(Dashboard.this, TweetingActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(Dashboard.this, actionButton, Dashboard.this.getString(R.string.transition_avatar));
                startActivity(tweet, activityOptions.toBundle());
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
                Intent tweet =  new Intent(Dashboard.this,TweetingActivity.class);
                startActivity(tweet);
            }
        });
        */
    }

    /*
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
*/
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void noDataAlert() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Dashboard.this);
        alertDialogBuilder.setTitle("No active network found");

        alertDialogBuilder
                .setMessage(
                        "This application needs active internet connection to interact with your twitter feeds, connect to a network and try again")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Dashboard.this.finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void logOutAlert() {

        Log.d("LogOut","Trigger 3");

        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Areyou sure you want to logout?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }


}
