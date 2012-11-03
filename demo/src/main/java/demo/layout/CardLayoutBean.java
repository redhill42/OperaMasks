package demo.layout;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.component.layout.impl.UICardLayout;
@ManagedBean
public class CardLayoutBean {
    @Bind
    private UICardLayout demoCardLayout;
     
    @Action
    public void next(){
        int currentActiveItem = 0;
        if(demoCardLayout.getActiveItem() != null){
            currentActiveItem = demoCardLayout.getActiveItem();
        }
        demoCardLayout.setActiveItem(currentActiveItem);
        if(currentActiveItem > -1 && currentActiveItem < 2){
            demoCardLayout.activeItem(currentActiveItem+1); 
        }
    }
    
    @Action
    public void pre(){
        int currentActiveItem = 1;
        if(demoCardLayout.getActiveItem() != null){
            currentActiveItem = demoCardLayout.getActiveItem();
        }
        demoCardLayout.setActiveItem(currentActiveItem);
        if(currentActiveItem > 0 && currentActiveItem <= 2){
            demoCardLayout.activeItem(currentActiveItem-1); 
        }
    }
    
    @Action
    public void finish(){
        demoCardLayout.activeItem(2); 
    }
}
