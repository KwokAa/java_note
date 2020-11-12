package spring.filter;

import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * @Description:  包扫描实现自定义规则实现类   FilterType.CUSTOM; 使用自定义规则
 * 继承 TypeFilter 接口
 * @Author: GuoChangYu
 * @Date: Created in 0:37 2020/11/12
 **/
public class MyTypeFilter implements TypeFilter {
    /**
     *
     * @param metadataReader ：读取到的当前正在扫描的类的信息
     * @param metadataReaderFactory：可以获取其它任何类的信息
     * @return true 匹配成功  flase:匹配失败
     * @throws IOException
     */
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        //获取当前类的注解信息
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

        //获取当前正在扫描的类的类信息，比如他的类型是什么，实现什么接口等等
        ClassMetadata classMetadata = metadataReader.getClassMetadata();

        //获取当前类的资源信息，比如类的路径，存在哪个盘等等
        Resource resource = metadataReader.getResource();

        //获取类名
        String className = classMetadata.getClassName();
        System.out.println(className);

        //定义过滤规则
        if (className.contains("er")) {
            //匹配成功 包含进容器中
            //注意 结果包含  myTypeFilter 虽然没有注解，但是自定义规则扫描包内所有class,符合条件后仍然将其加入容器当中
            return true;
        }

        return false;
    }
}
