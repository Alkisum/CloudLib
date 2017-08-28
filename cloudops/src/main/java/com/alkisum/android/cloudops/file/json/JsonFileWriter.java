package com.alkisum.android.cloudops.file.json;

import android.content.Context;
import android.os.AsyncTask;

import com.alkisum.android.cloudops.events.JsonFileWriterEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;

/**
 * Task writing JSON objects into files.
 *
 * @author Alkisum
 * @version 1.2
 * @since 1.0
 */
public class JsonFileWriter extends AsyncTask<Void, Void, Void> {

    /**
     * Context.
     */
    private final Context context;

    /**
     * List of JSON objects to write to files.
     */
    private final Queue<JsonFile> jsonFiles;

    /**
     * Exception that can be set when an exception is caught during the task.
     */
    private Exception exception;

    /**
     * Subscriber ids allowed to process the events.
     */
    private Integer[] subscriberIds;

    /**
     * JsonFileWriter constructor.
     *
     * @param context       Context
     * @param jsonFiles     List of JSON files
     * @param subscriberIds Subscriber ids allowed to process the events
     */
    public JsonFileWriter(final Context context,
                          final Queue<JsonFile> jsonFiles,
                          final Integer[] subscriberIds) {
        this.context = context;
        this.jsonFiles = jsonFiles;
        this.subscriberIds = subscriberIds;
    }

    @Override
    protected final Void doInBackground(final Void... params) {

        try {
            for (JsonFile jsonFile : jsonFiles) {
                // Create temporary file, its name does not matter
                File file = File.createTempFile(jsonFile.getBaseName(),
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
            EventBus.getDefault().post(new JsonFileWriterEvent(subscriberIds,
                    JsonFileWriterEvent.OK, jsonFiles));
        } else {
            EventBus.getDefault().post(new JsonFileWriterEvent(subscriberIds,
                    JsonFileWriterEvent.ERROR, exception));
        }
    }
}
