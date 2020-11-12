package spring.service;

import com.alibaba.fastjson.JSONArray;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 0:46 2020/11/9
 **/
public class PetStoreService {


    public <T> void printModel(T t) {
        try {

            if (t instanceof Object) {
                System.out.println(JSONArray.toJSONString(t));
            }
        } catch (Exception e) {
            System.out.println("错误：" + e.getMessage());
        }
    }

}
