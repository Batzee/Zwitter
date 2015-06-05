package zwitter.batzee.com.zwitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;


/**
 * Created by adh on 6/4/2015.
 */
public class TweetTimeLine extends ListFragment {

    Utils uTils;
    SharedPreferences.Editor credentialStore;
    SharedPreferences prefs;
    String userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uTils = new Utils();
        prefs = this.getActivity().getSharedPreferences(uTils.SharedPrefName, Context.MODE_PRIVATE);
        credentialStore = prefs.edit();

        userName = prefs.getString(uTils.SessionUsername, "");

        final UserTimeline userTimeLine = new UserTimeline.Builder()
                .screenName(userName+"")
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(getActivity(),userTimeLine);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.time_line, container, false);
    }

}
