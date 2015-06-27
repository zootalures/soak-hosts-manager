package edu.bath.soak.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for adding multiple {@link BeanView} annotations to a given class;
 * 
 * 
 * @author cspocc
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BeanViews {
	/**
	 * One or more {@link BeanView} annotations
	 * 
	 * @return
	 */
	BeanView[] value();

}
