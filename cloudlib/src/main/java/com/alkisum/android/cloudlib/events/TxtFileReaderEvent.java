package com.alkisum.android.cloudlib.events;

import com.alkisum.android.cloudlib.file.txt.TxtFile;

import java.util.List;

/**
 * Class defining TxtFileReader event for EventBus.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.3
 */
public class TxtFileReaderEvent extends FilteredEvent {

    /**
     * TXT file has been read successfully.
     */
    public static final int OK = 0;

    /**
     * TXT file could not be read.
     */
    public static final int ERROR = 1;

    /**
     * TXT file reader result.
     */
    private final int result;

    /**
     * TXT files read.
     */
    private List<TxtFile> txtFiles;

    /**
     * Exception thrown while reading TXT file, null if no exception has been
     * thrown.
     */
    private Exception exception;

    /**
     * TxtFileReaderEvent constructor.
     *
     * @param result        TXT file reader result
     * @param txtFiles      TXT files read
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public TxtFileReaderEvent(final Integer[] subscriberIds, final int result,
                              final List<TxtFile> txtFiles) {
        super(subscriberIds);
        this.result = result;
        this.txtFiles = txtFiles;
    }

    /**
     * TxtFileReaderEvent constructor.
     *
     * @param result        TXT file reader result
     * @param exception     Exception thrown while reading TXT file
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public TxtFileReaderEvent(final Integer[] subscriberIds, final int result,
                              final Exception exception) {
        super(subscriberIds);
        this.result = result;
        this.exception = exception;
    }

    /**
     * @return TXT file reader result
     */
    public int getResult() {
        return result;
    }

    /**
     * @return TXT files read
     */
    public List<TxtFile> getTxtFiles() {
        return txtFiles;
    }

    /**
     * @return Exception thrown while reading TXT file, null if no exception
     * has been thrown
     */
    public Exception getException() {
        return exception;
    }
}
