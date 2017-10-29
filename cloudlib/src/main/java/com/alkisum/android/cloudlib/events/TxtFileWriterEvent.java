package com.alkisum.android.cloudlib.events;

import com.alkisum.android.cloudlib.file.CloudFile;

import java.util.Queue;

/**
 * Class defining TxtFileWriter event for EventBus.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.3
 */
public class TxtFileWriterEvent extends FilteredEvent {

    /**
     * TXT file has been written successfully.
     */
    public static final int OK = 0;

    /**
     * TXT file could not be written.
     */
    public static final int ERROR = 1;

    /**
     * TXT file writer result.
     */
    private int result;

    /**
     * TXT files written.
     */
    private Queue<CloudFile> cloudFiles;

    /**
     * Exception thrown while writing TXT file, null if no exception has been
     * thrown.
     */
    private Exception exception;

    /**
     * TxtFileWriterEvent constructor.
     *
     * @param result        TXT file writer result
     * @param cloudFiles    TXT files written
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public TxtFileWriterEvent(final Integer[] subscriberIds, final int result,
                              final Queue<CloudFile> cloudFiles) {
        super(subscriberIds);
        this.result = result;
        this.cloudFiles = cloudFiles;
    }

    /**
     * TxtFileWriterEvent constructor.
     *
     * @param result        TXT file writer result
     * @param exception     Exception thrown while writing TXT file
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public TxtFileWriterEvent(final Integer[] subscriberIds, final int result,
                              final Exception exception) {
        super(subscriberIds);
        this.result = result;
        this.exception = exception;
    }

    /**
     * @return TXT file writer result
     */
    public int getResult() {
        return result;
    }

    /**
     * @return TXT files written
     */
    public Queue<CloudFile> getCloudFiles() {
        return cloudFiles;
    }

    /**
     * @return Exception thrown while writing TXT file, null if no exception
     * has been thrown
     */
    public Exception getException() {
        return exception;
    }
}
