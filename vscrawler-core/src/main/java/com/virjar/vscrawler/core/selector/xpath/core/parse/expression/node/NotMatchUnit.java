package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node;

/**
 * Created by virjar on 17/6/10.
 */
@OpKey("!~")
public class NotMatchUnit extends BooleanRevertUnit {
    @Override
    String targetName() {
        return "~=";
    }
}
