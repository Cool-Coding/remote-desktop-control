package cn.yang.master.client.commandhandler;

import cn.yang.common.command.Commands;
import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.common.util.BeanUtil;
import cn.yang.master.client.constant.ExceptionConstants;
import cn.yang.master.client.exception.ConnectionException;
import cn.yang.master.client.exception.FireCommandHandlerException;
import cn.yang.master.client.ui.MasterDesktop;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class FireCommandHandler extends AbstractMasterFireCommandHandler<Object> {
    public FireCommandHandler() throws CommandHandlerException{

    }

    @Override
    public void fire(String puppetName,Enum<Commands> command,Object data) throws FireCommandHandlerException {
        if (puppetName==null){
            throw new FireCommandHandlerException(ExceptionConstants.PUPPET_NAME_EMPTY);
        }

        final Request request = buildRequest(command, puppetName, data);
        try {
            sendRequest(request);
        }catch (ConnectionException e){
            throw  new FireCommandHandlerException(e);
        }
    }
}
