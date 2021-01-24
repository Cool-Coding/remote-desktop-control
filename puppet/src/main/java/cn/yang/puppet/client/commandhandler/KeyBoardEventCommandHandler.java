package cn.yang.puppet.client.commandhandler;

import cn.yang.common.InputEvent.MasterKeyEvent;
import cn.yang.common.dto.Response;
import cn.yang.puppet.client.constant.ExceptionMessageConstants;
import cn.yang.puppet.client.exception.NullValueException;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class KeyBoardEventCommandHandler extends AbstractPuppetCommandHandler {


    @Override
    protected void handle0(ChannelHandlerContext ctx, Response response) throws Exception {
        Object obj=response.getValue();

        if (obj==null){
            error(response, ExceptionMessageConstants.KEYBOARD_EVENT_NULL);
            throw new NullValueException(ExceptionMessageConstants.KEYBOARD_EVENT_NULL);
        }

        if (!(obj instanceof MasterKeyEvent)){
            error(response, ExceptionMessageConstants.KEYBOARD_EVENT_ERROR);
            throw new ClassCastException(ExceptionMessageConstants.KEYBOARD_EVENT_ERROR);
        }

        MasterKeyEvent keyEvent=(MasterKeyEvent)obj;
        debug(response,keyEvent.toString());
        if(keyEvent.isPressed()){
            REPLAY.keyPress(keyEvent);
            REPLAY.keyRelease(keyEvent);
        }else{
            //为了处理pressed之后的延迟，press后立即释放
            //REPLAY.keyRelease(keyEvent);
        }
    }
}
