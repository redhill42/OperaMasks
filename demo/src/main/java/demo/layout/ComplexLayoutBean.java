package demo.layout;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.component.layout.impl.UICardLayout;
import org.operamasks.faces.component.layout.impl.UIWindow;
import org.operamasks.faces.component.widget.UIButton;
@ManagedBean
public class ComplexLayoutBean {
    @Bind
    private UIWindow demoWindow;
    
    @Bind
    private UICardLayout wizard;
    
    @Bind
    private UIButton next;
     
    @Bind
    private UIButton pre;

    
    @Action
    public void show(){
        wizard.activeItem(0);
        changeTitle(0);
        changeButtonState(0);
        demoWindow.show();
    }
    
    @Action
    public void next(){
        int currentActiveItem = 0;
        if(wizard.getActiveItem() != null){
            currentActiveItem = wizard.getActiveItem();
        }
        if(currentActiveItem > -1 && currentActiveItem < 2){
            currentActiveItem++; 
        }
        wizard.activeItem(currentActiveItem);
        changeTitle(currentActiveItem);
        changeButtonState(currentActiveItem);
    }
    
    private void changeButtonState(int currentActiveItem) {
        if(currentActiveItem == 0){
            pre.setDisabled(true);
            next.setDisabled(false);
        }
        if(currentActiveItem == 1){
            pre.setDisabled(false);
            next.setDisabled(false);
        }
        if(currentActiveItem == 2){
            pre.setDisabled(false);
            next.setDisabled(true);
        }
    }

    @Action
    public void pre(){
        int currentActiveItem = 1;
        if(wizard.getActiveItem() != null){
            currentActiveItem = wizard.getActiveItem();
        }
        if(currentActiveItem > 0 && currentActiveItem <= 2){
            currentActiveItem--; 
        }
        wizard.activeItem(currentActiveItem);
        changeTitle(currentActiveItem);
        changeButtonState(currentActiveItem);
    }
    
    @Action
    public void finish(){
        demoWindow.close();
    }
    
    private void changeTitle(int item){
        if(item == 0){
            demoWindow.setTitle("填写基本信息");
        }
        if(item == 1){
            demoWindow.setTitle("填写详细信息");
        }
        if(item == 2){
            demoWindow.setTitle("注册完成");
        }
    }
}
