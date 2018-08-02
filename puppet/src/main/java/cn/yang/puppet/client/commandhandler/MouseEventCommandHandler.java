package cn.yang.puppet.client.commandhandler;

import cn.yang.common.InputEvent.MouseEvent;
import cn.yang.common.dto.Response;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.puppet.client.constant.ExceptionMessageConstants;
import cn.yang.puppet.client.exception.NullValueException;
import io.netty.channel.ChannelHandlerContext;

import java.awt.*;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class MouseEventCommandHandler extends AbstractPuppetCommandHandler {


    public MouseEventCommandHandler() throws CommandHandlerException,AWTException{

    }

    @Override
    protected void handle0(ChannelHandlerContext ctx, Response response) throws Exception {
        Object obj=response.getValue();

        if (obj==null){
            error(response, ExceptionMessageConstants.MOUSE_EVENT_NULL);
            throw new NullValueException(ExceptionMessageConstants.MOUSE_EVENT_NULL);
        }

        if (!(obj instanceof MouseEvent)){
            error(response, ExceptionMessageConstants.MOUSE_EVENT_ERROR);
            throw new ClassCastException(ExceptionMessageConstants.MOUSE_EVENT_ERROR);
        }

        MouseEvent mouseEvent=(MouseEvent)obj;
        if(mouseEvent.isClicked()){
            REPLAY.mouseClick(mouseEvent);
        }

        if(mouseEvent.isDoubleClicked()){
            REPLAY.mouseDoubleClick(mouseEvent);
        }

        if(mouseEvent.isDragged()){
            REPLAY.mouseDragged(mouseEvent,mouseEvent.getSite());
        }

        if(mouseEvent.isMouseMoved()){
            REPLAY.mouseMove(mouseEvent.getSite());
        }

        if(mouseEvent.isMouseWheel()){
            REPLAY.mouseWheel(mouseEvent);
        }

        if(mouseEvent.isMousePressed()){
            REPLAY.mousePress(mouseEvent);
        }

        if(mouseEvent.isMouseReleased()){
            REPLAY.mouseRelease(mouseEvent);
        }

    }
}
