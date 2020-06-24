package com.test.password.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.test.password.R;
import com.test.password.util.Constants;
import com.test.password.util.SharedPrefUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Reusable Dialog Fragment used for accepting passcode from Users.
 */
public class PopDialog extends AppCompatDialogFragment {
    private EditText editTextPassword;
    private PopDialogListener listener;
    private int screenValue;
    private SharedPrefUtil sharedPrefUtil;

    public PopDialog(int screen) {
        this.screenValue = screen;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_passcode, null);
        builder.setView(view)
                .setTitle(getTitleName(sharedPrefUtil.getScreenCounter()))
                .setCancelable(false)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                            return true;
                        }
                        return false;
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String password = editTextPassword.getText().toString();
                        if (password.length() > Constants.EMPTY_PASSWORD_SIZE) {
                            int screen = screenValue;
                            listener.passCodeCompare(password, screen);
                        } else {
                            listener.messageToActivity(getString(R.string.no_passcode_entered));
                        }
                    }
                });
        editTextPassword = view.findViewById(R.id.edit_password);
        return builder.create();
    }

    private String getTitleName(int dialogCount) {
        String value = getString(R.string.passcode_text_one);
        switch (dialogCount) {
            case 1: {
                value = getString(R.string.passcode_text_one);
                break;
            }
            case 2: {
                value = getString(R.string.passcode_text_two);
                break;
            }
            case 3: {
                value = getString(R.string.passcode_text_three);
                break;
            }
        }
        return value;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setCanceledOnTouchOutside(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (PopDialogListener) context;
            sharedPrefUtil = new SharedPrefUtil(context);
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement PopDialogListener");
        }
    }

    public interface PopDialogListener {
        void passCodeCompare(String password, int screen);

        void messageToActivity(String message);
    }

}
