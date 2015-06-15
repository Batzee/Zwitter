package zwitter.batzee.com.zwitter.com.batzee.zwitter.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by adh on 6/9/2015.
 */
public class TwitterResourceService extends IntentService {

    String sesiionToken;
    String sessionSecret;
    String apiKey;
    String apiSecret;




    int result;

    public TwitterResourceService() {
        super("TwitterResourceService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle DatafromLogin = intent.getExtras();

        sesiionToken = DatafromLogin.getString("sessionToken");
        sessionSecret = DatafromLogin.getString("sessionSecret");
        apiKey = DatafromLogin.getString("apikey");
        apiSecret = DatafromLogin.getString("apisecret");



        AccessToken a = new AccessToken(sesiionToken, sessionSecret);
        twitter4j.Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(apiKey, apiSecret);
        twitter.setOAuthAccessToken(a);
        twitter4j.User user = null;
        try {
            user = twitter.showUser(twitter.getScreenName());
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        //String profileURL = user.getProfileImageURL();
        String profileURL = user.getProfileImageURL();
        String backgroundURL = user.getProfileBackgroundImageURL();
        String name = user.getName();
        String desc = user.getDescription();
        int followers=user.getFollowersCount();
        int following=user.getFriendsCount();
        int favourites=user.getFavouritesCount();


        result = Activity.RESULT_OK;

        Bundle TwitterBundle = intent.getExtras();
        TwitterBundle.putString("profileImage", profileURL);
        TwitterBundle.putString("backgroundImage", backgroundURL);
        TwitterBundle.putString("profileName", name);
        TwitterBundle.putString("profileDesc", desc);
        TwitterBundle.putInt("follower", followers);
        TwitterBundle.putInt("following", following);
        TwitterBundle.putInt("favs", favourites);


        if (TwitterBundle != null) {
            Messenger LoginMessenger = (Messenger) TwitterBundle
                    .get("TWITTERRESMESSENGER");
            Message TwitterMessage = Message.obtain();
            TwitterMessage.arg1 = result;
            TwitterMessage.setData(TwitterBundle);
            try {
                LoginMessenger.send(TwitterMessage);
            } catch (android.os.RemoteException e1) {
                Log.w("Message Not Sent", "Message Not Sent");
            }
        }


    }
}
