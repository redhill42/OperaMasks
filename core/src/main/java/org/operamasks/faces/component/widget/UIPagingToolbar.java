/*
 * $Id: UIPagingToolbar.java,v 1.4 2007/12/11 04:20:12 jacky Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

package org.operamasks.faces.component.widget;

import java.util.ArrayList;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.component.widget.toolbar.AddToolBarItem;
import org.operamasks.faces.component.widget.toolbar.ToolBarStateChange;
import org.operamasks.faces.util.FacesUtils;

public class UIPagingToolbar extends UIPager
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.PagingToolbar";
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.Toolbar";
    
    private List<ToolBarStateChange> changes;

    public UIPagingToolbar() {
        setRendererType(RENDERER_TYPE);
    }
    
    public UIPagingToolbar(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }

    /**
     * 控件在客户端的js变量名
     */
    private String jsvar;

    /**
     * 显示的提示, default: 'Displaying {0} - {1} of {2}';
     */
    private String displayMsg;

    /**
     * 没有记录的时候显示的提示, : default: 'No data to display';
     */
    private String emptyMsg;

    /**
     * 对分页工具栏上的文字的细微调整
     */
    private String beforePageText;

    /**
     * 对分页工具栏上的文字的细微调整
     */
    private String afterPageText;

    /**
     * 对分页工具栏上的文字的细微调整
     */
    private String firstText;

    /**
     * 对分页工具栏上的文字的细微调整
     */
    private String prevText;

    /**
     * 对分页工具栏上的文字的细微调整
     */
    private String nextText;

    /**
     * 对分页工具栏上的文字的细微调整
     */
    private String lastText;

    /**
     * 对分页工具栏上的文字的细微调整
     */
    private String refreshText;

    public String getJsvar() {
        if (this.jsvar != null) {
            return this.jsvar;
        }
        ValueExpression ve = getValueExpression("jsvar");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setJsvar(String jsvar) {
        this.jsvar = jsvar;
    }

    public String getDisplayMsg() {
        if (this.displayMsg != null) {
            return this.displayMsg;
        }
        ValueExpression ve = getValueExpression("displayMsg");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDisplayMsg(String displayMsg) {
        this.displayMsg = displayMsg;
    }

    public String getEmptyMsg() {
        if (this.emptyMsg != null) {
            return this.emptyMsg;
        }
        ValueExpression ve = getValueExpression("emptyMsg");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setEmptyMsg(String emptyMsg) {
        this.emptyMsg = emptyMsg;
    }

    public String getBeforePageText() {
        if (this.beforePageText != null) {
            return this.beforePageText;
        }
        ValueExpression ve = getValueExpression("beforePageText");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setBeforePageText(String beforePageText) {
        this.beforePageText = beforePageText;
    }

    public String getAfterPageText() {
        if (this.afterPageText != null) {
            return this.afterPageText;
        }
        ValueExpression ve = getValueExpression("afterPageText");
        if (ve != null) {
            return (String) ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setAfterPageText(String afterPageText) {
        this.afterPageText = afterPageText;
    }

    public String getFirstText() {
        if (this.firstText != null) {
            return this.firstText;
        }
        ValueExpression ve = getValueExpression("firstText");
        if (ve != null) {
            return (String) ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setFirstText(String firstText) {
        this.firstText = firstText;
    }

    public String getPrevText() {
        if (this.prevText != null) {
            return this.prevText;
        }
        ValueExpression ve = getValueExpression("prevText");
        if (ve != null) {
            return (String) ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setPrevText(String prevText) {
        this.prevText = prevText;
    }

    public String getNextText() {
        if (this.nextText != null) {
            return this.nextText;
        }
        ValueExpression ve = getValueExpression("nextText");
        if (ve != null) {
            return (String) ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setNextText(String nextText) {
        this.nextText = nextText;
    }

    public String getLastText() {
        if (this.lastText != null) {
            return this.lastText;
        }
        ValueExpression ve = getValueExpression("lastText");
        if (ve != null) {
            return (String) ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLastText(String lastText) {
        this.lastText = lastText;
    }

    public String getRefreshText() {
        if (this.refreshText != null) {
            return this.refreshText;
        }
        ValueExpression ve = getValueExpression("refreshText");
        if (ve != null) {
            return (String) ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setRefreshText(String refreshText) {
        this.refreshText = refreshText;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar,
            displayMsg ,
            emptyMsg ,
            beforePageText ,
            afterPageText ,
            firstText ,
            prevText ,
            nextText ,
            lastText ,
            refreshText,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar = (String)values[i++];
        displayMsg = (String)values[i++];
        emptyMsg = (String)values[i++];
        beforePageText = (String)values[i++];
        afterPageText = (String)values[i++];
        firstText = (String)values[i++];
        prevText = (String)values[i++];
        nextText = (String)values[i++];
        lastText = (String)values[i++];
        refreshText = (String)values[i++];
    }
    
    public void addItem(UIMenu menu) {
    	addItem((UIComponent)menu);
    }
    
    public void addItem(UISeparator separator) {
    	addItem((UIComponent)separator);
    }
    
    public void addItem(UICombo combo) {
    	addItem((UIComponent)combo);
    }
    
    public void addItem(UICommand command) {
    	addItem((UIComponent)command);
    }
    
    private void addItem(UIComponent component) {
    	if (FacesUtils.currentPhase() == PhaseId.INVOKE_APPLICATION) {
    		getChanges().add(new AddToolBarItem(this, component));
    		getChildren().add(component);
    	}
    }
    
/*    public void removeItem(UIComponent item) {
    	for (int i = 0; i < getChildren().size(); i++) {
    		UIComponent component = getChildren().get(i);
    		
    		if (component.getId().equals(item.getId())) {
    			getChildren().remove(i);
    			
    			if (FacesUtils.currentPhase() == PhaseId.INVOKE_APPLICATION)
    	    		changes.add(new RemoveToolBarItem(this, component));
    		}
    	}
    }*/

	public List<ToolBarStateChange> getChanges() {
		if (changes == null)
			changes = new ArrayList<ToolBarStateChange>();
		
		return changes;
	}

	public void setChanges(List<ToolBarStateChange> changes) {
		this.changes = changes;
	}
}
