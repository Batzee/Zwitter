package zwitter.batzee.com.zwitter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by adh on 5/20/2015.
 */
public class Tweet extends Activity {

    Utils uTils;
    SharedPreferences.Editor credentialStore;
    SharedPreferences prefs;

    EditText tweetText;
    ImageButton tweetButton;
    String tweetableText;

    String sessionToken;
    String sessionSecret;
    String userName;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweet);

        initializeSharedPref();

        tweetText = (EditText) findViewById(R.id.tweetText);
        tweetButton = (ImageButton) findViewById(R.id.tweetButton);

        /*
        tweetButton.setImageDrawable(getResources().getDrawable(R.drawable.ztweetlogo));
        tweetButton.setBackgroundResource(R.drawable.action_selector);
        */

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweetableText = tweetText.getText().toString();
                new RetrieveFeedTask().execute();
                tweetButton.setEnabled(false);
            }
        });

    }

    private void initializeSharedPref() {

        uTils = new Utils();
        prefs =  getSharedPreferences(uTils.SharedPrefName, MODE_PRIVATE);
        credentialStore = prefs.edit();

        userName = prefs.getString(uTils.SessionUsername, "");
        userID = prefs.getString(uTils.SessionUserID, "");
        sessionToken = prefs.getString(uTils.SessionToken, "");
        sessionSecret = prefs.getString(uTils.SessionSecret, "");
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, Integer> {

        private Exception exception;

        protected Integer doInBackground(String... urls) {

            AccessToken a = new AccessToken(sessionToken, sessionSecret);
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(uTils.ApiKey, uTils.ApiSecret);
            twitter.setOAuthAccessToken(a);
            try {
               // twitter.updateStatus("This Tweet is generated from my Own Twitter app Zwitter - App is in testing Phase");
                twitter.updateStatus(tweetableText);

                Tweet.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Tweet.this, "Successfully Tweeted", Toast.LENGTH_SHORT).show();
                        tweetButton.setEnabled(true);
                    }
                });

                finish();
            } catch (twitter4j.TwitterException e) {
                e.printStackTrace();
               Toast.makeText(Tweet.this, "Tweet Failed", Toast.LENGTH_SHORT).show();
                tweetButton.setEnabled(true);
            }
            return 1;
        }

    }

}
