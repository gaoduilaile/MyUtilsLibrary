package cn.krvision.toolmodule.bean;

import java.io.Serializable;

/**
 * Created by gaoqiong on 2018/11/13 14:51
 * Description:$description$
 */
public class BaseBean implements Serializable {
    private int status;//	0或者-1
    private String msg;//status为0时，该项为空；status为-1时，该项为具体错误原因
    private int page_total;//	固定为1
    private int page_index;//	固定为1
    private int page_size;//固定为10

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getPage_total() {
        return page_total;
    }

    public void setPage_total(int page_total) {
        this.page_total = page_total;
    }

    public int getPage_index() {
        return page_index;
    }

    public void setPage_index(int page_index) {
        this.page_index = page_index;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }
}
