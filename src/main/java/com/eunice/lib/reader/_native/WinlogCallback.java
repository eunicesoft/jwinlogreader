package com.eunice.lib.reader._native;

import com.eunice.lib.reader.WinlogError;

public interface WinlogCallback {
    void onEvent(String channel, String event);
    void onError(String channel, WinlogError err);
}
