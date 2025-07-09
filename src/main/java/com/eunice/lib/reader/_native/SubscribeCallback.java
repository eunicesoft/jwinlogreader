package com.eunice.lib.reader._native;

import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public interface SubscribeCallback extends StdCallLibrary.StdCallCallback {
    int EVT_SUBSCRIBE_NOTIFY_ACTION_DELIVER = 1;
    int EVT_SUBSCRIBE_NOTIFY_ACTION_ERROR = 2;

    int invoke(int Action, Pointer UserContext, Pointer EventHandle) throws RuntimeException;
}
