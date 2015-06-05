package zwitter.batzee.com.zwitter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

/**
 * Created by adh on 6/4/2015.
 */
public class TweetSearchLine extends ListFragment {

    private EditText searchText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialSearcTweet();

    }

    private void initialSearcTweet() {
        final SearchTimeline searchTimeLine = new SearchTimeline.Builder()
                .query("#Exilesoft")
                .build();

        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(getActivity(), searchTimeLine);
        setListAdapter(adapter);
    }

    private void searcTweet(String word) {
        final SearchTimeline searchTimeLine = new SearchTimeline.Builder()
                .query(word+"")
                .build();

        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(getActivity(), searchTimeLine);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // return inflater.inflate(R.layout.search_line, container, false);

        View view = inflater.inflate(R.layout.search_line, container, false);
        searchText = (EditText) view.findViewById(R.id.searchText);

        searchText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            String word = searchText.getText().toString();
                            searcTweet(word);
                            hideKeyBoard();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        return view;
    }

    private void hideKeyBoard() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

}