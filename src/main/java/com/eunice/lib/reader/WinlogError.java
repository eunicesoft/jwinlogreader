package com.eunice.lib.reader;

public class WinlogError {
    private int errorCode;
    private Exception exception;

    public WinlogError(int errorCode, Exception exception) {
        this.errorCode = errorCode;
        this.exception = exception;
    }

    public static WinlogError nativeError(int errorCode) {
        return new WinlogError(errorCode, null);
    }

    public static WinlogError exception(Exception exception) {
        return new WinlogError(-1, exception);
    }


    public Exception getException() {
        return exception;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
