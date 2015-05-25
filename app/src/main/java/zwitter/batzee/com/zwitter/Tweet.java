package zwitter.batzee.com.zwitter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.twitter.sdk.android.tweetcomposer.TweetComposer;

/**
 * Created by adh on 5/20/2015.
 */
public class Tweet extends Activity {

    EditText tweetText;
    ImageButton tweetButton;
    String tweetableText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweet);

        tweetText = (EditText) findViewById(R.id.tweetText);
        tweetButton = (ImageButton) findViewById(R.id.tweetButton);

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tweetableText = tweetText.getText().toString();

                TweetComposer.Builder builder = new TweetComposer.Builder(Tweet.this)
                        .text(tweetableText);

                builder.show();

            }
        });

    }
}
