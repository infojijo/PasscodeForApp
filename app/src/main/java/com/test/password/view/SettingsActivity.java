package com.test.password.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.test.password.R;
import com.test.password.presenter.Presenter;
import com.test.password.receiver.AlertReceiver;
import com.test.password.util.Constants;
import com.test.password.util.SharedPrefUtil;

import androidx.appcompat.app.AppCompatActivity;

/**
 * SettingsActivity acts as a Home screen for the app.
 * MVP architecture is followed in most of the operations.
 */
public class SettingsActivity extends AppCompatActivity
        implements PopDialog.PopDialogListener, Presenter.View {

    private Presenter presenter;
    private SharedPrefUtil sharedPrefUtil;
    private Switch switchLocker;
    private TextView mTextMessage;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        /**
         * @param context
         * @param intent
         * Broadcastreceiver receives updates on Passcode lock removal.
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            switchLocker.setEnabled(true);
            switchLocker.setChecked(true);
            presenter.updatePasswordMismatch("", 1);
            sharedPrefUtil.removePasswordAttempt();
            sharedPrefUtil.setPasswordSet(true);
            sharedPrefUtil.setScreenCounter(Constants.ENTER_PASSWORD_SCREEN_ID);
            openDialog(sharedPrefUtil.getScreenCounter());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchLocker = findViewById(R.id.switchLock);
        mTextMessage = findViewById(R.id.textMessage);

        presenter = new Presenter(this);
        sharedPrefUtil = new SharedPrefUtil(this);

        switchLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchLocker.isChecked()) {
                    if (!sharedPrefUtil.IsPasswordSet()) {
                        sharedPrefUtil.setScreenCounter(Constants.NEW_PASSWORD_SCREEN_ID);
                        openDialog(sharedPrefUtil.getScreenCounter());
                    } else {
                        switchLocker.setChecked(true);
                        sharedPrefUtil.setSecure(true);
                    }
                } else {
                    switchLocker.setChecked(false);
                    sharedPrefUtil.setSecure(false);
                    presenter.updatePasswordMismatch("", 0);
                }
            }
        });

        registerReceiver(broadcastReceiver, new IntentFilter(Constants.ALARM_BC_NAME));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sharedPrefUtil.IsAccountLocked()) {
            switchLocker.setEnabled(false);
            presenter.updatePasswordMismatch(getString(R.string.too_many_failed), 0);
        } else {
            if (sharedPrefUtil.IsPasswordSet()) {
                if (sharedPrefUtil.IsSecure()) {
                    sharedPrefUtil.setScreenCounter(Constants.ENTER_PASSWORD_SCREEN_ID);
                    openDialog(sharedPrefUtil.getScreenCounter());
                } else {
                    switchLocker.setChecked(false);
                }
            }
        }
    }

    /**
     * @param screen Initializing and invoking DialogFragment
     */
    public void openDialog(int screen) {
        PopDialog popDialog = new PopDialog(screen);
        popDialog.show(getSupportFragmentManager(), "popup dialog");
    }

    @Override
    public void messageToActivity(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * @param password
     * @param screen   Compare each passcode status to decide the next steps to proceed with.
     */
    @Override
    public void passCodeCompare(String password, int screen) {
        sharedPrefUtil.setScreenCounter(sharedPrefUtil.getScreenCounter() + 1);

        switch (sharedPrefUtil.getScreenCounter()) {

            case 2: {
                openDialog(sharedPrefUtil.getScreenCounter());
                sharedPrefUtil.setPassword(password);
                break;
            }
            case 3: {
                if (sharedPrefUtil.getPassword().equals(password)) {
                    presenter.updatePasswordMismatch(getString(R.string.passcode_matched), 1);
                    sharedPrefUtil.setPasswordSet(true);
                    sharedPrefUtil.setSecure(true);
                } else {
                    switchLocker.setChecked(false);
                    presenter.updatePasswordMismatch(getString(R.string.incorrect_password), 0);
                    sharedPrefUtil.setScreenCounter(Constants.NEW_PASSWORD_SCREEN_ID);
                }
                break;
            }
            case 4: {
                if (sharedPrefUtil.getPassword().equals(password)) {
                    presenter.updatePasswordMismatch(getString(R.string.password_matched), 1);
                    sharedPrefUtil.setPasswordSet(true);
                    switchLocker.setChecked(true);
                    switchLocker.setEnabled(true);
                    sharedPrefUtil.setAccountLocked(false);
                    sharedPrefUtil.removePasswordAttempt();
                    sharedPrefUtil.setSecure(true);
                } else {

                    presenter.updatePasswordMismatch(getString(R.string.relaunch), 0);
                    sharedPrefUtil.setPasswordAttempts();
                    switchLocker.setEnabled(false);
                    switchLocker.setChecked(true);
                    if (sharedPrefUtil.getPasswordAttempts() > Constants.MAX_PASSWORD_ATTEMPT) {
                        switchLocker.setEnabled(false);
                        sharedPrefUtil.setAccountLocked(true);
                        sharedPrefUtil.setSecure(true);
                        switchLocker.setChecked(true);
                        sharedPrefUtil.removePasswordAttempt();
                        presenter.updatePasswordMismatch(getString(R.string.too_many_failed), 0);
                        presenter.runPasswordLock();
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * In this method an AlarmService is called to run when Passcode becomes locked.
     * Current time + 1 minute(60000 mls) is added for keeping the app in locked mode.
     * For a short running task AlarmService seems suitable/accurate when compared with other options.
     */
    @Override
    public void runPasswordLockBackground() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        long alarmtime = System.currentTimeMillis() + 60000;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmtime, pendingIntent);
    }

    /**
     * @param message
     * @param status  Method callbacks from presenter inroder to display status messages.
     */
    @Override
    public void updateStatusMessage(String message, int status) {
        mTextMessage.setText(message);
        mTextMessage.setTextColor(status == 1 ? getResources().getColor(R.color.colorAccent)
                : getResources().getColor(R.color.coloRed));
    }
}