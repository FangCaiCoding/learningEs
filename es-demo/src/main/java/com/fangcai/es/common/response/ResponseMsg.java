package com.fangcai.es.common.response;

import lombok.Data;

/**
 * @author MouFangCai
 * @date 2020/6/7 17:31
 * @description 正常返回的 obj
 */
@Data
public class ResponseMsg {
    private String code;
    private Object data;
    private String msg;

    public ResponseMsg (String code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public ResponseMsg (Object data, String msg) {
        this.code = "200";
        this.data = data;
        this.msg = msg;
    }

    public ResponseMsg (Object data) {
        this.code = "200";
        this.data = data;
    }


}
