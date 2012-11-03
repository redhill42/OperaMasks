package org.operamasks.faces.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>将模型对象属性批量绑定到视图组件上.</p>
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Accessible
public @interface ComponentAttributes 
{
    /**
     * <p>指定模型属性的视图作用域, 当模型对象和多个视图绑定时可以用这个属性加以区分.</p>
     *
     * @return 视图标识符
     */
    String view() default "";
    
    /**
     * <p>指定视图组件的标识, 如果未指定则使用属性名称作为标识.</p>
     *
     * @return 视图组件标识
     */
    String id() default "";
}
