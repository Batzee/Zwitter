package zwitter.batzee.com.zwitter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.twitter.sdk.android.Twitter;

import zwitter.batzee.com.zwitter.com.batzee.zwitter.adapters.SlideDrawerAdapter;

/**
 * Created by adh on 5/20/2015.
 */
public class Dashboard extends AppCompatActivity {

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

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        uTils = new Utils();
        prefs =  getSharedPreferences(uTils.SharedPrefName, MODE_PRIVATE);
        credentialStore = prefs.edit();

        userName = prefs.getString(uTils.SessionUsername, "");
        userID = prefs.getString(uTils.SessionUserID, "");
        sessionToken = prefs.getString(uTils.SessionToken, "");
        sessionSecret = prefs.getString(uTils.SessionSecret, "");

        setFloatingActionButton();
        setUpDrawerAction();

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
                    }
                    else{
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
}
