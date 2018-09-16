package cn.yang.master.client.netty;
import cn.yang.common.util.CommandHandlerLoader;
import cn.yang.common.dto.Response;
import cn.yang.common.command.*;
import cn.yang.common.command.handler.ICommandHandler;
import cn.yang.common.exception.CommandHandlerLoaderException;
import cn.yang.master.client.commandhandler.AbstractMasterFireCommandHandler;
import cn.yang.master.client.constant.ExceptionMessageConstants;
import cn.yang.master.client.exception.FireCommandHandlerException;
import cn.yang.master.client.exception.MasterChannelHandlerException;
import cn.yang.master.client.exception.MasterClientException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.util.concurrent.*;

/**
 * @author Cool-Coding
 *         2018/7/25
 */
@ChannelHandler.Sharable
public class MasterNettyClientHandler extends SimpleChannelInboundHandler<Response> {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterNettyClientHandler.class);

    @SuppressWarnings("unchecked")
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        if (response.getError()!=null){
            SwingUtilities.invokeLater(()->{
                JOptionPane.showMessageDialog(null,response.getError().getMessage());
            });
            LOGGER.error(response.getError().getMessage());
            return;
        }

        final ICommandHandler commandHandler = CommandHandlerLoader.getCommandHandler(response.getCommand());
        LOGGER.debug(response.toString());
        commandHandler.handle(ctx,response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(),cause);
        ctx.close();
    }

    @SuppressWarnings("unchecked")
    void fireCommand(String puppetName, Enum<Commands> command, Object data){
        if (StringUtils.isEmpty(puppetName)){
            throw new MasterChannelHandlerException(ExceptionMessageConstants.PUPPET_NAME_EMPTY);
        }

        try {
        getFireCommandHandler(command).fire(puppetName,command,data);
        LOGGER.debug("fire a command to server:{}",command);
        } catch (FireCommandHandlerException e) {
            throw new MasterChannelHandlerException(e.getMessage(), e);
        }
    }


    @SuppressWarnings("unchecked")
    private AbstractMasterFireCommandHandler<Object> getFireCommandHandler(Enum<Commands> command) throws MasterChannelHandlerException{
        try {
            final ICommandHandler commandHandler = CommandHandlerLoader.getCommandHandler(command);
            if (commandHandler instanceof AbstractMasterFireCommandHandler) {
                return (AbstractMasterFireCommandHandler<Object>) commandHandler;
            } else {
                throw new MasterChannelHandlerException(ExceptionMessageConstants.FIRE_COMMAND_HANDLE_ERROR);
            }
        }catch (CommandHandlerLoaderException e){
            throw new MasterChannelHandlerException(e.getMessage(),e);
        }

    }
}
