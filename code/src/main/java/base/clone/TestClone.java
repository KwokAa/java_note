package base.clone;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 17:26 2020/11/2
 **/
public class TestClone {
    public static void main(String[] args) throws CloneNotSupportedException {


        Person person = new Person();
        person.setAge(10);
        person.setName("小明");
        Person person_1 = person;
        System.out.println("person == person_1 :"+ (person == person_1));
        System.out.println("--------------测试=结束---------------");


        PersonClone personClone  = new PersonClone();
        personClone.setAge(2);
        personClone.setName("小刚");
        PersonClone personClone1 = (PersonClone) personClone.clone();
        System.out.println("personClone == personClone1 :"+(personClone == personClone1));
        System.out.println("personClone:" + JSONObject.toJSONString(personClone));
        System.out.println("personClone1" + JSONObject.toJSONString(personClone1));

        System.out.println("--------------测试一般clone---------------------------");

        //
        PersonClone personClone_2  = new PersonClone();
        personClone.setAge(2);
        personClone.setName("小刚");
        Address address = new Address();
        address.setId(1);
        address.setAddr("p城");
        personClone_2.setAddress(address);
        PersonClone personClone_3 = (PersonClone) personClone_2.clone();
        System.out.println("personClone_2 == personClone_3 :"+(personClone_2 == personClone_3));
        System.out.println("personClone_2:" + JSONObject.toJSONString(personClone_2));
        System.out.println("personClone_3" + JSONObject.toJSONString(personClone_3));
        System.out.println("----------测试model里面有非一般数据类型-----------");

        //修改clone后的personClone_3
//        Address address_1 = new Address();
//        address_1.setId(2);
//        address_1.setAddr("y城");

//        personClone_3.setAddress(address_1);

        personClone_3.getAddress().setId(3);

        System.out.println("personClone_2 == personClone_3 :"+(personClone_2 == personClone_3));
        System.out.println("personClone_2:" + JSONObject.toJSONString(personClone_2));
        System.out.println("personClone_3" + JSONObject.toJSONString(personClone_3));

        System.out.println("----------测试model里面有非一般数据类型 clone后修改-----------");


    }


}
