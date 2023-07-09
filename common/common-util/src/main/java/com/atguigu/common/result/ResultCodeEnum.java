package com.atguigu.common.result;

public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(201,"失败");

    private Integer code;
    private String message;

    private ResultCodeEnum(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
