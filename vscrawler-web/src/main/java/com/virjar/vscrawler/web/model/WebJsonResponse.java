package com.virjar.vscrawler.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by virjar on 2018/2/3.
 */
public class WebJsonResponse<T> {

    public final int status;
    public final String message;
    public final T data;

    @JsonCreator
    public WebJsonResponse(@JsonProperty("status") int status,
                           @JsonProperty("message") String message,
                           @JsonProperty("data") T data) {

        this.status = status;
        this.message = message;
        this.data = data;
    }
}
