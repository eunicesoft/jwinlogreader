package com.eunice.lib.reader;

import com.eunice.lib.reader.constant.SubScription;
import com.eunice.lib.reader.impl.SubScriptionChannel;
import com.eunice.lib.reader.impl.WinlogError;
import com.eunice.lib.reader.impl.WinlogReader;
import com.eunice.lib.reader.winnative.WinlogCallback;

import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws Exception {
        WinlogReader build = WinlogReader.builder()
                .channel(SubScriptionChannel.create("Microsoft-Windows-Sysmon/Operational"))
                .channel(SubScriptionChannel.create(SubScription.APPLICATION))
                .executor(Executors.newSingleThreadExecutor())
                .enableJson()
                .onEvent(new WinlogCallback() {
                    @Override
                    public void onEvent(String channel, String event) {
                        System.out.println(event);
                        System.out.println("\n");
                    }

                    @Override
                    public void onError(String channel, WinlogError err) {

                    }
                })
                .build();
        build.start();

        Thread.currentThread().join();

        Runtime.getRuntime().addShutdownHook(new Thread(build::close));
    }
}
