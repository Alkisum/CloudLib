package com.alkisum.android.cloudlib.net.nextcloud;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.alkisum.android.cloudlib.utils.Notifier;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;

/**
 * Base class for ownCloud operations.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.2
 */
class NcOperator {

    /**
     * Context.
     */
    private final Context context;

    /**
     * OwnCloud client.
     */
    private OwnCloudClient client;

    /**
     * Handler for the operation on the ownCloud server.
     */
    private final Handler handler;

    /**
     * Notifier instance to show notification when doing operations.
     */
    private Notifier notifier;

    /**
     * NcOperator constructor.
     *
     * @param context     Context
     * @param intent      Intent for notification
     * @param channelId   Channel id for notification
     * @param channelName Channel name
     * @param icon        Icon for notification
     */
    NcOperator(final Context context, final Intent intent,
               final String channelId, final String channelName,
               final int icon) {
        this.context = context;
        this.handler = new Handler();

        notifier = new Notifier(context, channelId, channelName);
        if (intent != null) {
            notifier.setIntent(context, intent);
        }
        notifier.setIcon(icon);
    }

    /**
     * Initialize ownCloud client with given information.
     *
     * @param address  Server address
     * @param username Username
     * @param password Password
     */
    final void init(final String address, final String username,
                    final String password) {
        Uri serverUri = Uri.parse(address);
        client = OwnCloudClientFactory.createOwnCloudClient(
                serverUri, context, true);
        client.setCredentials(OwnCloudCredentialsFactory.newBasicCredentials(
                username, password));
    }

    /**
     * @return Context
     */
    final Context getContext() {
        return context;
    }

    /**
     * @return ownCloud client
     */
    final OwnCloudClient getClient() {
        return client;
    }

    /**
     * @return Handler for the operation on the ownCloud server
     */
    final Handler getHandler() {
        return handler;
    }

    /**
     * @return Notifier instance to show notification when doing operations
     */
    final Notifier getNotifier() {
        return notifier;
    }
}
