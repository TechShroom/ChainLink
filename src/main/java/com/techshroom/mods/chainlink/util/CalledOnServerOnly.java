package com.techshroom.mods.chainlink.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cpw.mods.fml.relauncher.SideOnly;

/**
 * Like {@link SideOnly} + {@link Side#SERVER}, except not stripped on the
 * client...
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface CalledOnServerOnly {
}
