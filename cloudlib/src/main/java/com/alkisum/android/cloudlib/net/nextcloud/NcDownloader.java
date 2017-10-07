package com.alkisum.android.cloudlib.net.nextcloud;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.alkisum.android.cloudlib.R;
import com.alkisum.android.cloudlib.events.DownloadEvent;
import com.alkisum.android.cloudlib.file.json.JsonFile;
import com.alkisum.android.cloudlib.utils.OcUtils;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.DownloadRemoteFileOperation;
import com.owncloud.android.lib.resources.files.FileUtils;
import com.owncloud.android.lib.resources.files.ReadRemoteFolderOperation;
import com.owncloud.android.lib.resources.files.RemoteFile;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Class downloading files from an Nextcloud server.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.0
 */
public class NcDownloader extends NcOperator implements
        OnRemoteOperationListener, OnDatatransferProgressListener {

    /**
     * Log tag.
     */
    private static final String TAG = "NcDownloader";

    /**
     * Subscriber ids allowed to process the events.
     */
    private Integer[] subscriberIds;

    /**
     * Remote path to use to download the files.
     */
    private String remotePath;

    /**
     * Queue of remote files to download.
     */
    private Queue<RemoteFile> remoteFiles;

    /**
     * List of downloaded files to be read.
     */
    private List<File> localFiles;

    /**
     * List of file names to exclude when downloading remote files.
     */
    private List<String> excludeFileNames;

    /**
     * Number of files to download.
     */
    private int totalRemoteFiles;

    /**
     * EventBus instance.
     */
    private EventBus eventBus = EventBus.getDefault();

    /**
     * NcDownloader constructor.
     *
     * @param context       Context
     * @param intent        Intent for notification, null if no intent needed
     * @param channelId     Channel id
     * @param channelName   Channel name
     * @param subscriberIds Subscriber ids allowed to process the events
     */
    public NcDownloader(final Context context, final Intent intent,
                        final String channelId, final String channelName,
                        final Integer[] subscriberIds) {
        super(context, intent, channelId, channelName,
                android.R.drawable.stat_sys_download);
        this.subscriberIds = subscriberIds;
    }

    /**
     * Initialize the downloader with all the connection information.
     *
     * @param address  Server address
     * @param path     Remote path
     * @param username Username
     * @param password Password
     */
    public final void init(final String address, final String path,
                           final String username, final String password) {
        remotePath = buildRemotePath(path);
        super.init(address, username, password);
    }

    /**
     * @param excludeFileNames List of file names to exclude when downloading
     *                         remote files
     */
    public final void setExcludeFileNames(final List<String> excludeFileNames) {
        this.excludeFileNames = excludeFileNames;
    }

    /**
     * Start the process. Get the remote files to download.
     */
    public final void start() {
        getRemoteFiles();
    }

    /**
     * List all remote files contained in the remote path directory.
     */
    private void getRemoteFiles() {
        ReadRemoteFolderOperation readOperation =
                new ReadRemoteFolderOperation(remotePath);
        readOperation.execute(getClient(), this, getHandler());
    }

    /**
     * Download the given remote file.
     *
     * @param file Remote file
     */
    private void download(final RemoteFile file) {
        File localFile = new File(getContext().getCacheDir(),
                file.getRemotePath());
        localFiles.add(localFile);

        DownloadRemoteFileOperation downloadOperation =
                new DownloadRemoteFileOperation(file.getRemotePath(),
                        getContext().getCacheDir().getAbsolutePath());
        downloadOperation.addDatatransferProgressListener(this);
        downloadOperation.execute(getClient(), this, getHandler());
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
                R.string.downloader_downloading) + fileName);
        getNotifier().setProgress(percentage);
        getNotifier().show();
    }

    @Override
    public final void onRemoteOperationFinish(
            final RemoteOperation operation,
            final RemoteOperationResult result) {
        if (result.isSuccess()) {
            if (operation instanceof ReadRemoteFolderOperation) {
                onReadRemoteFolderFinish(result);
            } else if (operation instanceof DownloadRemoteFileOperation) {
                onDownloadRemoteFileFinish();
            }
        } else {
            Log.e(TAG, result.getLogMessage(), result.getException());
            eventBus.post(new DownloadEvent(subscriberIds, DownloadEvent.ERROR,
                    result.getLogMessage()));
        }
    }

    /**
     * Called when the read remote folder operation is finished.
     *
     * @param result Operation result
     */
    private void onReadRemoteFolderFinish(final RemoteOperationResult result) {
        remoteFiles = new LinkedList<>();
        localFiles = new ArrayList<>();
        for (Object obj : result.getData()) {
            RemoteFile remoteFile = (RemoteFile) obj;
            String fileName = OcUtils.getRemoteFileName(remoteFile);
            if (fileName.endsWith(JsonFile.FILE_EXT)
                    && !excludeFileNames.contains(fileName)) {
                remoteFiles.add(remoteFile);
            }
        }
        totalRemoteFiles = remoteFiles.size();
        RemoteFile remoteFile = remoteFiles.poll();
        if (remoteFile != null) {
            getNotifier().setText((totalRemoteFiles - remoteFiles.size())
                    + "/" + totalRemoteFiles);
            download(remoteFile);
        } else {
            eventBus.post(new DownloadEvent(subscriberIds,
                    DownloadEvent.NO_FILE));
        }
    }

    /**
     * Called when the download remote file operation is finished.
     */
    private void onDownloadRemoteFileFinish() {
        RemoteFile remoteFile = remoteFiles.poll();
        if (remoteFile != null) {
            getNotifier().setText((totalRemoteFiles - remoteFiles.size())
                    + "/" + totalRemoteFiles);
            download(remoteFile);
            eventBus.post(new DownloadEvent(subscriberIds,
                    DownloadEvent.DOWNLOADING));
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getNotifier().setIcon(
                            android.R.drawable.stat_sys_download_done);
                    getNotifier().setAutoCancel(true);
                    getNotifier().setTitle(getContext().getString(
                            R.string.downloader_complete));
                    getNotifier().setProgress(100);
                    getNotifier().show();
                }
            }, 100);
            eventBus.post(new DownloadEvent(subscriberIds, DownloadEvent.OK,
                    localFiles));
        }
    }

    /**
     * Build a valid remote path from the path given by the user.
     *
     * @param path Path submit by user
     * @return Valid remote path
     */
    private static String buildRemotePath(final String path) {
        String remotePath = path;
        if (remotePath == null || remotePath.equals("")) {
            remotePath = FileUtils.PATH_SEPARATOR;
        }
        if (!remotePath.startsWith(FileUtils.PATH_SEPARATOR)) {
            remotePath = FileUtils.PATH_SEPARATOR + remotePath;
        }
        if (!remotePath.endsWith(FileUtils.PATH_SEPARATOR)) {
            remotePath = remotePath + FileUtils.PATH_SEPARATOR;
        }
        return remotePath;
    }
}
