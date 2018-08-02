package cn.yang.master.client.commandhandler;

import cn.yang.common.InputEvent.MasterKeyEvent;
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
public class KeyEventFireCommandHandler extends AbstractMasterFireCommandHandler<MasterKeyEvent> {
    public KeyEventFireCommandHandler() throws CommandHandlerException{

    }

    @Override
    public void fire(String puppetName,Enum<Commands> command,MasterKeyEvent keyEvent) throws FireCommandHandlerException {
        if (puppetName==null){
            throw new FireCommandHandlerException(ExceptionMessageConstants.PUPPET_NAME_EMPTY);
        }

        final Request request = buildRequest(Commands.KEYBOARD, puppetName, keyEvent);
        try {
            sendRequest(request);
        }catch (ConnectionException e){
            throw new FireCommandHandlerException(e);
        }
    }
}
