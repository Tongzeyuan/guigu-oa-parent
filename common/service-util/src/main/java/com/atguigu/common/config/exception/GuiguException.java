package com.atguigu.common.config.exception;

import lombok.Data;

@Data
public class GuiguException extends RuntimeException{

    private Integer code;

    public GuiguException(Integer code,String message){
        super(message);
        this.code = code;
    }


}
