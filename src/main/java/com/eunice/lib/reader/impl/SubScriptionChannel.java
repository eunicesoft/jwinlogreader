package com.eunice.lib.reader.impl;


import com.eunice.lib.reader.constant.SubScription;

public class SubScriptionChannel {

    private final String channel;
    private final String query;

    public SubScriptionChannel(String channel, String query) {
        this.channel = channel;
        this.query = query;
    }

    public String getChannel() {
        return channel;
    }

    public String getQuery() {
        return query;
    }

    public static SubScriptionChannel create(String channel, String query) {
        return new SubScriptionChannel(channel, query);
    }

    public static SubScriptionChannel create(SubScription channel, String query) {
        return new SubScriptionChannel(channel.getServiceName(), query);
    }

    public static SubScriptionChannel create(String channel) {
        return create(channel, null);
    }

    public static SubScriptionChannel create(SubScription channel) {
        return create(channel, null);
    }
}
