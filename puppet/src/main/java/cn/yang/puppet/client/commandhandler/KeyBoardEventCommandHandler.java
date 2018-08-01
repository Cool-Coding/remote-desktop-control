package cn.yang.puppet.client.commandhandler;

import cn.yang.common.InputEvent.MasterKeyEvent;
import cn.yang.common.dto.Response;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.puppet.client.constant.ExceptionConstants;
import cn.yang.puppet.client.exception.NullValueException;
import io.netty.channel.ChannelHandlerContext;

import java.awt.*;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class KeyBoardEventCommandHandler extends AbstractPuppetCommandHandler {


    public KeyBoardEventCommandHandler() throws CommandHandlerException,AWTException{

    }

    @Override
    protected void handle0(ChannelHandlerContext ctx, Response response) throws Exception {
        Object obj=response.getResult();

        if (obj==null){
            LOGGER.error(cn.yang.puppet.client.constant.ExceptionConstants.KEYBOARD_EVENT_NULL);
            throw new NullValueException(ExceptionConstants.KEYBOARD_EVENT_NULL);
        }

        if (!(obj instanceof MasterKeyEvent)){
            LOGGER.error(cn.yang.puppet.client.constant.ExceptionConstants.KEYBOARD_EVENT_ERROR);
            throw new ClassCastException(ExceptionConstants.KEYBOARD_EVENT_ERROR);
        }

        MasterKeyEvent keyEvent=(MasterKeyEvent)obj;
        LOGGER.info(keyEvent.toString());
        if(keyEvent.isPressed()){
            puppetDesktop.keyPress(keyEvent);
            puppetDesktop.keyRelease(keyEvent);
        }else{
            //为了处理pressed之后的延迟，press后立即释放
            //puppetDesktop.keyRelease(keyEvent);
        }
    }
}
