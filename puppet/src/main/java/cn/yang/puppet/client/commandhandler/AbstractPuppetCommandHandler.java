package cn.yang.puppet.client.commandhandler;

import cn.yang.common.command.Commands;
import cn.yang.common.constant.ExceptionMessageConstants;
import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.command.handler.ICommandHandler;
import cn.yang.common.constant.Constants;
import cn.yang.common.exception.ResponseHandleException;
import cn.yang.common.sequence.SequenceGenerator;
import cn.yang.common.util.BeanUtil;
import cn.yang.common.util.PropertiesUtil;
import cn.yang.common.util.ExtensionLoader;
import cn.yang.common.util.MacUtils;
import cn.yang.puppet.client.constant.ConfigConstants;
import cn.yang.puppet.client.ui.IReplay;
import cn.yang.common.exception.CommandHandlerException;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;

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
    private static final SequenceGenerator generator=BeanUtil.getBean(SequenceGenerator.class);

    /**
     * 桌面控制器
     */
    protected static final IReplay REPLAY = BeanUtil.getBean(IReplay.class);
    /**
     * 标识:是否正处于被控制中
     */
    private static volatile boolean isUnderControlled=false;


    public AbstractPuppetCommandHandler() throws CommandHandlerException{
        LOGGER = LoggerFactory.getLogger(this.getClass());
        //sequence generator is  maintained in spring xml instead of txt
        /*if(generator==null) {
            try {
                generator = ExtensionLoader.loadSingleObject(SequenceGenerator.class);
            }catch (IOException e){
                LOGGER.error(e.getMessage(),e);
                throw new CommandHandlerException(e.getMessage(),e);
            }
        }*/
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Response response) throws Exception {
        final Enum<Commands> command = response.getCommand();
        if (command == null){
            LOGGER.error(ExceptionMessageConstants.REQUIRED_COMMAND);
            ctx.channel().close();
            throw new ResponseHandleException(ExceptionMessageConstants.REQUIRED_COMMAND);
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
        request.setId(Constants.PUPPET + mac + generator.next());
        request.setCommand(command);
        request.setPuppetName("puppet");
        request.setValue(value);
        return  request;
    }

    void error(Response response,String message){
        LOGGER.error("{}:{}",response,message);
    }

    void debug(Response response,String... message){
        LOGGER.debug("{}:{}",response,Arrays.toString(message));
    }

    void info(Response response,String message){
        LOGGER.info("{}:{}",response,message);
    }

    void warn(Response response,String message){
        LOGGER.warn("{}:{}",response,message);
    }

    protected void startUnderControlled(){
        AbstractPuppetCommandHandler.isUnderControlled=true;
    }

    protected void stopUnderControlled(){
        AbstractPuppetCommandHandler.isUnderControlled=false;
    }

    public static IReplay getReplay() {
        return REPLAY;
    }

    protected boolean isUnderControlled(){
        return AbstractPuppetCommandHandler.isUnderControlled;
    }
}
