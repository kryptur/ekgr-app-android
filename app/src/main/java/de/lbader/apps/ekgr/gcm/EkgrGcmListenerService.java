package de.lbader.apps.ekgr.gcm;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;


public class EkgrGcmListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {

        super.onMessageReceived(from, data);
    }
}
