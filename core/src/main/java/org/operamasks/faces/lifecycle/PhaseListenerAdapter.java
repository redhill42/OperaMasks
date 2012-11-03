package org.operamasks.faces.lifecycle;

import static org.operamasks.resources.Resources.JSF_INSTANTIATION_ERROR;
import static org.operamasks.resources.Resources._T;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.operamasks.faces.annotation.AfterPhase;
import org.operamasks.faces.annotation.BeforePhase;
import org.operamasks.faces.application.ApplicationAssociate;

@SuppressWarnings("serial")
public class PhaseListenerAdapter implements PhaseListener{
	private Object delegate;
	private List<Method> beforePhaseMethods;
	private List<Method> afterPhaseMethods;
	private boolean isImpl;
	private Class<?> delegateClass;
	
	protected static Logger log = Logger.getLogger("org.operamasks.faces.lefecycle");

	public PhaseListenerAdapter(Class<?> delegateClass) {
        this.beforePhaseMethods = new LinkedList<Method>();
        this.afterPhaseMethods = new LinkedList<Method>();
        this.isImpl = false;
        this.delegateClass = delegateClass;
        
		if (PhaseListener.class.isAssignableFrom(delegateClass)) {
			isImpl = true;
		} else {
	        scan(delegateClass);
		}
	} 

	private void scan(Class<?> listenerClass) {
        for(Class<?> clz = listenerClass; clz.getSuperclass() != null; clz = clz.getSuperclass()) {
            Method[] methods = clz.getDeclaredMethods();
            for(Method m : methods) {
                BeforePhase p = m.getAnnotation(BeforePhase.class);
                if (p != null) {
                    m.setAccessible(true);
                    this.beforePhaseMethods.add(m);
                }
                AfterPhase a = m.getAnnotation(AfterPhase.class);
                if (a != null) {
                    m.setAccessible(true);
                    this.afterPhaseMethods.add(m);
                }
            }
        }
	}

	private void createDelegateInstance() {
        ApplicationAssociate assoc = ApplicationAssociate.getInstance();
		try {
			this.delegate = java.beans.Beans.instantiate(assoc.getClassLoader(), delegateClass.getName());
		} catch (Exception e) {
            throw new FacesException(_T(JSF_INSTANTIATION_ERROR, delegateClass.getName()), e);
		}
	}

	public void afterPhase(PhaseEvent event) {
		if (this.delegate == null) {
			createDelegateInstance();
		}
		if (isImpl) {
			PhaseListener listener = (PhaseListener)delegate;
			if (log.isLoggable(Level.FINEST)) {
			    log.finest(delegate.getClass().getName() + ".afterPhase() called.");
			}
			listener.afterPhase(event);
		} else {
			for (Method m : afterPhaseMethods) {
				try {
		            if (log.isLoggable(Level.FINEST)) {
		                log.finest(delegate.getClass().getName() + "." + m.getName() + "(...) called.");
		            }
					m.invoke(delegate, event);
				} catch (Exception e) {
					throw new FacesException( "invoke phase listener error", e);
				}
			}
		}
	}

	public void beforePhase(PhaseEvent event) {
		if (this.delegate == null) {
			createDelegateInstance();
		}
		if (isImpl) {
			PhaseListener listener = (PhaseListener)delegate;
			listener.beforePhase(event);
		} else {
			for (Method m : beforePhaseMethods) {
				try {
					m.invoke(delegate, event);
				} catch (Exception e) {
					throw new FacesException( "invoke phase listener error", e);
				}
			}
		}
	}

	public PhaseId getPhaseId() {
		if (this.delegate == null) {
			createDelegateInstance();
		}
		if (isImpl) {
			PhaseListener listener = (PhaseListener)delegate;
			return listener.getPhaseId();
		}
		return PhaseId.ANY_PHASE;
	}

}
