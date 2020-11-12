package spring.condition;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Description: 自定义逻辑返回需要导入的组件
 * @Author: GuoChangYu
 * @Date: Created in 23:04 2020/11/12
 **/
public class MyImportSelector implements ImportSelector {
    /**
     *
     * @param annotationMetadata 当前标注@Import注解的类的所有注解信息
     * @return 返回值，就是导入到容器中的组件全类名
     */
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {


        //annotationMetadata 可以获取注解信息 补充业务逻辑,比如补充自定义注解

        return new String[]{"spring.model.Blue","spring.model.Yellow"};



        //不应该返回null,可以返回空数组
//        return new String[0];
//        return null;
    }
}
