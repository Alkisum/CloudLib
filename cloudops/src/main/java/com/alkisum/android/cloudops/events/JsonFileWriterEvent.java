package com.alkisum.android.cloudops.events;

import com.alkisum.android.cloudops.file.json.JsonFile;

import java.util.Queue;

/**
 * Class defining JsonFileWriter event for EventBus.
 *
 * @author Alkisum
 * @version 1.2
 * @since 1.2
 */
public class JsonFileWriterEvent extends FilteredEvent {

    /**
     * JSON file has been written successfully.
     */
    public static final int OK = 0;

    /**
     * JSON file could not be written.
     */
    public static final int ERROR = 1;

    /**
     * JSON file writer result.
     */
    private int result;

    /**
     * JSON files written.
     */
    private Queue<JsonFile> jsonFiles;

    /**
     * Exception thrown while writing JSON file, null if no exception has been
     * thrown.
     */
    private Exception exception;

    /**
     * JsonFileWriterEvent constructor.
     *
     * @param result        JSON file writer result
     * @param jsonFiles     JSON files written
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public JsonFileWriterEvent(final Integer[] subscriberIds, final int result,
                               final Queue<JsonFile> jsonFiles) {
        super(subscriberIds);
        this.result = result;
        this.jsonFiles = jsonFiles;
    }

    /**
     * JsonFileWriterEvent constructor.
     *
     * @param result        JSON file writer result
     * @param exception     Exception thrown while writing JSON file
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public JsonFileWriterEvent(final Integer[] subscriberIds, final int result,
                               final Exception exception) {
        super(subscriberIds);
        this.result = result;
        this.exception = exception;
    }

    /**
     * @return JSON file writer result
     */
    public int getResult() {
        return result;
    }

    /**
     * @return JSON files written
     */
    public Queue<JsonFile> getJsonFiles() {
        return jsonFiles;
    }

    /**
     * @return Exception thrown while writing JSON file, null if no exception
     * has been thrown
     */
    public Exception getException() {
        return exception;
    }
}
