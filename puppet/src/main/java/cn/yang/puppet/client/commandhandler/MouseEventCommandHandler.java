package cn.yang.puppet.client.commandhandler;

import cn.yang.common.InputEvent.MouseEvent;
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
public class MouseEventCommandHandler extends AbstractPuppetCommandHandler {


    public MouseEventCommandHandler() throws CommandHandlerException,AWTException{

    }

    @Override
    protected void handle0(ChannelHandlerContext ctx, Response response) throws Exception {
        Object obj=response.getResult();

        if (obj==null){
            LOGGER.error(cn.yang.puppet.client.constant.ExceptionConstants.MOUSE_EVENT_NULL);
            throw new NullValueException(ExceptionConstants.MOUSE_EVENT_NULL);
        }

        if (!(obj instanceof MouseEvent)){
            LOGGER.error(cn.yang.puppet.client.constant.ExceptionConstants.MOUSE_EVENT_ERROR);
            throw new ClassCastException(ExceptionConstants.MOUSE_EVENT_ERROR);
        }

        MouseEvent mouseEvent=(MouseEvent)obj;
        if(mouseEvent.isClicked()){
            puppetDesktop.mouseClick(mouseEvent);
        }

        if(mouseEvent.isDoubleClicked()){
            puppetDesktop.mouseDoubleClick(mouseEvent);
        }

        if(mouseEvent.isDragged()){
            puppetDesktop.mouseDragged(mouseEvent,mouseEvent.getSite());
        }

        if(mouseEvent.isMouseMoved()){
            puppetDesktop.mouseMove(mouseEvent.getSite());
        }

        if(mouseEvent.isMouseWheel()){
            puppetDesktop.mouseWheel(mouseEvent);
        }

        if(mouseEvent.isMousePressed()){
            puppetDesktop.mousePress(mouseEvent);
        }

        if(mouseEvent.isMouseReleased()){
            puppetDesktop.mouseRelease(mouseEvent);
        }

    }
}
