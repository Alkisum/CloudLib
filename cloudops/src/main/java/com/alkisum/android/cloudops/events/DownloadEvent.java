package com.alkisum.android.cloudops.events;

import java.io.File;
import java.util.List;

/**
 * Class defining download event for EventBus.
 *
 * @author Alkisum
 * @version 1.2
 * @since 1.2
 */
public class DownloadEvent extends FilteredEvent {

    /**
     * Download operation finished with errors.
     */
    public static final int ERROR = 0;

    /**
     * Download operation finished successfully.
     */
    public static final int OK = 1;

    /**
     * No file to download.
     */
    public static final int NO_FILE = 2;

    /**
     * Files are being downloaded.
     */
    public static final int DOWNLOADING = 3;

    /**
     * Download operation result.
     */
    private int result;

    /**
     * Error message.
     */
    private String message;

    /**
     * Files downloaded.
     */
    private List<File> files;

    /**
     * DownloadEvent constructor.
     *
     * @param result        Download operation result
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public DownloadEvent(final Integer[] subscriberIds, final int result) {
        super(subscriberIds);
        this.result = result;
    }

    /**
     * DownloadEvent constructor.
     *
     * @param result        Download operation result
     * @param message       Error message
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public DownloadEvent(final Integer[] subscriberIds, final int result,
                         final String message) {
        super(subscriberIds);
        this.result = result;
        this.message = message;
    }

    /**
     * DownloadEvent constructor.
     *
     * @param result        Download operation result
     * @param files         Files downloaded
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public DownloadEvent(final Integer[] subscriberIds, final int result,
                         final List<File> files) {
        super(subscriberIds);
        this.result = result;
        this.files = files;
    }

    /**
     * @return Download operation result
     */
    public final int getResult() {
        return result;
    }

    /**
     * @return Error message
     */
    public final String getMessage() {
        return message;
    }

    /**
     * @return Files downloaded
     */
    public final List<File> getFiles() {
        return files;
    }
}
