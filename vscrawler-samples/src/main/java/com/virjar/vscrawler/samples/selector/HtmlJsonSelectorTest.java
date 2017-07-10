package com.virjar.vscrawler.samples.selector;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import com.virjar.vscrawler.core.selector.combine.Selector;

/**
 * Created by virjar on 17/7/10.
 */
public class HtmlJsonSelectorTest {
    public static void main(String[] args) throws IOException {

        AbstractSelectable selectable = Selector.rawText("http://www.virjar.com",
                IOUtils.toString(HtmlJsonSelectorTest.class.getResourceAsStream("/htmljson.html"), Charsets.UTF_8));

        List<String> allDepartureDate = selectable.css("#testid pre").xpath("/text()")
                .jsonPath("$.bookingVoyages[0:].bookingFlights[0:].departureDate").stringRule("self()")
                .createOrGetModel();

        System.out.println(Joiner.on(",").join(allDepartureDate));
    }
}
