package cn.yang.master.client.constant;

/**
 * @author cool-coding
 * @date 2018/7/26
 */
public class ExceptionMessageConstants {
    public static final String HANDLER_NOT_SUPPORTED      = "Not supported channelHandler";
    public static final String PUPPET_NAME_EMPTY          = "The puppet's name is required";
    public static final String MASTER_FIRE_COMMAND_ERROR  = "Master can't handle this command";
    public static final String FIRE_COMMAND_HANDLE_ERROR  = "Command is not a subclass of AbstractMasterFireCommandHandler";
    public static final String CONTRL_COMMAND_RESULT_ERROR= "The result of control command is not right.it should be a puppet name";
    public static final String CONNECTION_SERVER_FAILED   = "Connecting to server is failed";
    public static final String LAUNCH_FAILED              = "Startup error";
}
