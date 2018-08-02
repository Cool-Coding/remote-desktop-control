package cn.yang.common.command;

/**
 * @author cool-coding
 * 2018/7/27
 * 命令
 */
public enum Commands{
    /**
     * 控制端或傀儡端连接服务器时的命令
     */
    CONNECT,

    /**
     * 控制命令
     * 1.主人向服务器发送控制请求
     * 2.服务器将控制命令发给傀儡
     * 3.傀儡收到控制命令，将向服务器发送截屏
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
