package cn.yang.master.client.commandhandler;

import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.command.Commands;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.common.util.BeanUtil;
import cn.yang.master.client.exception.ConnectionException;
import cn.yang.master.client.ui.IMasterDesktop;
import cn.yang.master.client.constant.ExceptionMessageConstants;
import cn.yang.master.client.exception.FireCommandHandlerException;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class ControlFireCommandHandler extends AbstractMasterFireCommandHandler<String> {
    public ControlFireCommandHandler() throws CommandHandlerException{

    }

    @Override
    protected void handle0(ChannelHandlerContext ctx, Response response) throws Exception {
        if(response.getValue() instanceof String) {
            //控制成功后,创建一个PuppetScreen对象，准备显示Puppet屏幕
            final IMasterDesktop desktop = BeanUtil.getBean(IMasterDesktop.class);
            desktop.lanuch((String) response.getValue());
        }else{
            throw new CommandHandlerException(ExceptionMessageConstants.CONTRL_COMMAND_RESULT_ERROR);
        }
    }

    @Override
    public void fire(String puppetName,Enum<Commands> command,String puppetName2) throws FireCommandHandlerException {
        if (puppetName==null){
            throw new FireCommandHandlerException(ExceptionMessageConstants.PUPPET_NAME_EMPTY);
        }

        final Request request = buildRequest(Commands.CONTROL, puppetName, null);
        try {
            sendRequest(request);
        }catch (ConnectionException e){
            throw new FireCommandHandlerException(e);
        }
    }
}
