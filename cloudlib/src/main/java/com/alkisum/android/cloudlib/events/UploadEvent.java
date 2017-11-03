package com.alkisum.android.cloudlib.events;

/**
 * Class defining upload event for EventBus.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.2
 */
public class UploadEvent extends FilteredEvent {

    /**
     * Upload operation finished with errors.
     */
    public static final int ERROR = 0;

    /**
     * Upload operation finished successfully.
     */
    public static final int OK = 1;

    /**
     * Files are being uploaded.
     */
    public static final int UPLOADING = 2;

    /**
     * Upload operation result.
     */
    private final int result;

    /**
     * Error message.
     */
    private String message;

    /**
     * UploadEvent constructor.
     *
     * @param result        Upload operation result
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public UploadEvent(final Integer[] subscriberIds, final int result) {
        super(subscriberIds);
        this.result = result;
    }

    /**
     * UploadEvent constructor.
     *
     * @param result        Upload operation result
     * @param message       Error message
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public UploadEvent(final Integer[] subscriberIds, final int result,
                       final String message) {
        super(subscriberIds);
        this.result = result;
        this.message = message;
    }

    /**
     * @return Upload operation result
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
}
