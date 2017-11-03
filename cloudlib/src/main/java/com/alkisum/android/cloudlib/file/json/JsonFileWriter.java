package com.alkisum.android.cloudlib.file.json;

import android.os.AsyncTask;

import com.alkisum.android.cloudlib.events.JsonFileWriterEvent;
import com.alkisum.android.cloudlib.file.CloudFile;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Task writing JSON objects into files.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.0
 */
public class JsonFileWriter extends AsyncTask<Void, Void, Void> {

    /**
     * Cache directory.
     */
    private final File cacheDir;

    /**
     * List of JSON objects to write to files.
     */
    private final List<JsonFile> jsonFiles;

    /**
     * Queue of CloudFile objects containing the JSON files to be uploaded.
     */
    private final Queue<CloudFile> cloudFiles;

    /**
     * Exception that can be set when an exception is caught during the task.
     */
    private Exception exception;

    /**
     * Subscriber ids allowed to process the events.
     */
    private final Integer[] subscriberIds;

    /**
     * JsonFileWriter constructor.
     *
     * @param cacheDir      Cache directory
     * @param jsonFiles     List of JSON files
     * @param subscriberIds Subscriber ids allowed to process the events
     */
    public JsonFileWriter(final File cacheDir,
                          final List<JsonFile> jsonFiles,
                          final Integer[] subscriberIds) {
        this.cacheDir = cacheDir;
        this.jsonFiles = jsonFiles;
        this.subscriberIds = subscriberIds;
        this.cloudFiles = new LinkedList<>();
    }

    @Override
    protected final Void doInBackground(final Void... params) {

        try {
            for (JsonFile jsonFile : jsonFiles) {
                // Create temporary file, its name does not matter
                File file = File.createTempFile(jsonFile.getBaseName(),
                        JsonFile.FILE_EXT, cacheDir);

                FileWriter writer = new FileWriter(file);
                writer.write(jsonFile.getJsonObject().toString(4));
                writer.flush();
                writer.close();

                jsonFile.setFile(file);

                cloudFiles.add(jsonFile);
            }
        } catch (IOException | JSONException e) {
            exception = e;
        }

        return null;
    }

    @Override
    protected final void onPostExecute(final Void param) {
        if (exception == null) {
            EventBus.getDefault().post(new JsonFileWriterEvent(subscriberIds,
                    JsonFileWriterEvent.OK, cloudFiles));
        } else {
            EventBus.getDefault().post(new JsonFileWriterEvent(subscriberIds,
                    JsonFileWriterEvent.ERROR, exception));
        }
    }
}
