package com.virjar.vscrawler.core.selector.combine;

/**
 * Created by virjar on 2017/12/14.<br/>
 * rawText对于抽取节点api来说,可能永远不会使用,在面对html或者json的转化的时候,可能导致不必要的序列化工作,
 * 依靠此类实现rawText懒加载能力
 *
 * @author virjar
 * @since 0.2.1
 */
public interface RawTextStringFactory {
    String rawText();
}
