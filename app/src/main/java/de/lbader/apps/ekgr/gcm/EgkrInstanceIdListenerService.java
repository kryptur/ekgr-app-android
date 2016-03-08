package de.lbader.apps.ekgr.gcm;

import com.google.android.gms.iid.InstanceIDListenerService;

public class EgkrInstanceIdListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
    }
}
