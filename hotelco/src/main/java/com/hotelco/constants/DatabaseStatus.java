package com.hotelco.constants;

/**
 * Represents database statuses. NOT_CONNECTED relates that the database is not
 * currently connected. READY relates that the database is ready for processing,
 * but not currently processing. PROCESSING relates that the database is
 * currently processing, and not ready for other transactions.
 * 
 * @author Daniel Schwartz
 */
public enum DatabaseStatus {
    /**
     * not currently connected
     */
    NOT_CONNECTED,
    /**
     * ready for processing requests, but not currently processing
     */
    READY,
    /**
     * processing a request and should not be interrupted
     */
    PROCESSING
}
