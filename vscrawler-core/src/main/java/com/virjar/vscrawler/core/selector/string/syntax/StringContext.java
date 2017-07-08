package com.virjar.vscrawler.core.selector.string.syntax;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by virjar on 17/7/8.
 */
@Data
@AllArgsConstructor
public class StringContext {
    private String baseUrl;
    private String data;
    private List<String> parent;
    private int index;
}
