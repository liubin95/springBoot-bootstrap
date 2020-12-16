package com.caomu.bootstrap.domain;

import java.io.Serializable;

/**
 * 统一返回值
 *
 * @author liubin
 */
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 是否成功。
     */
    private Boolean succeeded;


    /**
     * 错误信息。
     */
    private String msg;


    /**
     * 返回对象(具体类型参照具体的HTTP请求)。
     */
    private Object obj;

    public Result() {

        this.succeeded = true;
        this.setMsg("操作成功");
    }

    public Boolean getSucceeded() {

        return succeeded;
    }

    public void setSucceeded(Boolean succeeded) {

        this.succeeded = succeeded;
    }

    public String getMsg() {

        return msg;
    }

    public void setMsg(String msg) {

        this.msg = msg;
    }

    public Object getObj() {

        return obj;
    }

    public void setObj(Object obj) {

        this.obj = obj;
    }

}
