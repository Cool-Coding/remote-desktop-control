package cn.yang.master.client.commandhandler;

import cn.yang.common.dto.Response;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.common.util.BeanUtil;
import cn.yang.master.client.constant.MessageConstants;
import cn.yang.master.client.ui.IMasterDesktop;
import io.netty.channel.ChannelHandlerContext;

import java.awt.image.BufferedImage;

/**
 * @author Cool-Coding
 *         2018/7/27
 *傀儡传过来的屏幕命令处理逻辑
 */
public class ScreenCommandHandler extends AbstractMasterCommandHandler {
    public ScreenCommandHandler() throws CommandHandlerException{

    }

    @Override
    protected void handle0(ChannelHandlerContext ctx, Response response) throws Exception {
       refreshScreen(response);
    }

    private void refreshScreen(Response response){
        info(response, MessageConstants.RECEIVE_SCREEN_SNAPSHOT);
        if(response.getValue() instanceof byte[]) {
            byte[] bytes=(byte[])response.getValue();
            final IMasterDesktop desktop = BeanUtil.getBean(IMasterDesktop.class);
            desktop.refreshScreen(response.getPuppetName(),bytes);
        }
    }
}
