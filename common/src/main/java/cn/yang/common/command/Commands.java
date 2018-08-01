package cn.yang.common.command;

import java.io.Serializable;

/**
 * @author cool-coding
 * @date 2018/7/27
 */
public enum Commands{


    /**
     * 控制端或傀儡端连接服务器时的命令
     */
    CONNECT,

    /**
     * 连接命令
     * 1.主人向服务器发送控制请求
     * 2.服务器将连接命令发给傀儡
     * 3.傀儡收到连接命令，将主动连接服务器
     */
    CONTROL,

    /**
     * 傀儡发送心跳给服务器
     */
    HEARTBEAT,

    /**
     * 傀儡发送屏幕截图命令
     */
    SCREEN,

    /**
     * 控制端发送键盘事件
     */
    KEYBOARD,

    /**
     * 控制端发送鼠标事件
     */
    MOUSE,

    /**
     * 断开控制傀儡
     */
    TERMINATE,

    /**
     * 清晰度
     */
    QUALITY
}
