package cn.yang.master.client.constant;

/**
 * @author cool-coding
 * @date 2018/7/26
 */
public class ExceptionConstants {
    public static final String HANDLER_NOT_SUPPORTED="not supported channelHandler";
    public static final String PUPPET_NAME_EMPTY="the puppet's name is required";
    public static final String MASTER_FIRE_COMMAND_ERROR="master can't handle this command";
    public static final String FIRE_COMMAND_HANDLE_ERROR="command is not a subclass of AbstractMasterFireCommandHandler";
    public static final String CONTRL_COMMAND_RESULT_ERROR="the result of control command is not right.it should be a puppet name";
    public static final String CONNECTION_SERVER_FAILED="connecting to server is failed";
}
