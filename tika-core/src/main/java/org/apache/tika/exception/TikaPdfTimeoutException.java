package org.apache.tika.exception;

/**
 * Tika pdf timeout exception when pdfbox timeout on parsing pdf.
 * 
 * @author zhangkun
 *
 */
public class TikaPdfTimeoutException extends TikaException {
    /**
     * Creates an instance of exception
     * @param msg message
     */
    public TikaPdfTimeoutException(String msg) {
        super(msg);
    }

    public TikaPdfTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
