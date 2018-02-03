package com.virjar.vscrawler.web.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by virjar on 2018/2/3.<br>
 * 前端返回统一数据结构
 */
@Data
public class WebJsonResponse<T> {

    private int status;
    private  String message;
    private  T data;

    @JsonCreator
    public WebJsonResponse(@JsonProperty("status") int status,
                           @JsonProperty("message") String message,
                           @JsonProperty("data") T data) {

        this.status = status;
        this.message = message;
        this.data = data;
    }
}
