package cn.yang.common.constant;

/**
 * @author Cool-Coding
 * @date 2018/7/24
 */
public class ExceptionMessageConstants {
    public static final String WRONG_CLIENT_TYPE                ="reqestId must start with 'M'(master) 或 'P'(puppet)";
    public static final String REQUIRED_REQUESTID               ="requestId is required";
    public static final String CONNECTION_EXIST                 ="the same connection exists";
    public static final String WRONG_CONNECT_VALUE              ="wrong connection value";
    public static final String MASTER_CONNECTION_LOST           ="connection of master is lost";
    public static final String PUPPET_CONNECTION_LOST           ="connection of puppet is lost";
    public static final String CONNECT_PUPPET_FAILED            ="connect puppet failed";
    public static final String REQUIRED_PUPPET_NAME             ="the puppet name is required";
    public static final String REQUIRED_COMMAND                 ="command is required";
    public static final String COMMAND_HANDLER_ERROR            ="CommandHandler must implements ICommandHandler and only have one interface";
    public static final String COMMAND_HANDLER_NOT_FOUND        ="CommandHandler is not found";
    public static final String EXTENSION_NOT_FOUND              ="sequence generator is not found";
    public static final String COMMANDHANDLERS_FILE_NOT_FOUND   ="the file of commandhandlers is not found";
    public static final String COMMANDHANDLERS_FILE_CONFIG_ERROR="the file of commandhandlers is configured incorrect";
}
