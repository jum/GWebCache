/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

public class WebCacheException extends Exception {

    public WebCacheException(String message) {
        super(message);
    }

    public WebCacheException(String message, Throwable cause) {
        super(message, cause);
    }

}
