package de.lbader.apps.ekgr.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import de.lbader.apps.ekgr.R;

/**
 * Created by lbader on 3/7/16.
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);


            sharedPreferences.edit().putBoolean("gcm_send", true).apply();
            sendRegistrationToServer(token);
        } catch (Exception ex) {
            Log.e("GCM", "GCM registration failed");
            sharedPreferences.edit().putBoolean("gcm_send", false).apply();
        }
    }

    private void sendRegistrationToServer(String token) {

    }
}
