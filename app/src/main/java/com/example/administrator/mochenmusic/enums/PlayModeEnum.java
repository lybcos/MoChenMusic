package com.example.administrator.mochenmusic.enums;

/**
 * 播放模式
 * Created by wcy on 2015/12/26.
 */
public enum PlayModeEnum {
    LOOP(0),
    SHUFFLE(1),
    SINGLE(2);

    private int value;

    PlayModeEnum(int value) {
        this.value = value;
    }

    public static PlayModeEnum valueOf(int value) {
        switch (value) {
            case 1:
                return SHUFFLE;//随机播放
            case 2:
                return SINGLE;//单曲循环
            case 0:
            default:
                return LOOP;//列表循环
        }
    }

    public int value() {
        return value;
    }
}
