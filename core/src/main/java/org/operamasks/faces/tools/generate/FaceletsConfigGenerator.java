/*
 * $Id 
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
package org.operamasks.faces.tools.generate;

import org.operamasks.faces.tools.apt.ComponentDeclaration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;

public class FaceletsConfigGenerator extends AbstractGenerator {

    private AnnotationProcessorEnvironment env;
    private ComponentDeclaration comp;

    public FaceletsConfigGenerator(AnnotationProcessorEnvironment env, ComponentDeclaration comp) {
        this.env = env;
        this.comp = comp;
    }

    @Override
    public void generate() {
    	if(!comp.isEnable() || comp.isOnlyBuildComponentFile()){
    		return;
    	}
    	
        String projectBase = System.getProperty("projectBase");
        String filename = comp.getTagConfigFileURL(projectBase);
        // 读取文件
        Document document = XmlFileHelper.read(filename);
        if (document == null) {
            env.getMessager().printError("读取以下文件失败:" + filename);
            return;
        }

        // 如果节点存在，更新节点
        boolean exists = false;
        NodeList components = document.getElementsByTagName("tag");
        tagLabel: for (int i = 0; i < components.getLength(); i++) {
            Node component = components.item(i);
            for (int j = 0; j < component.getChildNodes().getLength(); j++) {
                Node child = component.getChildNodes().item(j);
                if ("tag-name".equals(child.getNodeName()) && child.getTextContent().equals(comp.getTagName())) {
                    exists = true;
                }
                if ("component".equals(child.getNodeName()) && exists) {
                    for (int k = 0; k < child.getChildNodes().getLength(); k++) {
                        Node c = child.getChildNodes().item(k);
                        if ("component-type".equals(c.getNodeName())) {
                            c.setTextContent(comp.getComponentType());
                            break tagLabel;
                        }
                    }
                }
            }
        }

        // 如果节点不存在，插入节点
        if (!exists) {
            Element newTag = document.createElement("tag");
            Element newName = document.createElement("tag-name");
            newName.appendChild(document.createTextNode(comp.getTagName()));

            Element newComponent = document.createElement("component");
            Element newComponentType = document.createElement("component-type");
            newComponentType.appendChild(document.createTextNode(comp.getComponentType()));
            newComponent.appendChild(newComponentType);

            newTag.appendChild(newName);
            newTag.appendChild(newComponent);

            Node facesConfig = document.getElementsByTagName("facelet-taglib").item(0);
            facesConfig.appendChild(document.createTextNode("\t"));
            facesConfig.appendChild(newTag);
        }

        // 写回文件
        if(!XmlFileHelper.write(filename, document, true)){
            env.getMessager().printError("写回以下文件失败:" + filename);
        }
    }

}
