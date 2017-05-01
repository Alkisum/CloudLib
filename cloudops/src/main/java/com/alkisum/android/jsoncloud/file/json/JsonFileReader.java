package com.alkisum.android.jsoncloud.file.json;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Task reading data from files and converting it to JSON objects.
 *
 * @author Alkisum
 * @version 1.0
 * @since 1.0
 */
public class JsonFileReader extends AsyncTask<Void, Void, List<JsonFile>> {

    /**
     * Listener to get notification when the task finishes.
     */
    private final JsonFileReaderListener callback;

    /**
     * List of files to read.
     */
    private final List<File> files;

    /**
     * Exception that can be set when an exception is caught during the task.
     */
    private Exception exception;

    /**
     * JsonFileReader constructor.
     *
     * @param callback Listener of the task
     * @param files    List of files to read
     */
    public JsonFileReader(final JsonFileReaderListener callback,
                          final List<File> files) {
        this.callback = callback;
        this.files = files;
    }

    @Override
    protected final List<JsonFile> doInBackground(final Void... params) {
        List<JsonFile> jsonFiles = new ArrayList<>();
        BufferedReader br = null;
        try {
            for (File file : files) {
                br = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }
                String jsonString = sb.toString();
                JsonFile jsonFile = new JsonFile(
                        file.getName(),
                        new JSONObject(jsonString),
                        file);
                jsonFiles.add(jsonFile);
            }
        } catch (IOException | JSONException e) {
            exception = e;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                exception = e;
            }
        }
        return jsonFiles;
    }

    @Override
    protected final void onPostExecute(final List<JsonFile> jsonFiles) {
        if (exception == null) {
            callback.onJsonFilesRead(jsonFiles);
        } else {
            callback.onReadJsonFileFailed(exception);
        }
    }

    /**
     * Listener for the JsonFileReader.
     */
    public interface JsonFileReaderListener {

        /**
         * Called when the files are read and the data converted to JSON
         * objects.
         *
         * @param jsonFiles List of JSON files
         */
        void onJsonFilesRead(List<JsonFile> jsonFiles);

        /**
         * Called when an exception has been caught during the task.
         *
         * @param exception Exception caught
         */
        void onReadJsonFileFailed(Exception exception);
    }
}
