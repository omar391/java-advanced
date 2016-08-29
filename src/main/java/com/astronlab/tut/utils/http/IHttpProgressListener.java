package com.astronlab.tut.utils.http;

public interface IHttpProgressListener {

    String CONNECTING_MSG = "Connecting..";
    String FAILS_MSG = "Failed";
    String SUCCESSFUL_MSG = "Working";
    String RETRY_MSG = "Retrying..";

    void notifyListener(Object stateType, Object updateType, Object value);
    enum Status {
        FAILS, SUCCESS, RUNNING
    }

    enum UpdateType {
        DOWNLOAD, UPLOAD, STATUS, HEADER
    }
}