package edu.bath.soak.web.tags;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.tags.RequestContextAwareTag;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import edu.bath.soak.util.Tuple;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.BeanViews;

/**
 * Renders a given bean using the configured bean view .
 * 
 * Each bean may be associated with zero or more bean views, using the
 * {@link BeanView} annotation.
 * 
 * Within the bean, use the BeanView annotation to specify a view-resolver
 * relative location to find views.
 * 
 * Example: <code>
 * 
 * @BeanView("beanview/foo/bar")
 * class Bar{ ... } </code>
 * 
 *                                        and in a view <code>
 * &lt;soak:renderBean bean="${bar}"/&gt;
 * </code> Will cause the
 *                                        bean "bar" to be rendered using the
 *                                        JSP page resolved by the location
 *                                        "beanview/foo/bar"
 * 
 *                                        For example if a default
 *                                        {@link InternalResourceViewResolver}
 *                                        resolver searches /WEB-INF/ this would
 *                                        look for a jsp called
 *                                        WEB-INF/beanview/foo/bar.jsp
 * 
 * @see BeanView
 * @see BeanViews
 * 
 * @author cspocc
 * 
 */
public class RenderBeanView extends RequestContextAwareTag {
	Logger log = Logger.getLogger(RenderBeanView.class);

	Object bean;
	String baseLoc = "/WEB-INF/beanviews/";
	String attributeName = "bean";
	Object previousBean = null;
	String view = null;
	String objectBase = null;

	static Map<Tuple<Class, String>,View> viewCache = new HashMap<Tuple<Class, String>, View> ();
	
	public RenderBeanView() {

	}

	public void setBean(Object var) {
		if (null == var)
			throw new Error("Bean must be set");
		log.trace("got var " + var + " which is an instance of "
				+ var.getClass());
		bean = var;
	}

	protected View resolveView(String viewName, Class beanClass)
			throws Exception {
		Assert.notNull(bean);
		
		View cached = viewCache.get(new Tuple<Class, String>(beanClass,viewName));
		if(cached!=null){
			log.debug("returning cached view "+ cached + " for class " + beanClass + " with view " + viewName);
			return cached;
		}
		BeanView annotation = null;
		log.debug("resolving view name " + viewName + " for bean of class "
				+ beanClass);

		for (Object clazzAnotation : beanClass.getAnnotations()) {
			BeanView views[] = {};
			if (clazzAnotation instanceof BeanViews) {
				views = ((BeanViews) clazzAnotation).value();
			}

			if (clazzAnotation instanceof BeanView) {
				views = new BeanView[] { (BeanView) clazzAnotation };
			}

			for (BeanView gotAnnotation : views) {

				log.trace(" got annotation on " + beanClass.getName()
						+ " with location " + gotAnnotation.value());

				if (viewName != null && !gotAnnotation.view().equals("")) {
					if (gotAnnotation.view().equals(viewName)) {
						log.trace("Matched specific view  " + viewName + " on "
								+ beanClass.getName() + " with location "
								+ gotAnnotation.value());

						annotation = gotAnnotation;
					}
				} else if (viewName == null && gotAnnotation.view().equals("")) {
					log.trace("Matched default view   on "
							+ beanClass.getName() + " with location "
							+ gotAnnotation.value());

					annotation = gotAnnotation;
				}
			}
		}

		if (annotation == null) {
			if (beanClass.equals(Object.class)) {
				return null;
			} else {
				return resolveView(viewName, beanClass.getSuperclass());
			}
		}
		// got an annotaion
		for (Entry<String, ViewResolver> vr : ((Map<String, ViewResolver>) BeanFactoryUtils
				.beansOfTypeIncludingAncestors(getRequestContext()
						.getWebApplicationContext(), ViewResolver.class))
				.entrySet()) {
			View gotView = vr.getValue().resolveViewName(annotation.value(),
					getRequestContext().getLocale());

			if (gotView != null){
				viewCache.put(new Tuple(beanClass,view), gotView);
				return gotView;
			}

		}
		return null;

	}

