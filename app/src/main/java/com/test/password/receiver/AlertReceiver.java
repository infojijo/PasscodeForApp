package com.test.password.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.test.password.util.Constants;
import com.test.password.util.SharedPrefUtil;

/**
 *AlertReceiver uses to broadcast the alarm service that runs for password lock scenario.
 */
public class AlertReceiver extends BroadcastReceiver {
    private SharedPrefUtil sharedPrefUtil;

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPrefUtil = new SharedPrefUtil(context);
        sharedPrefUtil.setAccountLocked(false);

        Intent i = new Intent(Constants.ALARM_BC_NAME);
        i.putExtra("message", "BROADCAST_DATA");
        context.sendBroadcast(i);

    }
}