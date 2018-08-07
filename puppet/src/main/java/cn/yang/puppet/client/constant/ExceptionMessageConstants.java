package cn.yang.puppet.client.constant;

/**
 * @author Cool-Coding
 *         2018/7/26
 */
public class ExceptionMessageConstants {
    public static final String PUPPET_HANDLER_ERROR         = "the puppet netty client handler must be PuppetNettyClientHandler";
    public static final String KEYBOARD_EVENT_ERROR         = "The type of keyevent from server is wrong";
    public static final String KEYBOARD_EVENT_NULL          = "The value  of keyevent from server is null";
    public static final String MOUSE_EVENT_ERROR            = "The type of mouseevent from server is wrong";
    public static final String MOUSE_EVENT_NULL             = "The value of mouseevent from server is null";
    public static final String QUALITY_EVENT_VALUE_ERROR    = "The value  of quality from server is not the type Integer";
    public static final String QUALITY_EVENT_VALUE_NULL     = "The value  of quality from server is null";
    public static final String DISCONNECT_TO_SERVER         = "Disconnect to server {}:{},will be reconnect to server in {} milliseconds";

}
