package helloduke;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean(name="demo.helloduke.UserBean", scope=ManagedBeanScope.SESSION)
public class UserBean {
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getResult() {
        if (this.name == null || "".equals(name.trim()))
            return "Please input your name.";
        else
            return "Hello " + name;
    }

    public Object sayHello() {
        if ("duke".equalsIgnoreCase(name))
            return "/ajax/updater/sameName.xhtml";
        return null;
    }
}
