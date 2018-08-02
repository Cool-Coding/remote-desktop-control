package cn.yang.server.commandhandler;

import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.command.Commands;
import cn.yang.common.command.handler.ICommandHandler;
import cn.yang.common.constant.Constants;
import cn.yang.common.exception.ServerException;
import cn.yang.server.netty.ChannelPair;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ConcurrentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static cn.yang.common.constant.ExceptionMessageConstants.REQUIRED_PUPPET_NAME;
import static cn.yang.common.constant.ExceptionMessageConstants.REQUIRED_REQUESTID;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public abstract class AbstractServerCommandHandler implements ICommandHandler<Request>{

    /** logger */
    private static Logger LOGGER;

    /**
     * 已经建立连接的控制端(指当前正控制傀儡中)与傀儡端(与服务器连接中)
     * key:傀儡名称(傀儡名称唯一)
     * value:控制端与傀儡的channel
     */
    protected static final Map<String,ChannelPair> CONNECTED_CHANNELPAIRS=new ConcurrentHashMap<>();


    /**
     * 待连接的傀儡集合
     */
    protected static final Set<String> PRECONTROL_PUPPETS =new ConcurrentSet<>();


    public AbstractServerCommandHandler(){
        LOGGER=LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Request request) throws Exception {
        if(StringUtils.isEmpty(request.getId())){
            error(request,REQUIRED_REQUESTID);
            sendError(request,ctx,REQUIRED_REQUESTID);
            return;
        }

        //除了控制端请求连接不要求PuppetName有值外，其它命令都PuppetName必须有值
        boolean masterRequestConnect=Constants.MASTER == request.getId().charAt(0) && request.getCommand()== Commands.CONNECT;
        if(!masterRequestConnect){
            if(StringUtils.isEmpty(request.getPuppetName())) {
                error(request,REQUIRED_PUPPET_NAME);
                sendError(request, ctx, REQUIRED_PUPPET_NAME);
                return;
            }
        }

        handle0(ctx,request);
    }

    protected void sendError(Request request, ChannelHandlerContext ctx, String e){
        Response response=new Response();
        response.setPuppetName(request.getPuppetName());
        response.setId(request.getId());
        response.setError(new ServerException(e));
        ctx.writeAndFlush(response);
    }


    protected Response buildResponse(Request request,Enum<Commands> command){
        return buildResponse(request,command,null);
    }

    protected Response buildResponse(Request request,Enum<Commands> command, Object result){
        Response response=new Response();
        response.setId(request.getId());
        response.setPuppetName(request.getPuppetName());
        response.setCommand(command);
        response.setValue(result);
        return response;
    }

    void error(Request request,String... message){
        LOGGER.error("{}:{}",request, Arrays.toString(message));
    }

    void debug(Request request,String... message){
        LOGGER.debug("{}:{}",request, Arrays.toString(message));
    }

    void info(Request request,String... message){
        LOGGER.info("{}:{}",request, Arrays.toString(message));
    }

    void warn(Request request,String... message){
        LOGGER.warn("{}:{}",request, Arrays.toString(message));
    }

    public abstract  void handle0(ChannelHandlerContext ctx, Request request) throws Exception;
}
