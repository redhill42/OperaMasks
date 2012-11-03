/*
 * $Id: PageBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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
package demo;

import javax.faces.event.AbortProcessingException;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEventType;
import org.operamasks.faces.event.TreeEventListener;

@ManagedBean(scope = ManagedBeanScope.SESSION)
public class PageBean implements TreeEventListener {
    private String title;
    private String script ;

    public void processEvent(TreeEvent event) throws AbortProcessingException {
        script = null ;
        if( !UITreeNode.SELECT.equals( event.getEventType() ) ) {
            return ;
        }
    	if( event.getAffectedNode().isLeaf()) {
            Demo demo = parseUserData(event.getAffectedNode()) ;
            if( demo != null ) {
                this.title = demo.title ;
                String url = demo.url ;
                String srcUrl = demo.source ;
                String jspUrl = demo.jspSource ;
                StringBuffer buf = new StringBuffer() ;
                buf.append( "document.getElementById('main').src='" + url + "';\n" ) ;
                buf.append( "document.getElementById('source_frm').src='" + srcUrl + "';\n" ) ;
                buf.append( "document.getElementById('jspsource_frm').src='" + jspUrl + "';\n" ) ;
                script = buf.toString() ;
            }
    	}
        else if( event.getAffectedNode().equals( ((UITree)event.getComponent()).getRootNode() ) ) {
            this.title = "欢迎" ;
            StringBuffer buf = new StringBuffer() ;
            buf.append( "document.getElementById('main').src='common/welcome.jsf';\n" ) ;
            buf.append( "document.getElementById('source_frm').src='about:blank';\n" ) ;
            buf.append( "document.getElementById('jspsource_frm').src='about:blank';\n" ) ;
            script = buf.toString() ;
        }
    }

    public String getTitle() {
        if (this.title == null) {
            this.title = "欢迎";
        }
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScript() {
        return this.script;
    }

    public void setScript(String script) {
        this.script = script;
    }
    
    private Demo parseUserData( UITreeNode node ) {
        String data = (String)node.getUserData() ;
        String[] userDatas = data.split(",") ;
        Demo demo = new Demo() ;
        if( userDatas.length == 1 ) {
            demo.title = node.getText() ;
            demo.url = userDatas[0] ;
            demo.url = demo.url.replace( '.' , '/') ;
            demo.url = demo.url.concat(".jsp") ;
            demo.source = "common/source-notfound.html" ;
            demo.jspSource = "common/resources/jspSource.jsp?file=".concat( userDatas[0] );
            return demo ;
        }
        else if(userDatas.length == 2 ) {
            demo.title = node.getText() ;
            demo.url = userDatas[0] ;
            demo.url = demo.url.replace( '.' , '/') ;
            demo.url = demo.url.concat(".jsp") ;
            if (userDatas[1].trim().length() == 0) {
                demo.source = "common/source-notfound.html" ;
            } else {
                demo.source = "common/resources/javaSource.jsp?file=".concat( userDatas[1] );
            }
            demo.jspSource = "common/resources/jspSource.jsp?file=".concat( userDatas[0] );
            return demo ;
        }
        else if(userDatas.length == 3 ) {
            demo.title = node.getText() ;
            demo.url = userDatas[0] ;
            demo.url = demo.url.replace( '.' , '/') ;
            demo.url = demo.url.concat(".jsp") ;
            if (userDatas[1].trim().length() == 0) {
                demo.source = "common/source-notfound.html" ;
            } else {
                demo.source = "common/resources/javaSource.jsp?file=".concat( userDatas[1] );
            }
            demo.jspSource = "common/resources/jspSource.jsp?file=".concat( userDatas[2] );
            return demo ;
        }
        return null ;
    }
    
    private static class Demo {
        String title ;
        String url ;
        String source ;
        String jspSource ;
    }

}