	protected int doStartTagInternal() throws Exception {
		log.trace("starting tag");
		try {
			String url = null;

			if (bean == null) {
				throw new Error("unable to render bean, attribute is not set");
			}

			previousBean = pageContext.getAttribute(attributeName,
					PageContext.REQUEST_SCOPE);

			pageContext.setAttribute(attributeName, bean,
					PageContext.REQUEST_SCOPE);

			pageContext.setAttribute("objectBase", objectBase,
					PageContext.REQUEST_SCOPE);

			pageContext.setAttribute("view", view, PageContext.REQUEST_SCOPE);
			View springView = resolveView(view, bean.getClass());

			if (springView == null) {
				log.warn("No view found for object of type" + bean.getClass());
				pageContext.getOut().println(
						"<div class='errorBox'> No view found for object of type "
								+ bean.getClass() + "</div>");
			} else {
				if (!(springView instanceof InternalResourceView)) {
					throw new Exception(
							"The views for bean view tags can only be used with internal resource views");
				}
				InternalResourceView resourceView = (InternalResourceView) springView;

				log.debug("rendering view " + view + " for bean  " + bean
						+ " with base" + objectBase + " using URL "
						+ resourceView.getUrl());

				pageContext.include(resourceView.getUrl());
				log.debug("rendering complete for " + url + " for bean  "
						+ bean + " with base" + objectBase + " and view "
						+ view);
			}
			return SKIP_BODY;

		} catch (Exception e) {
			throw new Error(e);
		}
	}

	protected int doStartTagInternalOld() throws Exception {
		log.trace("starting tag");
		try {
			String url = null;

			if (bean == null) {
				throw new Error("unable to render bean, attribute is not set");
			}

			previousBean = pageContext.getAttribute(attributeName,
					PageContext.REQUEST_SCOPE);

			pageContext.setAttribute(attributeName, bean,
					PageContext.REQUEST_SCOPE);

			pageContext.setAttribute("objectBase", objectBase,
					PageContext.REQUEST_SCOPE);

			pageContext.setAttribute("view", view, PageContext.REQUEST_SCOPE);
			Class searchClass = bean.getClass();
			while (searchClass != null) {
				log.trace("looking for view for " + searchClass);
				if (null != view) {
					String searchUrl = baseLoc + searchClass.getName() + "-"
							+ view + ".jsp";
					log.trace("looking in " + searchUrl);
					String gotPath = pageContext.getServletContext()
							.getRealPath(searchUrl);

					if (gotPath != null) {
						File f = new File(gotPath);
						if (f.exists()) {
							log.trace("Found path:" + searchUrl);
							url = searchUrl;
							break;
						}
					}
				}

				String searchUrl = baseLoc + searchClass.getName() + ".jsp";
				log.trace("looking in " + searchUrl);
				String gotPath = pageContext.getServletContext().getRealPath(
						searchUrl);

				if (gotPath != null) {
					File f = new File(gotPath);
					if (f.exists()) {
						log.trace("Found path:" + searchUrl);
						url = searchUrl;
						break;
					}
				}

				searchClass = searchClass.getSuperclass();
			}

			if (url == null) {
				log.warn("No renderer found for object of type"
						+ bean.getClass());
				pageContext.getOut().println(
						"<div class='errorBox'> No renderer found for object of type "
								+ bean.getClass() + "</div>");
			} else {
				log.debug("rendering view " + url + " for bean  " + bean
						+ " with base" + objectBase + " and view " + view);
				pageContext.include(url);
				log.debug("rendering complete for " + url + " for bean  "
						+ bean + " with base" + objectBase + " and view "
						+ view);
			}
			return SKIP_BODY;

		} catch (Exception e) {
			throw new Error(e);
		}
	}

	@Override
	public int doEndTag() throws JspException {
		pageContext.setAttribute("bean", previousBean);
		return super.doEndTag();
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getObjectBase() {
		return objectBase;
	}

	public void setObjectBase(String objectBase) {
		this.objectBase = objectBase;
	}

}
