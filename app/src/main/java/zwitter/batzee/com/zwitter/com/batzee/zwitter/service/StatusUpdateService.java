package zwitter.batzee.com.zwitter.com.batzee.zwitter.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by adh on 6/1/2015.
 */
public class StatusUpdateService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public StatusUpdateService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
