package zwitter.batzee.com.zwitter;

import android.app.Application;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;

/**
 * Created by adh on 5/19/2015.
 */
public class App extends Application {

    private static App singleton;
    private TwitterAuthConfig authConfig;
    private static Config config;

    public static App getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        config = new Config();

        authConfig = new TwitterAuthConfig(config.ApiKey, config.ApiSecret);
        Fabric.with(this, new Twitter(authConfig),new TweetComposer(),new TwitterCore(authConfig));
        Log.d("App", "Success");

    }

}



