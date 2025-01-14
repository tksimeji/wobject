package com.tksimeji.wobject.api;

import net.kyori.adventure.key.KeyPattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Wobject {
    @KeyPattern String value();
}
