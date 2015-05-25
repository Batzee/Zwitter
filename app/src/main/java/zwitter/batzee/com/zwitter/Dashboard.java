package zwitter.batzee.com.zwitter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.services.StatusesService;

/**
 * Created by adh on 5/20/2015.
 */
public class Dashboard extends Activity {

    ImageButton Zweet;
    TwitterApiClient twitterApiClient;
    StatusesService statusService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

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

    }

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
