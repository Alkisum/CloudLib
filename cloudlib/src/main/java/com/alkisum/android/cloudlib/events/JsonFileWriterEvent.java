package com.alkisum.android.cloudlib.events;

import com.alkisum.android.cloudlib.file.CloudFile;

import java.util.Queue;

/**
 * Class defining JsonFileWriter event for EventBus.
 *
 * @author Alkisum
 * @version 1.3
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
    private final int result;

    /**
     * JSON files written.
     */
    private Queue<CloudFile> cloudFiles;

    /**
     * Exception thrown while writing JSON file, null if no exception has been
     * thrown.
     */
    private Exception exception;

    /**
     * JsonFileWriterEvent constructor.
     *
     * @param result        JSON file writer result
     * @param cloudFiles    JSON files written
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public JsonFileWriterEvent(final Integer[] subscriberIds, final int result,
                               final Queue<CloudFile> cloudFiles) {
        super(subscriberIds);
        this.result = result;
        this.cloudFiles = cloudFiles;
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
    public Queue<CloudFile> getCloudFiles() {
        return cloudFiles;
    }

    /**
     * @return Exception thrown while writing JSON file, null if no exception
     * has been thrown
     */
    public Exception getException() {
        return exception;
    }
}
