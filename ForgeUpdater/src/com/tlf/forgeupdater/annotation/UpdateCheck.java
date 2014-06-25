package com.tlf.forgeupdater.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = RUNTIME)
@Target(value = TYPE)
public @interface UpdateCheck
{
	/** The curse link to your mod. Get it from <code>curse.com/mc-mods/minecraft/[curse link] */
	String curseID();
}