package base.clone;

/**
 * @Description:
 * @Author: GuoChangYu
 * @Date: Created in 17:33 2020/11/2
 **/
public class PersonClone implements Cloneable {
    private int age;
    private String name;

    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
