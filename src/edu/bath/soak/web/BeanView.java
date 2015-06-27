package edu.bath.soak.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.bath.soak.web.tags.RenderBeanView;

/**
 * Annotation for indicating where to find bean views for a given class;
 * 
 * A bean may have zero or more configured bean views, these allow a bean to be
 * rendered indirectly within a view using the {@link RenderBeanView} tag.
 * 
 * @see RenderBeanView
 * @author cspocc
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BeanView {
	/**
	 * The view resolver location (i.e. hosts/foo) for the view to render this
	 * bean with
	 * 
	 * @return
	 */
	String value();

	/**
	 * The sub-view name to render this bean with if a render requests a
	 * specific view for a bean then the annotation which matches the requested
	 * view will be used
	 * 
	 * @return
	 */
	String view() default "";
}
