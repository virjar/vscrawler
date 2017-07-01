package com.virjar.vscrawler.core.selector.combine;

import java.util.List;

import com.virjar.sipsoup.model.SIPNode;
import com.virjar.sipsoup.model.XpathEvaluator;
import com.virjar.sipsoup.parse.XpathParser;
import com.virjar.vscrawler.core.selector.combine.convert.Converters;
import com.virjar.vscrawler.core.selector.combine.selectables.XpathNode;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by virjar on 17/6/30.
 */
public abstract class AbstractSelectable<M> {

    @Getter
    private String rowText;
    @Getter
    private String baseUrl;

    public AbstractSelectable(String rowText) {
        this(null, rowText);
    }

    public AbstractSelectable(String baseUrl, String rowText) {
        this.baseUrl = baseUrl;
        this.rowText = rowText;
    }

    @Setter
    protected M model;

    public abstract M createOrGetModel();

    protected <T extends AbstractSelectable> T covert(Class<T> mClass) {
        return Converters.findConvert(getClass(), mClass).convert(this);
    }

    public AbstractSelectable xpath(String xpathStr) {
        return xpath(XpathParser.compileNoError(xpathStr));
    }

    public AbstractSelectable xpath(XpathEvaluator xpathEvaluator) {
        List<SIPNode> sipNodes = xpathEvaluator.evaluate(covert(XpathNode.class).createOrGetModel());

        XpathNode xpathNode = new XpathNode(getBaseUrl(), getRowText());
        xpathNode.setModel(sipNodes);
        return xpathNode;
    }

}
