package com.alkisum.android.cloudops.net;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import com.alkisum.android.cloudops.R;
import com.alkisum.android.cloudops.utils.CloudPref;

/**
 * Dialog to connect to a ownCloud server.
 *
 * @author Alkisum
 * @version 1.2
 * @since 1.0
 */
public class ConnectDialog extends DialogFragment {

    /**
     * Fragment tag for FragmentManager.
     */
    public static final String FRAGMENT_TAG = "connect_dialog";

    /**
     * Argument for operation type.
     */
    private static final String ARG_OPERATION = "arg_operation";

    /**
     * EditText for server address.
     */
    private EditText addressEditText;

    /**
     * EditText for remote path.
     */
    private EditText pathEditText;

    /**
     * EditText for username.
     */
    private EditText usernameEditText;

    /**
     * EditText for password.
     */
    private EditText passwordEditText;

    /**
     * Listener for the dialog.
     */
    private ConnectDialogListener callback;

    /**
     * SharedPreferences to store the server address, the remote path and the
     * username.
     */
    private SharedPreferences sharedPref;

    /**
     * Create new instance of ConnectDialog.
     *
     * @param operation Operation type
     * @return Instance of ConnectDialog
     */
    public static ConnectDialog newInstance(final int operation) {
        ConnectDialog connectDialog = new ConnectDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_OPERATION, operation);
        connectDialog.setArguments(args);
        return connectDialog;
    }

    @Override
    public final void onAttach(final Context context) {
        super.onAttach(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set the ConnectDialogListener.
     *
     * @param callback ConnectDialogListener instance
     */
    public final void setCallback(final ConnectDialogListener callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public final Dialog onCreateDialog(final Bundle savedInstanceState) {
        final int operation = getArguments().getInt(ARG_OPERATION);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = View.inflate(getActivity(), R.layout.dialog_connect, null);

        addressEditText = view.findViewById(R.id.address);
        pathEditText = view.findViewById(R.id.path);
        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);

        addressEditText.setText(sharedPref.getString(CloudPref.ADDRESS, ""));
        pathEditText.setText(sharedPref.getString(CloudPref.PATH, ""));
        usernameEditText.setText(sharedPref.getString(CloudPref.USERNAME, ""));

        builder.setView(view)
                .setTitle(R.string.connect_title)
                .setPositiveButton(R.string.action_connect,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                ConnectInfo info = new ConnectInfo(
                                        addressEditText.getText().toString(),
                                        pathEditText.getText().toString(),
                                        usernameEditText.getText().toString(),
                                        passwordEditText.getText().toString()
                                );
                                if (callback != null) {
                                    callback.onSubmit(operation, info);
                                }

                                if (sharedPref.getBoolean(
                                        CloudPref.SAVE_OWNCLOUD_INFO, false)) {
                                    saveConnectInfo(info);
                                }

                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                ConnectDialog.this.getDialog().cancel();
                            }
                        });
        return builder.create();
    }

    /**
     * Save the connection information (server address, remote path and
     * username) into the SharedPreferences to pre-fill the connect dialog.
     *
     * @param connectInfo Connection information
     */
    private void saveConnectInfo(final ConnectInfo connectInfo) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CloudPref.ADDRESS, connectInfo.getAddress());
        editor.putString(CloudPref.PATH, connectInfo.getPath());
        editor.putString(CloudPref.USERNAME, connectInfo.getUsername());
        editor.apply();
    }

    /**
     * Listener for the dialog.
     */
    public interface ConnectDialogListener {

        /**
         * Called when the user submit the dialog.
         *
         * @param operation   Operation type
         * @param connectInfo Connection information entered in the dialog
         */
        void onSubmit(int operation, ConnectInfo connectInfo);
    }
}
