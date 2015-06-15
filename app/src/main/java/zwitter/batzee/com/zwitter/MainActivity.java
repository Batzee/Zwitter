package zwitter.batzee.com.zwitter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import zwitter.batzee.com.zwitter.com.batzee.zwitter.service.TwitterResourceService;

public class MainActivity extends Activity {

    TwitterLoginButton loginButton;
    SharedPreferences prefs;
    SharedPreferences.Editor credentialStore;
    Config config;

    String token;
    String secret;

    String sessionToken;
    Intent dashBoard;

    ProgressBar proBar;
    ImageView appIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        proBar = (ProgressBar) findViewById(R.id.progressBar);
        proBar.setVisibility(View.INVISIBLE);

        appIcon =  (ImageView)findViewById(R.id.imageView);

        config = new Config();
        dashBoard = new Intent(MainActivity.this, Dashboard.class);
        prefs =  getSharedPreferences(config.SharedPrefName, MODE_PRIVATE);
        credentialStore = prefs.edit();

        sessionToken = prefs.getString(config.SessionToken,"");

        if(!(sessionToken.equals(""))){
            finish();
            startActivity(dashBoard);
        }


        loginButton = (TwitterLoginButton)findViewById(R.id.login_button);

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d("Callback Results","Success - "+result);

                //TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterSession session = result.data;
                Long userID = session.getUserId();
                String userName = session.getUserName();

                TwitterAuthToken authToken = session.getAuthToken();
                token = authToken.token;
                secret = authToken.secret;

                credentialStore.putString(config.SessionToken,token);
                credentialStore.putString(config.SessionSecret, secret);
                credentialStore.putString(config.SessionUserID,userID+"");
                credentialStore.putString(config.SessionUsername, userName);
                credentialStore.commit();

                proBar.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);
                appIcon.setVisibility(View.INVISIBLE);

                getTwitterResource();


                Log.d("After Allowing", "token: "+ token+",secret: "+secret);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("Callback Results","fail - "+exception);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void getTwitterResource() {

        Bundle twitterServiceData = new Bundle();
        Messenger TwitterResMessenger = new Messenger(TwitterDataHandler);
        Intent loginIntent = new Intent(this, TwitterResourceService.class);

        twitterServiceData.putString("sessionToken",token );
        twitterServiceData.putString("sessionSecret", secret);
        twitterServiceData.putString("apikey", config.ApiKey);
        twitterServiceData.putString("apisecret", config.ApiSecret);

        loginIntent.putExtras(twitterServiceData);
        loginIntent.putExtra("TWITTERRESMESSENGER", TwitterResMessenger);

        try {
            startService(loginIntent);
        } catch (Exception e) {
            Log.d("Service Exception", e.toString());
        }
    }

    private final Handler TwitterDataHandler = new Handler() {
        @Override
        public void handleMessage(Message Twittermessage) {

            Bundle ReceivedData = Twittermessage.getData();
            String profileImagefromServer = ReceivedData.getString("profileImage");
            String backgroundImagefromServer = ReceivedData.getString("backgroundImage");
            String namefromServer = ReceivedData.getString("profileName");
            String descfromServer = ReceivedData.getString("profileDesc");

            int getfollower = ReceivedData.getInt("follower");
            int getFollowing = ReceivedData.getInt("following");
            int getFavs = ReceivedData.getInt("favs");


            credentialStore.putString(config.SessionProfileImage,profileImagefromServer);
            credentialStore.putString(config.SessionBackgroundImage,backgroundImagefromServer);
            credentialStore.putString(config.namefromServer,namefromServer);
            credentialStore.putString(config.descfromServer,descfromServer);
            credentialStore.putInt(config.Follower, getfollower);
            credentialStore.putInt(config.Following, getFollowing);
            credentialStore.putInt(config.Favs, getFavs);

            credentialStore.commit();

            proBar.setVisibility(View.INVISIBLE);

            startActivity(dashBoard);

        };
    };

}
