package com.eunice.lib.reader;

import com.eunice.lib.reader._native.SubscribeCallback;
import com.eunice.lib.reader._native.WevtApi;
import com.eunice.lib.reader._native.WinlogCallback;
import com.eunice.lib.reader.module.JsonParser;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * A class responsible for managing reading and subscription to Windows Event Logs.
 * Provides functionality to start and stop listening to specific channels and
 * invokes callbacks for events and errors.
 * @version 1.0.0
 * @see WinlogReaderBuilder
 */
public class WinlogReader {

    private final Logger logger = LoggerFactory.getLogger(WinlogReader.class);

    private final WinlogCallback callback;
    private final ExecutorService executorService;
    private boolean running = false;
    private final boolean jsonEnabled;

    private final List<SubScriptionChannel> subscriptionList;

    private JsonParser jsonParser;
    private SubscribeCallback nativeCallback;

    private static final class NativeSubscription {
        final SubScriptionChannel channel;
        Pointer subscription;
        Memory contextPtr;

        NativeSubscription(SubScriptionChannel channel) {
            this.channel = channel;
        }
    }

    private final Map<Long, NativeSubscription> eventMap = new ConcurrentHashMap<>();

    public static WinlogReaderBuilder builder() {
        return new WinlogReaderBuilder();
    }

    protected WinlogReader(List<SubScriptionChannel> subscriptionList, ExecutorService executorService, WinlogCallback callback, boolean jsonEnabled) {
        this.subscriptionList = subscriptionList;
        this.executorService = executorService;
        this.callback = callback;
        this.jsonEnabled = jsonEnabled;

        if (jsonEnabled) {
            jsonParser = new JsonParser();
        }
    }


    public void start() throws Exception {
        if (running) return;
        running = true;
        logger.info("start winlog reader");
        this.nativeCallback = (action, context, eventHandle) -> {
            long key = Pointer.nativeValue(context);
            NativeSubscription nativeSub = eventMap.get(key);

            if (nativeSub == null) {
                this.callback.onError("READER_ERROR", WinlogError.nativeError(-1));
                return 0;
            }

            if (action == SubscribeCallback.EVT_SUBSCRIBE_NOTIFY_ACTION_DELIVER) {
                String text = renderEventXml(eventHandle);
                if (jsonEnabled) {
                    try {
                        text = jsonParser.normalize(text);
                    } catch (IOException e) {
                        this.executorService.submit(() -> this.callback.onError(nativeSub.channel.getChannel(), WinlogError.exception(e)));
                    }
                }

                if (text != null) {
                    String finalText = text;
                    this.executorService.submit(() -> this.callback.onEvent(nativeSub.channel.getChannel(), finalText));
                } else {
                    this.executorService.submit(() -> this.callback.onError(nativeSub.channel.getChannel(), WinlogError.nativeError(-1)));
                }

            } else if (action == SubscribeCallback.EVT_SUBSCRIBE_NOTIFY_ACTION_ERROR) {
                int lastError = Native.getLastError();
                this.executorService.submit(() -> this.callback.onError(nativeSub.channel.getChannel(), WinlogError.nativeError(lastError)));
            }

            return 0;
        };

        for (SubScriptionChannel subScriptionChannel : this.subscriptionList) {
            NativeSubscription nativeSub = new NativeSubscription(subScriptionChannel);

            Memory ctxPtr = new Memory(Native.POINTER_SIZE);
            ctxPtr.clear();
            long key = Pointer.nativeValue(ctxPtr);

            nativeSub.contextPtr = ctxPtr;
            eventMap.put(key, nativeSub);

            Pointer subscription = WevtApi.INSTANCE.EvtSubscribe(
                    Pointer.NULL,
                    Pointer.NULL,
                    new WString(subScriptionChannel.getChannel()),
                    subScriptionChannel.getQuery() != null ? new WString(subScriptionChannel.getQuery()) : null,
                    Pointer.NULL,
                    ctxPtr,
                    this.nativeCallback,
                    WevtApi.EVT_SUBSCRIBE_TO_FUTURE_EVENTS
            );

            if (subscription == Pointer.NULL) {
                throw new IllegalAccessException("not permitted to subscribe because administrator denied access.");
            }

            nativeSub.subscription = subscription;
        }
    }

    public void close() {
        if (!running) return;
        running = false;

        for (NativeSubscription nativeSub : this.eventMap.values()) {
            if (nativeSub.subscription != null) {
                WevtApi.INSTANCE.EvtClose(nativeSub.subscription);
            }
        }

        this.executorService.shutdown();
        this.nativeCallback = null;
    }

    private String renderEventXml(Pointer eventHandle) {

        IntByReference used = new IntByReference();
        IntByReference count = new IntByReference();

        // 1차: 필요한 버퍼 크기 (bytes)
        WevtApi.INSTANCE.EvtRender(
                Pointer.NULL,
                eventHandle,
                WevtApi.EVT_RENDER_EVENT_XML,
                0,
                Pointer.NULL,
                used,
                count
        );

        int bytes = used.getValue();
        if (bytes <= 0) return null;

        Memory buffer = new Memory(bytes);

        boolean ok = WevtApi.INSTANCE.EvtRender(
                Pointer.NULL,
                eventHandle,
                WevtApi.EVT_RENDER_EVENT_XML,
                bytes,
                buffer,
                used,
                count
        );

        if (!ok) return null;

        int charLen = bytes / 2;

        char[] chars = buffer.getCharArray(0, charLen);

        int realLen = 0;
        for (char aChar : chars) {
            if (aChar == 0) break;
            realLen++;
        }

        return new String(chars, 0, realLen)
                .replaceAll("\uFFFD+", "");

    }

}
