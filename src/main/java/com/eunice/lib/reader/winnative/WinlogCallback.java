package com.eunice.lib.reader.winnative;

import com.eunice.lib.reader.impl.WinlogError;

public interface WinlogCallback {
    void onEvent(String channel, String event);
    void onError(String channel, WinlogError err);
}
