package cn.yang.master.client.commandhandler;

import cn.yang.common.dto.Request;
import cn.yang.common.command.Commands;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.master.client.constant.ExceptionMessageConstants;
import cn.yang.master.client.exception.ConnectionException;
import cn.yang.master.client.exception.FireCommandHandlerException;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class TerminateFireCommandHandler extends AbstractMasterFireCommandHandler<String> {
    public TerminateFireCommandHandler() throws CommandHandlerException{

    }

    @Override
    public void fire(String puppetName,Enum<Commands> command,String puppetName2) throws FireCommandHandlerException {
        if (puppetName==null){
            throw new FireCommandHandlerException(ExceptionMessageConstants.PUPPET_NAME_EMPTY);
        }

        final Request request = buildRequest(Commands.TERMINATE, puppetName, null);
        try {
            sendRequest(request);
        }catch (ConnectionException e){
            throw new FireCommandHandlerException(e);
        }
    }
}
