package com.virjar.vscrawler.core.selector.xpath.parse;

import java.util.concurrent.ExecutionException;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XPathParser;
import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;
import com.virjar.vscrawler.core.selector.xpath.model.XpathEvaluator;

import lombok.Getter;

/**
 * Created by virjar on 17/6/9.
 */
public class XpathParser {
    private static volatile boolean cacheEnabled = true;
    private static LoadingCache<String, Optional<XpathEvaluator>> cache;
    @Getter
    private String xpathStr;
    private TokenQueue tokenQueue;

    public XpathParser(String xpathStr) {
        this.xpathStr = xpathStr;
        tokenQueue = new TokenQueue(xpathStr);
    }

    public static XpathEvaluator compile(String xpathStr) throws XpathSyntaxErrorException {
        if (cacheEnabled) {
            return fromCache(xpathStr);
        }
        return new XpathParser(xpathStr).parse();
    }

    public static XpathEvaluator compileNoError(String xpathStr) {
        try {
            return compile(xpathStr);
        } catch (XpathSyntaxErrorException e) {
            return new XpathEvaluator.AnanyseStartEvaluator();
        }
    }

    public XpathEvaluator parse() throws XpathSyntaxErrorException {
        XpathStateMachine xpathStateMachine = new XpathStateMachine(tokenQueue);
        while (xpathStateMachine.getState() != XpathStateMachine.BuilderState.END) {
            xpathStateMachine.getState().parse(xpathStateMachine);
        }
        return xpathStateMachine.getEvaluator();
    }

    private static XpathEvaluator fromCache(String xpathStr) throws XpathSyntaxErrorException {
        if (cache == null) {
            synchronized (XPathParser.class) {
                if (cache == null) {
                    cache = CacheBuilder.newBuilder().build(new CacheLoader<String, Optional<XpathEvaluator>>() {
                        @Override
                        public Optional<XpathEvaluator> load(String key) throws Exception {
                            try {
                                return Optional.of(new XpathParser(key).parse());
                            } catch (XpathSyntaxErrorException e) {
                                return Optional.absent();
                            }
                        }
                    });
                }
            }
        }

        try {
            Optional<XpathEvaluator> xpathEvaluatorOptional = cache.get(xpathStr);
            if (xpathEvaluatorOptional.isPresent()) {
                return xpathEvaluatorOptional.get();
            }

            throw new XpathSyntaxErrorException(0, "can not parse xpath str: " + xpathStr);
        } catch (ExecutionException e) {// 这个异常不会发生
            return new XpathParser(xpathStr).parse();
        }
    }

    public static void setCacheEnabled(boolean cacheEnabled) {
        XpathParser.cacheEnabled = cacheEnabled;
        if (!cacheEnabled && cache != null) {
            cache.invalidateAll();
        }
    }
}
