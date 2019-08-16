package entity;

import java.io.Serializable;

public class Result implements Serializable{

    private boolean success;//添加结果是否成功

    private String message;//返回添加数据成功与否的对应信息

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
