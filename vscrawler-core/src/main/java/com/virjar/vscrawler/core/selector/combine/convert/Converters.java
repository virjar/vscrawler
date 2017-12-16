package com.virjar.vscrawler.core.selector.combine.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virjar.sipsoup.model.SIPNode;
import com.virjar.sipsoup.model.SipNodes;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import com.virjar.vscrawler.core.selector.combine.selectables.JsonNode;
import com.virjar.vscrawler.core.selector.combine.selectables.RawNode;
import com.virjar.vscrawler.core.selector.combine.selectables.StringNode;
import com.virjar.vscrawler.core.selector.combine.selectables.XpathNode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Map;

/**
 * Created by virjar on 17/6/30.
 */
public class Converters {
    private static Map<RegistryHolder, NodeConvert> nodeConvertMap = Maps.newHashMap();

    static {
        registerDefault();
    }

    private static void registerJson() {
        register(JsonNode.class, JsonNode.class, new NodeConvert<JsonNode, JsonNode>() {
            @Override
            public JsonNode convert(JsonNode from) {
                return from;
            }
        });

        register(RawNode.class, JsonNode.class, new NodeConvert<RawNode, JsonNode>() {
            @Override
            public JsonNode convert(RawNode from) {
                return new JsonNode(from.getBaseUrl(), from.getRawText());
            }
        });

        register(StringNode.class, JsonNode.class, new NodeConvert<StringNode, JsonNode>() {
            @Override
            public JsonNode convert(StringNode from) {
                JsonNode jsonNode = new JsonNode(from.getBaseUrl(), (String) null);
                jsonNode.setModel(Lists.transform(from.createOrGetModel(), new Function<String, JSON>() {
                    @Override
                    public JSON apply(String input) {
                        return (JSON) JSON.parse(input);
                    }
                }));
                return jsonNode;
            }
        });

        register(XpathNode.class, JsonNode.class, new NodeConvert<XpathNode, JsonNode>() {
            @Override
            public JsonNode convert(XpathNode from) {
                JsonNode ret = new JsonNode(from.getBaseUrl(), (String) null);
                ret.setModel(Lists.newLinkedList(
                        Iterables.transform(Iterables.filter(from.createOrGetModel(), new Predicate<SIPNode>() {
                            @Override
                            public boolean apply(SIPNode input) {
                                return input.isText();
                            }
                        }), new Function<SIPNode, JSON>() {
                            @Override
                            public JSON apply(SIPNode input) {
                                return (JSON) JSON.parse(input.getTextVal());
                            }
                        })));
                return ret;
            }
        });
    }

    private static void registerRaw() {
        register(RawNode.class, RawNode.class, new NodeConvert<RawNode, RawNode>() {
            @Override
            public RawNode convert(RawNode from) {
                return from;
            }
        });

        register(JsonNode.class, RawNode.class, new NodeConvert<JsonNode, RawNode>() {
            @Override
            public RawNode convert(JsonNode from) {
                List<JSON> jsons = from.createOrGetModel();
                if (jsons.size() == 1) {
                    return new RawNode(from.getBaseUrl(), jsons.get(1).toJSONString());
                }
                StringBuilder sb = new StringBuilder();
                for (JSON json : jsons) {
                    sb.append(json.toJSONString()).append(" ");
                }
                return new RawNode(from.getBaseUrl(), sb.toString());
            }
        });

        register(StringNode.class, RawNode.class, new NodeConvert<StringNode, RawNode>() {
            @Override
            public RawNode convert(StringNode from) {
                return new RawNode(from.getBaseUrl(), StringUtils.join(from.createOrGetModel(), " "));
            }
        });

        register(XpathNode.class, RawNode.class, new NodeConvert<XpathNode, RawNode>() {
            @Override
            public RawNode convert(XpathNode from) {
                return new RawNode(from.getBaseUrl(),
                        StringUtils.join(Iterables.transform(from.createOrGetModel(), new Function<SIPNode, String>() {
                            @Override
                            public String apply(SIPNode input) {
                                return input.isText() ? input.getTextVal() : input.getElement().html();
                            }
                        }), " "));
            }
        });
    }

