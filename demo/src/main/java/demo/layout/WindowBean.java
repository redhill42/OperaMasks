package demo.layout;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.component.layout.impl.UIWindow;
@ManagedBean
public class WindowBean {
    @Bind
    private UIWindow demoWindow;
     
    @Action
    public void show(){
        demoWindow.show();
    }
}
