package com.eunice.lib.reader;

import com.eunice.lib.reader._native.WinlogCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * A builder class for {@link WinlogReader}.
 * @version 1.0.0
 */
public final class WinlogReaderBuilder {

    private final List<SubScriptionChannel> subscriptions = new ArrayList<>();
    private boolean jsonEnabled = false;

    private WinlogCallback eventHandler;

    private ExecutorService executors;

    public WinlogReaderBuilder channel(SubScriptionChannel channel) {
        subscriptions.add(channel);
        return this;
    }

    public WinlogReaderBuilder executor(ExecutorService executor) {
        this.executors = executor;
        return this;
    }

    public WinlogReaderBuilder enableJson() {
        this.jsonEnabled = true;
        return this;
    }

    public WinlogReaderBuilder onEvent(WinlogCallback handler) {
        this.eventHandler = handler;
        return this;
    }

    public WinlogReader build() {
        if (eventHandler == null) throw new IllegalStateException("Event handler is not set");
        if (subscriptions.isEmpty()) throw new IllegalStateException("Channel is not set");
        if (executors == null) {
            executors = Executors.newSingleThreadExecutor();
        }
        return new WinlogReader(subscriptions, executors, eventHandler, jsonEnabled);
    }

}
