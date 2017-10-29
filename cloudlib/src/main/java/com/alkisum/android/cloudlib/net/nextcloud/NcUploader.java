package com.alkisum.android.cloudlib.net.nextcloud;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.alkisum.android.cloudlib.R;
import com.alkisum.android.cloudlib.events.UploadEvent;
import com.alkisum.android.cloudlib.file.CloudFile;
import com.alkisum.android.cloudlib.utils.OcUtils;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.FileUtils;
import com.owncloud.android.lib.resources.files.UploadRemoteFileOperation;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Queue;

/**
 * Class uploading files to the server.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.0
 */
public class NcUploader extends NcOperator implements OnRemoteOperationListener,
        OnDatatransferProgressListener {

    /**
     * Log tag.
     */
    private static final String TAG = "NcUploader";

    /**
     * Subscribers allowed to process the events.
     */
    private Integer[] subscriberIds;

    /**
     * Queue of files to upload.
     */
    private Queue<CloudFile> cloudFiles;

    /**
     * Path on the server where to upload the file.
     */
    private String remotePath;

    /**
     * EventBus instance.
     */
    private EventBus eventBus = EventBus.getDefault();

    /**
     * NcUploader constructor.
     *
     * @param context       Context
     * @param intent        Intent for notification, null if no intent needed
     * @param channelId     Channel id
     * @param channelName   Channel name
     * @param subscriberIds Subscribers allowed to process the events
     */
    public NcUploader(final Context context, final Intent intent,
                      final String channelId, final String channelName,
                      final Integer[] subscriberIds) {
        super(context, intent, channelId, channelName,
                android.R.drawable.stat_sys_upload);
        this.subscriberIds = subscriberIds;
    }

    /**
     * Initialize the uploader with all the connection information.
     *
     * @param address  Server address
     * @param path     Remote path
     * @param username Username
     * @param password Password
     */
    public final void init(final String address, final String path,
                           final String username, final String password) {
        remotePath = path;
        super.init(address, username, password);
    }

    /**
     * Start uploading the given files.
     *
     * @param cloudFileQueue Files to upload
     */
    public final void start(final Queue<CloudFile> cloudFileQueue) {
        this.cloudFiles = cloudFileQueue;
        upload(cloudFiles.poll());
    }

    /**
     * Upload the file to the server.
     *
     * @param file File to upload
     */
    private void upload(final CloudFile file) {
        File fileToUpload = file.getFile();
        String path = buildRemotePath(file);
        String mimeType = "text/plain";
        Long timeStampLong = fileToUpload.lastModified() / 1000;
        String timeStamp = timeStampLong.toString();

        UploadRemoteFileOperation op = new UploadRemoteFileOperation(
                fileToUpload.getAbsolutePath(),
                path,
                mimeType,
                timeStamp);
        op.addDatatransferProgressListener(this);
        op.execute(getClient(), this, getHandler());
    }

    /**
     * Build a valid remote path from the path given by the user.
     *
     * @param file File used to build the remote path
     * @return Valid remote path
     */
    private String buildRemotePath(final CloudFile file) {
        if (remotePath == null || remotePath.equals("")) {
            remotePath = FileUtils.PATH_SEPARATOR;
        }
        if (!remotePath.startsWith(FileUtils.PATH_SEPARATOR)) {
            remotePath = FileUtils.PATH_SEPARATOR + remotePath;
        }
        if (!remotePath.endsWith(FileUtils.PATH_SEPARATOR)) {
            remotePath = remotePath + FileUtils.PATH_SEPARATOR;
        }
        // Add the file name to the remote path
        return remotePath + file.getName();
    }

    @Override
    public final void onTransferProgress(final long progressRate,
                                         final long totalTransferredSoFar,
                                         final long totalToTransfer,
                                         final String fileName) {
        final int percentage;
        if (totalToTransfer > 0) {
            percentage = (int) (totalTransferredSoFar * 100 / totalToTransfer);
        } else {
            percentage = 0;
        }
        getNotifier().setTitle(getContext().getString(
                R.string.uploader_uploading) + OcUtils.getFileName(fileName));
        getNotifier().setProgress(percentage);
        getNotifier().show();
    }

    @Override
    public final void onRemoteOperationFinish(
            final RemoteOperation operation,
            final RemoteOperationResult result) {
        if (result.isSuccess()) {
            onUploadRemoteFileFinish();
        } else {
            Log.e(TAG, result.getLogMessage(), result.getException());
            eventBus.post(new UploadEvent(subscriberIds, UploadEvent.ERROR,
                    result.getLogMessage()));
        }
    }

    /**
     * Called when the upload remote file operation is finished.
     */
    private void onUploadRemoteFileFinish() {
        CloudFile file = cloudFiles.poll();
        if (file != null) {
            upload(file);
            eventBus.post(new UploadEvent(subscriberIds,
                    UploadEvent.UPLOADING));
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getNotifier().setIcon(
                            android.R.drawable.stat_sys_upload_done);
                    getNotifier().setAutoCancel(true);
                    getNotifier().setTitle(getContext().getString(
                            R.string.uploader_complete));
                    getNotifier().setProgress(100);
                    getNotifier().show();
                }
            }, 100);
            eventBus.post(new UploadEvent(subscriberIds, UploadEvent.OK));
        }
    }
}
