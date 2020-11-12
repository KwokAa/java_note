package spring.model;

import org.springframework.beans.factory.annotation.Value;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 23:26 2020/11/11
 **/
public class Person {

    /**
     * 使用@Value赋值
     *   1.基本数值
     *   2.可以写SpEL; #{}
     *   3.可以写${};取出配置文件[properties]中的值（在运行环境变量里面的值）
     *
     *
     */
    @Value("zhangsan")
    private String name;
    @Value("#{20-2}")
    private int age;
    @Value("${person.nickName}")
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Person() {
    }

    public Person(String name, int age, String nickName) {
        this.name = name;
        this.age = age;
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
