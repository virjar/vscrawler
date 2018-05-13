package com.virjar.vscrawler.web.springboot.util;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public abstract class SystemPropertyUtils {
    public static final String PLACEHOLDER_PREFIX = "${";
    public static final String PLACEHOLDER_SUFFIX = "}";
    public static final String VALUE_SEPARATOR = ":";
    private static final String SIMPLE_PREFIX = "${".substring(1);

    public static String resolvePlaceholders(String text) {
        return text == null ? null : parseStringValue(null, text, text, new HashSet<String>());
    }

    public static String resolvePlaceholders(Properties properties, String text) {
        return text == null ? null : parseStringValue(properties, text, text, new HashSet<String>());
    }

    private static String parseStringValue(Properties properties, String value, String current, Set<String> visitedPlaceholders) {
        StringBuilder buf = new StringBuilder(current);
        int startIndex = current.indexOf("${");

        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(buf, startIndex);
            if (endIndex != -1) {
                String placeholder = buf.substring(startIndex + "${".length(), endIndex);
                String originalPlaceholder = placeholder;
                if (!visitedPlaceholders.add(placeholder)) {
                    throw new IllegalArgumentException("Circular placeholder reference '" + placeholder + "' in property definitions");
                }

                placeholder = parseStringValue(properties, value, placeholder, visitedPlaceholders);
                String propVal = resolvePlaceholder(properties, value, placeholder);
                if (propVal == null) {
                    int separatorIndex = placeholder.indexOf(":");
                    if (separatorIndex != -1) {
                        String actualPlaceholder = placeholder.substring(0, separatorIndex);
                        String defaultValue = placeholder.substring(separatorIndex + ":".length());
                        propVal = resolvePlaceholder(properties, value, actualPlaceholder);
                        if (propVal == null) {
                            propVal = defaultValue;
                        }
                    }
                }

                if (propVal != null) {
                    propVal = parseStringValue(properties, value, propVal, visitedPlaceholders);
                    buf.replace(startIndex, endIndex + "}".length(), propVal);
                    startIndex = buf.indexOf("${", startIndex + propVal.length());
                } else {
                    startIndex = buf.indexOf("${", endIndex + "}".length());
                }

                visitedPlaceholders.remove(originalPlaceholder);
            } else {
                startIndex = -1;
            }
        }

        return buf.toString();
    }

    private static String resolvePlaceholder(Properties properties, String text, String placeholderName) {
        String propVal = getProperty(placeholderName, null, text);
        if (propVal != null) {
            return propVal;
        } else {
            return properties == null ? null : properties.getProperty(placeholderName);
        }
    }

    public static String getProperty(String key) {
        return getProperty(key, null, "");
    }

    public static String getProperty(String key, String defaultValue) {
        return getProperty(key, defaultValue, "");
    }

    private static String getProperty(String key, String defaultValue, String text) {
        try {
            String propVal = System.getProperty(key);
            if (propVal == null) {
                propVal = System.getenv(key);
            }

            if (propVal == null) {
                propVal = System.getenv(key.replace('.', '_'));
            }

            if (propVal == null) {
                propVal = System.getenv(key.toUpperCase().replace('.', '_'));
            }

            if (propVal != null) {
                return propVal;
            }
        } catch (Throwable var4) {
            System.err.println("Could not resolve key '" + key + "' in '" + text + "' as system property or in environment: " + var4);
        }

        return defaultValue;
    }

    private static int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + "${".length();
        int withinNestedPlaceholder = 0;

        while (index < buf.length()) {
            if (substringMatch(buf, index, "}")) {
                if (withinNestedPlaceholder <= 0) {
                    return index;
                }

                --withinNestedPlaceholder;
                index += "}".length();
            } else if (substringMatch(buf, index, SIMPLE_PREFIX)) {
                ++withinNestedPlaceholder;
                index += SIMPLE_PREFIX.length();
            } else {
                ++index;
            }
        }

        return -1;
    }

    private static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        for (int j = 0; j < substring.length(); ++j) {
            int i = index + j;
            if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
                return false;
            }
        }

        return true;
    }
}
