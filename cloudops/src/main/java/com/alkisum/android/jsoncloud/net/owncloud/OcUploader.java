package com.alkisum.android.jsoncloud.net.owncloud;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.alkisum.android.jsoncloud.file.json.JsonFile;
import com.alkisum.android.jsoncloud.file.json.JsonFileWriter;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.FileUtils;
import com.owncloud.android.lib.resources.files.UploadRemoteFileOperation;

import java.io.File;
import java.util.Queue;

/**
 * Class uploading files to the ownCloud server.
 *
 * @author Alkisum
 * @version 1.0
 * @since 1.0
 */
public class OcUploader implements OnRemoteOperationListener,
        OnDatatransferProgressListener, JsonFileWriter.JsonFileWriterListener {

    /**
     * Log tag.
     */
    private static final String TAG = "OcUploader";

    /**
     * Queue of JSON files to upload.
     */
    private Queue<JsonFile> jsonFiles;

    /**
     * Context.
     */
    private final Context context;

    /**
     * Listener for the upload task.
     */
    private UploaderListener callback;

    /**
     * Path on the server where to upload the file.
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
     * OcUploader constructor.
     *
     * @param context   Context
     * @param jsonFiles Queue of json files to upload
     */
    public OcUploader(final Context context, final Queue<JsonFile> jsonFiles) {
        this.context = context;
        try {
            callback = (UploaderListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getSimpleName()
                    + " must implement UploaderListener");
        }
        this.jsonFiles = jsonFiles;
        handler = new Handler();
    }

    /**
     * Initialize the uploader with all the connection information.
     *
     * @param address  Server address
     * @param path     Remote path
     * @param username Username
     * @param password Password
     * @return Current instance of the uploader
     */
    public final OcUploader init(final String address, final String path,
                                 final String username, final String password) {
        remotePath = path;

        Uri serverUri = Uri.parse(address);
        client = OwnCloudClientFactory.createOwnCloudClient(
                serverUri, context, true);
        client.setCredentials(OwnCloudCredentialsFactory.newBasicCredentials(
                username, password));

        return this;
    }

    /**
     * Start the process. Execute the JsonFileWriter task to write JSON objects
     * into temporary JSON files.
     */
    public final void start() {
        new JsonFileWriter(context, this, jsonFiles).execute();
    }

    @Override
    public final void onJsonFilesWritten() {
        upload(jsonFiles.poll());
    }

    @Override
    public final void onWriteJsonFileFailed(final Exception exception) {
        callback.onWritingFileFailed(exception);
    }

    /**
     * Upload the file to the ownCloud server.
     *
     * @param jsonFile JSON file to upload
     */
    private void upload(final JsonFile jsonFile) {
        callback.onUploadStart(jsonFile);

        File fileToUpload = jsonFile.getFile();
        String path = buildRemotePath(jsonFile);
        String mimeType = "text/plain";
        Long timeStampLong = fileToUpload.lastModified() / 1000;
        String timeStamp = timeStampLong.toString();

        UploadRemoteFileOperation op = new UploadRemoteFileOperation(
                fileToUpload.getAbsolutePath(),
                path,
                mimeType,
                timeStamp);
        op.addDatatransferProgressListener(this);
        op.execute(client, this, handler);
    }

    /**
     * Build a valid remote path from the path given by the user.
     *
     * @param jsonFile JSON file used to build the remote path
     * @return Valid remote path
     */
    private String buildRemotePath(final JsonFile jsonFile) {
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
        return remotePath + jsonFile.getName() + JsonFile.FILE_EXT;
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
                Log.i(TAG, "Uploading... (" + percentage + "%)");
                callback.onUploading(percentage);
            }
        });
    }

    @Override
    public final void onRemoteOperationFinish(
            final RemoteOperation operation,
            final RemoteOperationResult result) {
        if (result.isSuccess()) {
            JsonFile jsonFile = jsonFiles.poll();
            if (jsonFile != null) {
                upload(jsonFile);
            } else {
                callback.onAllUploadComplete();
            }
        } else {
            Log.e(TAG, result.getLogMessage(), result.getException());
            callback.onUploadFailed(result.getLogMessage());
        }
    }

    /**
     * Listener to get notification from the OcUploader tasks.
     */
    public interface UploaderListener {

        /**
         * Called when the JSON file could not have been written.
         *
         * @param e Exception thrown during the task
         */
        void onWritingFileFailed(Exception e);

        /**
         * Called when an upload operation starts.
         *
         * @param jsonFile JSON file that is being uploaded
         */
        void onUploadStart(JsonFile jsonFile);

        /**
         * Called when the file is being uploaded.
         *
         * @param percentage Progress of the upload (percentage)
         */
        void onUploading(int percentage);

        /**
         * Called when all upload operations are completed.
         */
        void onAllUploadComplete();

        /**
         * Called when the upload failed.
         *
         * @param message Message describing the cause of the failure
         */
        void onUploadFailed(String message);
    }
}
