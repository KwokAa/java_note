package spring.model;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 0:48 2020/11/9
 **/
public class PetStore {
    private int width;
    private int length;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    PetStore() {
        System.out.println("初始化petstore");
    }
}
