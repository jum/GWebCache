/*
 * $Id$
 * This is an unpublished work copyright (c) 2004 Jens-Uwe Mager
 * 30177 Hannover, Germany, jum@anubis.han.de
 */

package org.mager.gwebcache;

public class BadResponseException extends Exception {

    public BadResponseException(int code, String message) {
        super(code + " " + message);
    }

}
