package com.alkisum.android.cloudops.events;

import com.alkisum.android.cloudops.file.json.JsonFile;

import java.util.List;

/**
 * Class defining JsonFileReader event for EventBus.
 *
 * @author Alkisum
 * @version 1.2
 * @since 1.2
 */
public class JsonFileReaderEvent extends FilteredEvent {

    /**
     * JSON file has been read successfully.
     */
    public static final int OK = 0;

    /**
     * JSON file could not be read.
     */
    public static final int ERROR = 1;

    /**
     * JSON file reader result.
     */
    private int result;

    /**
     * JSON files read.
     */
    private List<JsonFile> jsonFiles;

    /**
     * Exception thrown while reading JSON file, null if no exception has been
     * thrown.
     */
    private Exception exception;

    /**
     * JsonFileReaderEvent constructor.
     *
     * @param result        JSON file reader result
     * @param jsonFiles     JSON files read
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public JsonFileReaderEvent(final Integer[] subscriberIds, final int result,
                               final List<JsonFile> jsonFiles) {
        super(subscriberIds);
        this.result = result;
        this.jsonFiles = jsonFiles;
    }

    /**
     * JsonFileReaderEvent constructor.
     *
     * @param result        JSON file reader result
     * @param exception     Exception thrown while reading JSON file
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public JsonFileReaderEvent(final Integer[] subscriberIds, final int result,
                               final Exception exception) {
        super(subscriberIds);
        this.result = result;
        this.exception = exception;
    }

    /**
     * @return JSON file reader result
     */
    public int getResult() {
        return result;
    }

    /**
     * @return JSON files read
     */
    public List<JsonFile> getJsonFiles() {
        return jsonFiles;
    }

    /**
     * @return Exception thrown while reading JSON file, null if no exception
     * has been thrown
     */
    public Exception getException() {
        return exception;
    }
}
