/*
 * $Id:
 *
 * Copyright (c) 2006 Operamasks Community.
 * Copyright (c) 2000-2006 Apusic Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package demo.binding;

import java.util.Map;

import org.operamasks.faces.annotation.Accessible;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.LocalString;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.SelectItem;
import org.operamasks.faces.annotation.SelectItems;
import org.operamasks.faces.annotation.BeforeRender;

@ManagedBean(scope = ManagedBeanScope.SESSION)
@SuppressWarnings("unused")
public class SelectItemsBean
{
    private @LocalString Map<String,String> messages;

    @Bind
    @SelectItems
    private String color;

    @Accessible
    private String[] cities = {
            "#{this.messages.GZ}",
            "#{this.messages.SZ}",
            "#{this.messages.SH}"
    };

    @Bind
    @SelectItems(source="#{this.cities}")
    private String city;

    @Bind
    private String colorImg;
    @Bind
    private String colorText;
    @Bind(id="colorText", attribute="style")
    private String colorTextStyle;

    @Bind
    private String cityText;

    public SelectItemsBean() {
        color = "Red";
        city = "广州";
    }

    @Action
    public void change() {
        cities = new String[]{
            "#{this.messages.CQ}",
            "#{this.messages.GZ}",
            "#{this.messages.SZ}",
            "#{this.messages.SH}"
        };
        city = "重庆";
    }

    @BeforeRender
    private void update(boolean isPostback) {
        colorImg = "../form/images/" + color + ".gif";
        colorText = color;
        colorTextStyle = "color: " + color;
        cityText = city;
    }
}
