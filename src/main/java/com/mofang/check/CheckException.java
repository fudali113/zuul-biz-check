package com.mofang.check;

/**
 * CheckException
 *
 * @author doob[fudali113@gmail.com]
 * @date 17-4-25上午10:20
 */
public class CheckException extends Exception {

    private int httpCode;
    private int bizCode;

    public CheckException(String message, int httpCode, int code) {
        super(message);
        this.bizCode = code;
        this.httpCode = httpCode;
    }

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }
}
