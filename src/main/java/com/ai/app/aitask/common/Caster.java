package com.ai.app.aitask.common;

/**
 * @author Alex Xu
 */
public class Caster {
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object source) {
        return (T) source;
    }
}
