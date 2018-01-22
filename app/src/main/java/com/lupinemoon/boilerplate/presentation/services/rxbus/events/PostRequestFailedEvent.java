package com.lupinemoon.boilerplate.presentation.services.rxbus.events;

import okhttp3.Request;

public class PostRequestFailedEvent {

    private Request request;

    public PostRequestFailedEvent(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

}
