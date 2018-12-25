package com.kaloyanov.ivan.yourleaf.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.kaloyanov.ivan.yourleaf.R;

/*
 * Simple dialog with a message
 * @author ivan.kaloyanov
 */
public class ForceQuitFragment extends DialogFragment {

    private static final String ACTION_KEY = "action";

    public static ForceQuitFragment newInstance(String action) {
        ForceQuitFragment fragment =  new ForceQuitFragment();
        Bundle bundle = new Bundle(3);
        bundle.putString(ACTION_KEY, action);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getArguments().getString(ACTION_KEY))
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
        return builder.create();
    }
}
