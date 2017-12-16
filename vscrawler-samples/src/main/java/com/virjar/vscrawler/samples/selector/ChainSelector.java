package com.virjar.vscrawler.samples.selector;

import com.google.common.base.Charsets;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by virjar on 17/7/24.
 */
public class ChainSelector {
    public static void main(String[] args) throws IOException {
        AbstractSelectable selectable = AbstractSelectable.createModel("http://www.virjar.com",
                IOUtils.toString(HtmlJsonSelectorTest.class.getResourceAsStream("/select.html"), Charsets.UTF_8));

        List<String> model = selectable.xpath("/css('#nationality')::option/text()").stringRule("self()").createOrGetModel();
        for (String str : model) {
            System.out.println(str);
        }
    }
}