    private static void registerString() {

        register(StringNode.class, StringNode.class, new NodeConvert<StringNode, StringNode>() {
            @Override
            public StringNode convert(StringNode from) {
                return from;
            }
        });

        register(JsonNode.class, StringNode.class, new NodeConvert<JsonNode, StringNode>() {

            private List<String> genDefault(List<JSON> fromModel) {
                return Lists.transform(fromModel, new Function<JSON, String>() {
                    @Override
                    public String apply(JSON input) {
                        return input.toJSONString();
                    }
                });
            }

            @Override
            public StringNode convert(JsonNode from) {
                StringNode ret = new StringNode(from.getBaseUrl(), null);

                List<String> tempRet = Lists.newLinkedList();
                List<JSON> fromModel = from.createOrGetModel();
                for (JSON json : fromModel) {
                    if (json instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) json;
                        for (Object o : jsonArray) {
                            if (o instanceof CharSequence) {
                                tempRet.add(o.toString());
                            } else {
                                ret.setModel(genDefault(fromModel));
                                return ret;
                            }
                        }
                    } else {
                        ret.setModel(genDefault(fromModel));
                        return ret;
                    }
                }
                ret.setModel(tempRet);
                return ret;
            }
        });

        register(RawNode.class, StringNode.class, new NodeConvert<RawNode, StringNode>() {
            @Override
            public StringNode convert(RawNode from) {
                return new StringNode(from.getBaseUrl(), from.createOrGetModel());
            }
        });

        register(XpathNode.class, StringNode.class, new NodeConvert<XpathNode, StringNode>() {
            @Override
            public StringNode convert(XpathNode from) {
                StringNode ret = new StringNode(from.getBaseUrl(), null);
                ret.setModel(Lists.transform(from.createOrGetModel(), new Function<SIPNode, String>() {
                    @Override
                    public String apply(SIPNode input) {
                        return input.isText() ? input.getTextVal() : input.getElement().html();
                    }
                }));
                return ret;
            }
        });
    }

    private static void registerXpath() {
        register(XpathNode.class, XpathNode.class, new NodeConvert<XpathNode, XpathNode>() {
            @Override
            public XpathNode convert(XpathNode from) {
                return from;
            }
        });

        register(JsonNode.class, XpathNode.class, new NodeConvert<JsonNode, XpathNode>() {
            @Override
            public XpathNode convert(JsonNode from) {
                throw new UnsupportedOperationException("can not cover json to xpath");
            }
        });

        register(RawNode.class, XpathNode.class, new NodeConvert<RawNode, XpathNode>() {
            @Override
            public XpathNode convert(RawNode from) {
                return new XpathNode(from.getBaseUrl(), from.getRawText());
            }
        });

        register(StringNode.class, XpathNode.class, new NodeConvert<StringNode, XpathNode>() {
            @Override
            public XpathNode convert(final StringNode from) {
                XpathNode ret = new XpathNode(from.getBaseUrl(), (String) null);
                ret.setModel(new SipNodes(Lists
                        .newLinkedList(Iterables.transform(from.createOrGetModel(), new Function<String, SIPNode>() {
                            @Override
                            public SIPNode apply(String input) {
                                try {
                                    Document document = Jsoup.parse(input, from.getBaseUrl());
                                    if (document != null) {
                                        return SIPNode.e(document);
                                    }
                                } catch (Exception e) {
                                    // do nothing
                                }
                                return SIPNode.t(input);
                            }
                        }))));
                return ret;
            }
        });
    }

    private static void registerDefault() {
        registerJson();
        registerRaw();
        registerString();
        registerXpath();
    }

    public static <F extends AbstractSelectable, T extends AbstractSelectable> void register(Class<F> from, Class<T> to,
                                                                                             NodeConvert<F, T> nodeConvert) {
        nodeConvertMap.put(new RegistryHolder(from, to), nodeConvert);
    }

    @SuppressWarnings("unchecked")
    public static <F extends AbstractSelectable, T extends AbstractSelectable> NodeConvert<F, T> findConvert(
            Class<? extends AbstractSelectable> from, Class<T> to) {
        return nodeConvertMap.get(new RegistryHolder(from, to));
    }

    private static class RegistryHolder {
        RegistryHolder(Class<? extends AbstractSelectable> from, Class<? extends AbstractSelectable> to) {
            this.from = from;
            this.to = to;
        }

        @Getter
        private Class<? extends AbstractSelectable> from;
        @Getter
        private Class<? extends AbstractSelectable> to;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            RegistryHolder that = (RegistryHolder) o;

            if (from != null ? !from.equals(that.from) : that.from != null)
                return false;
            return to != null ? to.equals(that.to) : that.to == null;

        }

        @Override
        public int hashCode() {
            int result = from != null ? from.hashCode() : 0;
            result = 31 * result + (to != null ? to.hashCode() : 0);
            return result;
        }
    }

}
