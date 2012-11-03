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
package demo;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.layout.impl.UIWindow;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class DialogBean {
    @Bind
    private UIWindow dialog1;
    @Bind
    private UIWindow dialog2;
    @Bind
    private UIWindow dialog3;
    @Bind
    private UIWindow dialog4;

    @Action
    public void showDialog1() {
        dialog1.show();
    }

    @Action
    public void close1() {
        dialog1.close();
    }

    @Action
    public void showDialog2() {
        dialog2.show();
    }

    @Action
    public void close2() {
        dialog2.close();
    }

    @Action
    public void showDialog3() {
        dialog3.show();
    }

    @Action
    public void close3() {
        dialog3.close();
    }
    
    @Action
    public void showDialog4() {
        dialog4.show();
    }

    @Action
    public void close4() {
        dialog4.close();
    }

}
