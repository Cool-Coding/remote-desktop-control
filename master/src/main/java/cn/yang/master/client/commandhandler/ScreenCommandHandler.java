package cn.yang.master.client.commandhandler;

import cn.yang.common.dto.Response;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.common.util.BeanUtil;
import cn.yang.common.util.ImageUtils;
import cn.yang.master.client.ui.MasterDesktop;
import io.netty.channel.ChannelHandlerContext;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class ScreenCommandHandler extends AbstractMasterCommandHandler {
    public ScreenCommandHandler() throws CommandHandlerException{

    }

    @Override
    protected void handle0(ChannelHandlerContext ctx, Response response) throws Exception {
       saveScreen(response);
    }

    private void saveScreen(Response response){
        //LOGGER.info("receive a screen snapshot from puppet");
        if(response.getResult() instanceof byte[]) {
            byte[] bytes=(byte[])response.getResult();
            BufferedImage bufferedImage=ImageUtils.getImageFromByteArray(bytes);
            final MasterDesktop desktop = BeanUtil.getBean(MasterDesktop.class,"desktop");
            desktop.refreshScreen(response.getPuppetName(),bufferedImage);
        }
    }
}
