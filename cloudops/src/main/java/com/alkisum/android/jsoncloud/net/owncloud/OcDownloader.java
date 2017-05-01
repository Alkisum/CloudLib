package com.alkisum.android.jsoncloud.net.owncloud;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.alkisum.android.jsoncloud.file.json.JsonFile;
import com.alkisum.android.jsoncloud.file.json.JsonFileReader;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.DownloadRemoteFileOperation;
import com.owncloud.android.lib.resources.files.FileUtils;
import com.owncloud.android.lib.resources.files.ReadRemoteFolderOperation;
import com.owncloud.android.lib.resources.files.RemoteFile;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Class downloading files from an ownCloud server.
 *
 * @author Alkisum
 * @version 1.0
 * @since 1.0
 */
public class OcDownloader implements OnRemoteOperationListener,
        OnDatatransferProgressListener, JsonFileReader.JsonFileReaderListener {

    /**
     * Log tag.
     */
    private static final String TAG = "OcDownloader";

    /**
     * Context.
     */
    private final Context context;

    /**
     * Listener for the download task.
     */
    private OcDownloaderListener callback;

    /**
     * Remote path to use to download the files.
     */
    private String remotePath;

    /**
     * OwnCloud client.
     */
    private OwnCloudClient client;

    /**
     * Handler for the operation on the ownCloud server.
     */
    private Handler handler;

    /**
     * Queue of remote files to download.
     */
    private Queue<RemoteFile> remoteFiles;

    /**
     * List of downloaded files to be read.
     */
    private List<File> localFiles;

    /**
     * OcDownloader constructor.
     *
     * @param context Context
     */
    public OcDownloader(final Context context) {
        this.context = context;
        try {
            callback = (OcDownloaderListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getSimpleName()
                    + " must implement OcDownloaderListener");
        }
        handler = new Handler();
    }

    /**
     * Initialize the downloader with all the connection information.
     *
     * @param address  Server address
     * @param path     Remote path
     * @param username Username
     * @param password Password
     * @return Current instance of the downloader
     */
    public final OcDownloader init(final String address, final String path,
                                   final String username,
                                   final String password) {
        remotePath = buildRemotePath(path);

        Uri serverUri = Uri.parse(address);
        client = OwnCloudClientFactory.createOwnCloudClient(
                serverUri, context, true);
        client.setCredentials(OwnCloudCredentialsFactory.newBasicCredentials(
                username, password));

        return this;
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
        readOperation.execute(client, this, handler);
    }

    /**
     * Download the given remote file.
     *
     * @param file Remote file
     */
    private void download(final RemoteFile file) {
        callback.onDownloadStart(file);

        File localFile = new File(context.getCacheDir(), file.getRemotePath());
        localFiles.add(localFile);

        DownloadRemoteFileOperation downloadOperation =
                new DownloadRemoteFileOperation(file.getRemotePath(),
                        context.getCacheDir().getAbsolutePath());
        downloadOperation.addDatatransferProgressListener(this);
        downloadOperation.execute(client, this, handler);
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
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Downloading... (" + percentage + "%)");
                callback.onDownloading(percentage);
            }
        });
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
            callback.onDownloadFailed(result.getLogMessage());
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
            String fileName = getRemoteFileName(remoteFile);
            if (fileName.endsWith(JsonFile.FILE_EXT)) {
                remoteFiles.add(remoteFile);
            }
        }
        RemoteFile remoteFile = remoteFiles.poll();
        if (remoteFile != null) {
            download(remoteFile);
        } else {
            callback.onNoFileToDownload();
        }
    }

    /**
     * Called when the download remote file operation is finished.
     */
    private void onDownloadRemoteFileFinish() {
        RemoteFile remoteFile = remoteFiles.poll();
        if (remoteFile != null) {
            download(remoteFile);
        } else {
            if (localFiles.isEmpty()) {
                callback.onNoFileToDownload();
            } else {
                callback.onAllDownloadComplete();
                new JsonFileReader(this, localFiles).execute();
            }
        }
    }

    /**
     * Get the file name from the remote file object.
     *
     * @param file Remote file
     * @return File name
     */
    private String getRemoteFileName(final RemoteFile file) {
        String[] splitPath = file.getRemotePath().split("/");
        return splitPath[splitPath.length - 1];
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

    @Override
    public final void onJsonFilesRead(final List<JsonFile> jsonFiles) {
        callback.onJsonFilesRead(jsonFiles);
    }

    @Override
    public final void onReadJsonFileFailed(final Exception exception) {
        callback.onReadingFileFailed(exception);
    }

    /**
     * Listener to get notification from the OcDownloader tasks.
     */
    public interface OcDownloaderListener {

        /**
         * Called when a download operation starts.
         *
         * @param file Remote file being downloaded
         */
        void onDownloadStart(RemoteFile file);

        /**
         * Called when there is no file to download.
         */
        void onNoFileToDownload();

        /**
         * Called when the file is being downloaded.
         *
         * @param percentage Progress of the download (percentage)
         */
        void onDownloading(int percentage);

        /**
         * Called when all download operations are completed.
         */
        void onAllDownloadComplete();

        /**
         * Called when the download failed.
         *
         * @param message Message describing the cause of the failure
         */
        void onDownloadFailed(String message);

        /**
         * Called when all the files have been read and the JSON objects
         * created.
         *
         * @param jsonFiles List of JSON files read
         */
        void onJsonFilesRead(List<JsonFile> jsonFiles);

        /**
         * Called when the JSON file could not have been read.
         *
         * @param e Exception thrown during the task
         */
        void onReadingFileFailed(Exception e);
    }
}
