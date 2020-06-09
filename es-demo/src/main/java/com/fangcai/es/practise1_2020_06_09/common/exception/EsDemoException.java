package com.fangcai.es.practise1_2020_06_09.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author MouFangCai
 * @date 2020/6/7 17:43
 * @description
 */
@Data
@AllArgsConstructor
public class EsDemoException extends RuntimeException{

    private String code;

    private String msg;

}
