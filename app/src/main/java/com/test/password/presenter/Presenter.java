package com.test.password.presenter;

/**
 * Presenter class used for updating status messages back to SettingsActivity
 * and
 * Controlling the Passcode lock timer service.
 */
public class Presenter {

    private View view;

    public Presenter(View view) {
        this.view = view;
    }

    public void updatePasswordMismatch(String error, int status) {
        view.updateStatusMessage(error,status);
    }
    public void runPasswordLock(){
        view.runPasswordLockBackground();
    }


    public interface View {
        void updateStatusMessage(String message, int status);
        void runPasswordLockBackground();
    }
}
