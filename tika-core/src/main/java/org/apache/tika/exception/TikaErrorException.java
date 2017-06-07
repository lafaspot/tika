package org.apache.tika.exception;

/**
 * Tika Error Exception is an exception converted from Error.
 *
 * @author jingyidai
 */
public class TikaErrorException extends TikaException {
    /**
     * Creates an instance of exception
     * @param msg message
     */
    public TikaErrorException(String msg) {
        super(msg);
    }

    public TikaErrorException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
