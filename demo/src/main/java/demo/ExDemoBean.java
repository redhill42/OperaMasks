package demo;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.component.widget.dialog.UIDialog;

@ManagedBean(name = "ExDemoBean", scope = ManagedBeanScope.SESSION)
public class ExDemoBean {
    private UIDialog exDemoDialog;
    public AjaxUpdater contentUpdater;

    public UIDialog getExDemoDialog() {
        return exDemoDialog;
    }

    public void setExDemoDialog(UIDialog exDemoDialog) {
        this.exDemoDialog = exDemoDialog;
    }

    public void openExDemoDialog(){
        exDemoDialog.show();
    }

    public AjaxUpdater getContentUpdater() {
        return contentUpdater;
    }

    public void setContentUpdater(AjaxUpdater contentUpdater) {
        this.contentUpdater = contentUpdater;
    }

    public void loadExDemo2(){
        contentUpdater.load("/ExDemo2.jsp");
    }
}