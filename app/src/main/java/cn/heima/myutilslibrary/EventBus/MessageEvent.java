package cn.heima.myutilslibrary.EventBus;

/**
 * Created by gaoqiong on 2018/10/12 15:54
 * Description:$description$
 */
public class MessageEvent {
    private String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
