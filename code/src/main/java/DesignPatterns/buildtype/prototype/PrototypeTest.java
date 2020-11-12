package DesignPatterns.buildtype.prototype;

import com.alibaba.fastjson.JSONArray;
import org.junit.Test;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 19:37 2020/11/5
 **/
public class PrototypeTest {
    @Test
    public void test1() throws CloneNotSupportedException {
        Student student = new Student(1, "lee", 10);
        //原型模式创建对象 studect2
        Student student2 =(Student)student.clone();
        student2.setName("huan");
        student2.setId(2);
        System.out.println(JSONArray.toJSONString(student2));
    }

    @Test
    public void testMilkTeaPrototype() throws CloneNotSupportedException {
        MilkTeaPrototype milkTeaPrototype = new MilkTeaPrototype();
        Water water = new Water();
        water.setHeat(true);
        milkTeaPrototype.setWater(water);
        int[] others = new int[]{1, 2, 3};
        milkTeaPrototype.setOthers(others);
        milkTeaPrototype.setIce(true);
        milkTeaPrototype.setType("biggest size");
        System.out.println(milkTeaPrototype);

        //深拷贝
        MilkTeaPrototype milkTeaPrototypeClone = milkTeaPrototype.clone();
        System.out.println(milkTeaPrototypeClone);


        //手写clone
        MilkTeaPrototype milkTeaPrototypeClone_manul = milkTeaPrototype.cloneMaunal();
        System.out.println(milkTeaPrototypeClone_manul);
    }
}
