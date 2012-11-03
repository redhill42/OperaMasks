package org.operamasks.faces.component.tree.base;

public interface TreeDataProvider {
    /**
     * 得到子节点的user data
     */
    Object[] getChildren(Object userData);
    
    /**
     * 得到节点的文本
     */
    String getText(Object userData);
    
    /**
     * 得到节点的图标，如果不要图标则返回null，如果要显示ext默认的图标则返回""
     */
    String getIcon(Object userData);
    
    /**
     * 得到节点的网址
     */
    String getHref(Object userData);
    
    /**
     * 得到要显示节点网址的目标iframe的name
     */
    String getHrefTarget(Object userData);
    
    /**
     * 是否要一个勾中框
     * return null则没有checkbox
     * return true表示一个勾中的checkbox
     * return false表示一个没有勾中的checkbox
     */
    Boolean isChecked(Object userData);
    
    /**
     * 节点是否展开
     */
    boolean isExpanded(Object userData);
    
    /**
     * 当选中节点的时候，是否影响其父节点和子节点，需要父节点和子节点也为true才生效
     */
    boolean isCascade(Object userData);
}
