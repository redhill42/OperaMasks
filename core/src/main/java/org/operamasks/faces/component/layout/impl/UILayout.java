package org.operamasks.faces.component.layout.impl;

import static org.operamasks.resources.Resources.UI_LAYOUT_CHILD_INCORRECT_TYPE;
import static org.operamasks.resources.Resources._T;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;

import org.operamasks.faces.event.EventTypes;
import org.operamasks.faces.event.ModelEvent;
import org.operamasks.faces.event.ModelEventListener;
import org.operamasks.faces.event.ThreadLocalEventBroadcaster;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.StructureValidateUtils;


public abstract class UILayout extends UIPanel implements ModelEventListener{
    private Logger logger = Logger.getLogger("org.operamasks.faces.view");
    public UILayout() {
        ThreadLocalEventBroadcaster.getInstance().addEventListenerOnce(EventTypes.BEFORE_RENDER_VIEW, this);
    }

    public void processModelEvent(ModelEvent event) {
        if (EventTypes.BEFORE_RENDER_VIEW.equals(event.getEventType()) && 
                logger.isLoggable(Level.WARNING)) {
            UIComponent unexpected = StructureValidateUtils.pickUnexpectedChildByClass(this, UIPanel.class);
            if (unexpected != null) 
                logger.warning(_T(UI_LAYOUT_CHILD_INCORRECT_TYPE, FacesUtils.getComponentDesc(unexpected)));
        }
    }
}
