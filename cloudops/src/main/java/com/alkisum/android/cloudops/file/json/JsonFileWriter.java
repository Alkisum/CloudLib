package com.alkisum.android.cloudops.file.json;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;

/**
 * Task writing JSON objects into files.
 *
 * @author Alkisum
 * @version 1.1
 * @since 1.0
 */
public class JsonFileWriter extends AsyncTask<Void, Void, Void> {

    /**
     * Context.
     */
    private final Context context;

    /**
     * Listener to get notification when the task finishes.
     */
    private final JsonFileWriterListener callback;

    /**
     * List of JSON objects to write to files.
     */
    private final Queue<JsonFile> jsonFiles;

    /**
     * Exception that can be set when an exception is caught during the task.
     */
    private Exception exception;

    /**
     * JsonFileWriter constructor.
     *
     * @param context   Context
     * @param callback  Listener of the task
     * @param jsonFiles List of JSON files
     */
    public JsonFileWriter(final Context context,
                          final JsonFileWriterListener callback,
                          final Queue<JsonFile> jsonFiles) {
        this.context = context;
        this.callback = callback;
        this.jsonFiles = jsonFiles;
    }

    @Override
    protected final Void doInBackground(final Void... params) {

        try {
            for (JsonFile jsonFile : jsonFiles) {
                // Create temporary file, its name does not matter
                File file = File.createTempFile("_" + jsonFile.getBaseName(),
                        JsonFile.FILE_EXT, context.getCacheDir());

                FileWriter writer = new FileWriter(file);
                writer.write(jsonFile.getJsonObject().toString(4));
                writer.flush();
                writer.close();

                jsonFile.setFile(file);
            }
        } catch (IOException | JSONException e) {
            exception = e;
        }

        return null;
    }

    @Override
    protected final void onPostExecute(final Void param) {
        if (exception == null) {
            callback.onJsonFilesWritten();
        } else {
            callback.onWriteJsonFileFailed(exception);
        }
    }

    /**
     * Listener for the JsonFileWriter.
     */
    public interface JsonFileWriterListener {

        /**
         * Called when the JSON objects have been written into the files.
         */
        void onJsonFilesWritten();

        /**
         * Called when an exception has been caught during the task.
         *
         * @param exception Exception caught
         */
        void onWriteJsonFileFailed(Exception exception);
    }
}
