package com.jaiz.utils;

/**
 * 换行状态枚举
 */
public enum NextLineStatusEnum {
    /**
     * 需要换行
     */
    NEED,

    /**
     * 不需要换行
     */
    NO,

    /**
     * 疑似需要换行,需要进一步检查
     */
    SUSPECT,

    SELECT,

}
