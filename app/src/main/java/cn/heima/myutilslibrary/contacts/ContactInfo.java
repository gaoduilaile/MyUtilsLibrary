package cn.heima.myutilslibrary.contacts;

import java.io.Serializable;

/**
 * Created by gaoqiong on 2018/5/13
 */

public class ContactInfo implements Serializable {

    private String name;
    private String telephone;
    private boolean isAdded;

    public ContactInfo(String name, String telephone, boolean isAdded) {
        this.name = name;
        this.telephone = telephone;
        this.isAdded = isAdded;
    }

    public ContactInfo(String name, String telephone) {
        this.name = name;
        this.telephone = telephone;
    }

    public ContactInfo() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "name='" + name + '\'' +
                ", telephone='" + telephone + '\'' +
                ", isAdded=" + isAdded +
                '}';
    }
}
