package cn.yang.puppet.client.commandhandler;

import cn.yang.common.command.Commands;
import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.command.handler.ICommandHandler;
import cn.yang.common.constant.Constants;
import cn.yang.common.exception.ResponseHandleException;
import cn.yang.common.sequence.SequenceGenerator;
import cn.yang.common.util.SequenceGeneratorUtil;
import cn.yang.common.util.MacUtils;
import cn.yang.puppet.client.ui.PuppetDesktop;
import cn.yang.common.exception.CommandHandlerException;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.IOException;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public abstract class AbstractPuppetCommandHandler implements ICommandHandler<Response> {

    /** logger */
    protected static Logger LOGGER;


    /**
     * 序号生成器
     */
    private static  SequenceGenerator generator;

    /**
     * 桌面控制器
     */
    protected static PuppetDesktop puppetDesktop;


    /**
     * 是否正处于被控制中
     */
    private static volatile boolean isUnderControlled=false;



    public AbstractPuppetCommandHandler() throws CommandHandlerException{
        LOGGER = LoggerFactory.getLogger(this.getClass());
        try {
            puppetDesktop = new PuppetDesktop();
        }catch (AWTException e){
            throw new CommandHandlerException(e.getMessage(),e);
        }

        if(generator==null) {
            try {
                generator = SequenceGeneratorUtil.getSequenceGenerator();
            }catch (IOException e){
                LOGGER.error(e.getMessage(),e);
                throw new CommandHandlerException(e.getMessage(),e);
            }
        }
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Response response) throws Exception {
        final Enum<Commands> command = response.getCommand();
        if (command == null){
            LOGGER.error(cn.yang.common.constant.ExceptionConstants.REQUIRED_COMMAND);
            ctx.channel().close();
            throw new ResponseHandleException(cn.yang.common.constant.ExceptionConstants.REQUIRED_COMMAND);
        }


        handle0(ctx,response);
    }

    protected abstract void handle0(ChannelHandlerContext ctx, Response response) throws Exception;


    public Request buildRequest(Enum<Commands> command, Object value){
        String mac= MacUtils.getMAC();
        if (StringUtils.isEmpty(mac)){
            return null;
        }

        Request request = new Request();
        request.setRequestId(Constants.PUPPET + mac + generator.next());
        request.setCommand(command);
        request.setPuppetName("puppet");
        request.setValue(value);
        return  request;
    }

    void error(Request request,String message){
        LOGGER.error("{}:{}",request,message);
    }

    void debug(Request request,String message){
        LOGGER.debug("{}:{}",request,message);
    }

    void debug(String format,String message){
        LOGGER.debug(format,message);
    }

    void info(Request request,String message){
        LOGGER.info("{}:{}",request,message);
    }

    void warn(Request request,String message){
        LOGGER.warn("{}:{}",request,message);
    }

    protected void startUnderControlled(){
        AbstractPuppetCommandHandler.isUnderControlled=true;
    }

    protected void stopUnderControlled(){
        AbstractPuppetCommandHandler.isUnderControlled=false;
    }

    public static PuppetDesktop getPuppetDesktop() {
        return puppetDesktop;
    }

    protected boolean isUnderControlled(){
        return AbstractPuppetCommandHandler.isUnderControlled;
    }
}
