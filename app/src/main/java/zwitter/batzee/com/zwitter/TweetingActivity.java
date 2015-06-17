package zwitter.batzee.com.zwitter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by adh on 5/20/2015.
 */
public class TweetingActivity extends Activity {

    Config uTils;
    SharedPreferences.Editor credentialStore;
    SharedPreferences prefs;

    EditText tweetText;
    ImageButton tweetButton;
    String tweetableText;

    String sessionToken;
    String sessionSecret;
    String userName;
    String userID;
    int zweetSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweet);

        zweetSize = 123;
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
                splitTweet(tweetableText);
                tweetButton.setEnabled(false);
                hideKeyBoard();
            }
        });

    }

    private void splitTweet(String text) {

        int text_count = text.length();
        int no_of_tweets = text_count/zweetSize;
        int mod_tweet = text_count%zweetSize;

      //  ArrayList<String> splitTweets = new ArrayList<>();
      //  List<String> l = Arrays.<String>asList(text.split("(?<=\\G.{132})"));
       // splitTweets = new ArrayList<String>(l);

        if(mod_tweet == 0){
            String[] num = new String[no_of_tweets];
            for(int i = 0, x=0, y=zweetSize; i<num.length; i++){
                num[i]  = text.substring(x,y);
                x += zweetSize;
                y += zweetSize;
            }

            new RetrieveFeedTask().execute(num);
        }
        else{
            String[] num = new String[no_of_tweets+1];
            for(int i = 0, x=0, y=zweetSize; i<num.length; i++){

                if(i == num.length-2){
                    num[i]  = text.substring(x,y);
                    x += zweetSize;
                    y += mod_tweet;
                }
                else if(i == no_of_tweets){
                    num[i]  = text.substring(x,y);
                }
                else{
                    num[i]  = text.substring(x,y);
                    x += zweetSize;
                    y += zweetSize;
                }
            }
            new RetrieveFeedTask().execute(num);
        }

    }

    private void hideKeyBoard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void initializeSharedPref() {

        uTils = new Config();
        prefs =  getSharedPreferences(uTils.SharedPrefName, MODE_PRIVATE);
        credentialStore = prefs.edit();

        userName = prefs.getString(uTils.SessionUsername, "");
        userID = prefs.getString(uTils.SessionUserID, "");
        sessionToken = prefs.getString(uTils.SessionToken, "");
        sessionSecret = prefs.getString(uTils.SessionSecret, "");
    }

    class RetrieveFeedTask extends AsyncTask<String[], Void, String[]> {

        String[] result;
        private Exception exception;

        protected String[] doInBackground(String[]... params) {

            String[] passed = params[0];

            int totalPage =passed.length;
            Log.d("No of Tweets",totalPage+"");

            AccessToken a = new AccessToken(sessionToken, sessionSecret);
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(uTils.ApiKey, uTils.ApiSecret);
            twitter.setOAuthAccessToken(a);
            try {

                for(int i = 0; i<totalPage ; i++) {
                    int current_page= i+1;
                    twitter.updateStatus(passed[i].toString()+" #zwitter ["+current_page+"/"+totalPage+"]");
                }

                TweetingActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(TweetingActivity.this, "Done", Toast.LENGTH_SHORT).show();
                        tweetButton.setEnabled(true);
                    }
                });

                finish();
            } catch (twitter4j.TwitterException e) {
                e.printStackTrace();
               Toast.makeText(TweetingActivity.this, "TweetingActivity Failed", Toast.LENGTH_SHORT).show();
                tweetButton.setEnabled(true);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            super.onPostExecute(result);
            //Set the result array to what ever object u want to
        }

    }

}
