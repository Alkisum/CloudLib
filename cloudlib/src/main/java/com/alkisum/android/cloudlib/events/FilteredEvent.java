package com.alkisum.android.cloudlib.events;

import java.util.Arrays;

/**
 * Base class for events using subscriber ids to allow event to be processed.
 *
 * @author Alkisum
 * @version 1.3
 * @since 1.2
 */
public class FilteredEvent {

    /**
     * Subscriber ids allowed to process the event.
     */
    private final Integer[] subscriberIds;

    /**
     * FilteredEvent constructor.
     *
     * @param subscriberIds Subscriber ids allowed to process the event
     */
    public FilteredEvent(final Integer[] subscriberIds) {
        this.subscriberIds = subscriberIds;
    }

    /**
     * Check  if the given subscriber is allowed to process the event.
     *
     * @param subscriberId Subscriber id to check
     * @return true if allowed, false otherwise
     */
    public boolean isSubscriberAllowed(final int subscriberId) {
        return subscriberIds == null
                || Arrays.asList(subscriberIds).contains(subscriberId);
    }
}
