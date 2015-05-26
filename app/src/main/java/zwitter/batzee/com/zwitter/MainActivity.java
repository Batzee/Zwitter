package zwitter.batzee.com.zwitter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class MainActivity extends ActionBarActivity {

    TwitterLoginButton loginButton;
    SharedPreferences prefs;
    SharedPreferences.Editor credentialStore;
    Utils uTils;
    String sessionToken;
    Intent dashBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uTils = new Utils();
        dashBoard = new Intent(MainActivity.this, Dashboard.class);
        prefs =  getSharedPreferences(uTils.SharedPrefName, MODE_PRIVATE);
        credentialStore = prefs.edit();

        sessionToken = prefs.getString(uTils.SessionToken,"");

        if(!(sessionToken.equals(""))){
            startActivity(dashBoard);
        }


        loginButton = (TwitterLoginButton)findViewById(R.id.login_button);

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d("Callback Results","Success - "+result);

                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                credentialStore.putString(uTils.SessionToken,token);
                credentialStore.putString(uTils.SessionSecret, secret);
                credentialStore.commit();

                finish();
                startActivity(dashBoard);

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
        loginButton.onActivityResult(requestCode, resultCode,data);
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
}
