package com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.consumer;

import java.util.List;

import com.virjar.vscrawler.core.selector.xpath.core.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.OperatorEnv;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.Token;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.token.TokenConsumer;

/**
 * Created by virjar on 17/6/12.
 */
public class OperatorConsumer implements TokenConsumer {
    @Override
    public String consume(TokenQueue tokenQueue) {
        List<OperatorEnv.AlgorithmHolder> algorithmHolders = OperatorEnv.allAlgorithmUnitList();
        for (OperatorEnv.AlgorithmHolder holder : algorithmHolders) {
            if (tokenQueue.matches(holder.getKey())) {
                tokenQueue.consume(holder.getKey());
                return holder.getKey();
            }
        }
        return null;
    }

    @Override
    public int order() {
        return 40;
    }

    @Override
    public String tokenType() {
        return Token.OPERATOR;
    }
}
