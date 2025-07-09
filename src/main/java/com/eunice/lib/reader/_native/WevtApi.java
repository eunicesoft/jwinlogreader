package com.eunice.lib.reader._native;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface WevtApi extends StdCallLibrary {

    WevtApi INSTANCE = Native.load("wevtapi", WevtApi.class);

    int EVT_RENDER_EVENT_XML = 1;
    int EVT_SUBSCRIBE_TO_FUTURE_EVENTS = 1;

    Pointer EvtSubscribe(
            Pointer Session,
            Pointer SignalEvent,
            WString ChannelPath,
            WString Query,
            Pointer Bookmark,
            Pointer Context,
            SubscribeCallback Callback,
            int Flags
    );

    boolean EvtRender(
            Pointer Context,
            Pointer Fragment,
            int Flags,
            int BufferSize,
            Pointer Buffer,
            IntByReference BufferUsed,
            IntByReference PropertyCount
    );

    boolean EvtClose(Pointer Handle);
}
