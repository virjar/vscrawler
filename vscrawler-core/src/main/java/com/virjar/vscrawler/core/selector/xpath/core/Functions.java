package com.virjar.vscrawler.core.selector.xpath.core;
/*
 * Copyright 2014 Wang Haomiao<et.tw@163.com> Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */

/**
 * xpath解析器的支持的全部函数集合，如需扩展按形式添加即可
 * 
 * @author: github.com/zhegexiaohuozi [seimimaster@gmail.com] Date: 14-3-15
 */
public class Functions {

    /**
     * ===================== 下面是用于过滤器的函数
     */

    /**
     * 判断是否包含
     * 
     * @param left
     * @param right
     * @return
     */
    public boolean contains(String left, String right) {
        return left.contains(right);
    }

}
