package org.operamasks.faces.interceptor;

import javax.faces.render.Renderer;

import org.operamasks.faces.debug.Debug;
import org.operamasks.faces.debug.DebugInterceptor;
import org.operamasks.cglib.proxy.Enhancer;

public class ProxyFactory {

	public static Renderer createProxyRenderer(Renderer rd) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(rd.getClass());
		
		CompositeInterceptor compositeInterceptor = new CompositeInterceptor();
		if (Debug.isDebugRenderer(rd.getClass().getName())) {
			compositeInterceptor.addInterceptor(new DebugInterceptor());
		}
		
		enhancer.setCallback(compositeInterceptor);
		
		Object obj = enhancer.create();
				
		rd = (Renderer)obj;
		return rd;
	}

	public static boolean isProxyRenderer(Renderer renderer) {
		if (renderer == null)
			return true;
		return Enhancer.isEnhanced(renderer.getClass());
	}
	
    public static boolean needProxy(Renderer rd) {
    	if (rd == null)
    		return false;
    	
        return Debug.isDebugRenderer(rd.getClass().getName());
    }
}
