package cn.yang.master.client.commandhandler;

import cn.yang.common.command.Commands;
import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.command.handler.ICommandHandler;
import cn.yang.common.constant.Constants;
import cn.yang.common.constant.ExceptionConstants;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.common.exception.ResponseHandleException;
import cn.yang.common.sequence.SequenceGenerator;
import cn.yang.common.util.SequenceGeneratorUtil;
import cn.yang.common.util.MacUtils;
import cn.yang.master.client.exception.ConnectionException;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public abstract class AbstractMasterCommandHandler implements ICommandHandler<Response> {

    /** logger */
    protected static Logger LOGGER;

    /**
     * 序号生成器
     */
    private static  SequenceGenerator generator;


    private static ChannelHandlerContext ctx;


    public AbstractMasterCommandHandler() throws CommandHandlerException {
        LOGGER = LoggerFactory.getLogger(this.getClass());

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
        if (command==null){
            LOGGER.error(ExceptionConstants.REQUIRED_COMMAND);
            ctx.channel().close();
            throw new ResponseHandleException(ExceptionConstants.REQUIRED_COMMAND);
        }

        handle0(ctx,response);
    }

    protected abstract void handle0(ChannelHandlerContext ctx, Response response) throws Exception;


    public Request buildRequest(Enum<Commands> command, String puppetName, Object obj){
        String mac= MacUtils.getMAC();
        if (StringUtils.isEmpty(mac)){
            return null;
        }

        Request request = new Request();
        request.setRequestId(Constants.MASTER + mac + generator.next());
        request.setCommand(command);
        request.setPuppetName(puppetName);
        request.setValue(obj);
        return  request;
    }

    protected void setChannelHandlerConext(ChannelHandlerContext ctx){
        AbstractMasterCommandHandler.ctx=ctx;
    }

    protected void sendRequest(Request request) throws ConnectionException{
        if (ctx!=null && ctx.channel()!=null && ctx.channel().isOpen()){
            ctx.writeAndFlush(request);
        }else{
            LOGGER.error(cn.yang.master.client.constant.ExceptionConstants.CONNECTION_SERVER_FAILED);
            throw new ConnectionException(cn.yang.master.client.constant.ExceptionConstants.CONNECTION_SERVER_FAILED);
        }
    }
}
