package com.virjar.vscrawler.core.selector.xpath.core.function.axis;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.virjar.vscrawler.core.selector.xpath.core.function.NameAware;

/**
 * Created by virjar on 17/6/6. 所有的轴都基于这个接口扩展 通过轴选出对应作用域的全部节点
 * 去掉不实用的轴，不支持namespace，attribute（可用@*代替），preceding(preceding-sibling支持)，following(following-sibling支持) 添加
 * preceding-sibling-one，following-sibling-one,即只选前一个或后一个兄弟节点，添加 sibling 选取全部兄弟节点
 *
 * @see "https://github.com/zhegexiaohuozi"
 */
public interface AxisFunction extends NameAware {
    Elements call(Element e, String... args);
}
