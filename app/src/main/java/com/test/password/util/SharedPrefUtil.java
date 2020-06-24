package com.test.password.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This is the support class for Shared Preference in the app. Handles shared preference storage / retrieval operations.
 */
public class SharedPrefUtil {

    private Context mContext;
    private SharedPreferences sharedPreferences;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SessionPref";
    private SharedPreferences.Editor editor;
    private static String IS_LOCKED = "IS_LOCKED";
    private static String SCREEN_COUNT = "screen_count";
    private static String SCREEN_PASSWORD = "screen_password";
    private static String PASSWORD_SET = "password_set";
    private static String SECURE_SET = "secure_set";
    private static String PASSWORD_ATTEMPTS = "password_attempts";


    public SharedPrefUtil(Context context) {
        this.mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void setAccountLocked(Boolean isLocked) {
        editor.putBoolean(IS_LOCKED, isLocked);
        editor.commit();
    }

    public boolean IsAccountLocked() {

        return sharedPreferences.getBoolean(IS_LOCKED, false);
    }

    public void setScreenCounter(int count) {
        editor.putInt(SCREEN_COUNT, count);
        editor.commit();
    }

    public void setPassword(String password) {
        editor.putString(SCREEN_PASSWORD, password);
        editor.commit();
    }

    public String getPassword() {
        return sharedPreferences.getString(SCREEN_PASSWORD, "");
    }

    public void setPasswordSet(boolean password) {
        editor.putBoolean(PASSWORD_SET, password);
        editor.commit();
    }

    public boolean IsPasswordSet() {
        return sharedPreferences.getBoolean(PASSWORD_SET, false);
    }

    public void setSecure(boolean secure) {
        editor.putBoolean(SECURE_SET, secure);
        editor.commit();
    }

    public boolean IsSecure() {
        return sharedPreferences.getBoolean(SECURE_SET, false);
    }

    public int getScreenCounter() {
        return sharedPreferences.getInt(SCREEN_COUNT, 1);
    }

    public void setPasswordAttempts() {
        editor.putInt(PASSWORD_ATTEMPTS, getPasswordAttempts() + 1);
        editor.commit();

    }

    public int getPasswordAttempts() {
        return sharedPreferences.getInt(PASSWORD_ATTEMPTS, 0);
    }

    public void removePasswordAttempt() {
        editor.remove(PASSWORD_ATTEMPTS);
        editor.commit();
    }

}
