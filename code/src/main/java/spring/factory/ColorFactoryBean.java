package spring.factory;

import org.springframework.beans.factory.FactoryBean;
import spring.model.Color;

/**
 * @Description: 创建一个Spring定义的FactoryBean
 * @Author: GuoChangYu
 * @Date: Created in 23:31 2020/11/12
 **/
public class ColorFactoryBean implements FactoryBean<Color> {

    /**
     * @return 返回一个Color对象，这个对象会添加到容器中
     * @throws Exception
     */
    @Override
    public Color getObject() throws Exception {
        System.out.println("ColorFactoryBean ---  getObject()---");
        return new Color();
//        return null;
    }

    @Override
    public Class<?> getObjectType() {
//        return null;
        return Color.class;
    }

    /**
     * 控制是否单例
     * true:单实例，在容器中只保存一份
     * false:多实例，每次获取都会创建一个新的对象，通过 上面的getObject（）调用
     * @return
     */
    @Override
    public boolean isSingleton() {
        return true;
//        return false;
    }
}
