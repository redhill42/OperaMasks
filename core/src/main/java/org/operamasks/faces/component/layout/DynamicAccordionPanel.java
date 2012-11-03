package org.operamasks.faces.component.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

/**
 * @deprecated 此类已经废弃
 */
@Deprecated
public class DynamicAccordionPanel extends AccordionPanel
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.layout.DynamicPanel";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.layout.Panel";
    public static final String RENDERER_TYPE = "org.operamasks.faces.layout.DynamicPanel";

    public DynamicAccordionPanel() {
        setRendererType(RENDERER_TYPE);
        clientIds = new ArrayList<String>();
        jsVars = new ArrayList<String>();
        childrenClientIds = new HashMap<String, List<String>>();
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
    private Map<String,List<String>> childrenClientIds;
    private List<String> clientIds;
    private List<String> jsVars;

    public List<String> getClientIds() {
        return clientIds;
    }
    
    public List<String> getJsVars() {
        return jsVars;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            clientIds         ,
            jsVars            ,
            childrenClientIds
        };
    }

    @SuppressWarnings("unchecked")
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        int i = 0;
        super.restoreState(context, values[i++]);
        clientIds   = (List<String>) values[i++];
        jsVars      = (List<String>) values[i++];
        childrenClientIds = (Map<String, List<String>>) values[i++];
    }

    public Map<String, List<String>> getChildrenClientIds() {
        return this.childrenClientIds;
    }

    public void setChildrenClientIds(Map<String, List<String>> childrenClientIds) {
        this.childrenClientIds = childrenClientIds;
    }

}
