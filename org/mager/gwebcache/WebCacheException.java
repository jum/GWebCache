/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

/**
 * The exception thrown if some error occurs during processing
 * a web cache request. This exception is caught and the message
 * is displayed to the client. 
 */
public class WebCacheException extends Exception {

    public WebCacheException(String message) {
        super(message);
    }

    public WebCacheException(String message, Throwable cause) {
        super(message, cause);
    }

}
