package com.eunice.lib.reader;


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

    public static SubScriptionChannel create(String channel) {
        return new SubScriptionChannel(channel, null);
    }
}
