package cn.yang.server.commandhandler;

import cn.yang.common.dto.Request;
import cn.yang.common.command.Commands;
import cn.yang.server.netty.ChannelPair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import static cn.yang.common.constant.ExceptionMessageConstants.MASTER_CONNECTION_LOST;
import static cn.yang.common.constant.ExceptionMessageConstants.WRONG_CONNECT_VALUE;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class ScreenCommandHandler extends AbstractServerCommandHandler {
    @Override
    public void handle0(ChannelHandlerContext ctx, Request request) throws Exception {
        final String puppetName = request.getPuppetName();

        //检查请求的内容
        if(!(request.getValue() instanceof byte[])){
            final String should_be_bufferedImage = String.format("%s %s %s", WRONG_CONNECT_VALUE, request.getValue(), "should be BufferedImage");
            error(request,should_be_bufferedImage);
            sendError(request,ctx,should_be_bufferedImage);
            return;
        }

        //检查控制端连接是否正常
        final ChannelPair channelPair = CONNECTED_CHANNELPAIRS.get(puppetName);
        if (channelPair==null || channelPair.getMasterChannel()==null || !channelPair.getMasterChannel().isOpen()){
            error(request,MASTER_CONNECTION_LOST);
            sendError(request,ctx,MASTER_CONNECTION_LOST);
            return;
        }

        final Channel masterChannel = channelPair.getMasterChannel();
        masterChannel.writeAndFlush(buildResponse(request, Commands.SCREEN,request.getValue()));
    }
}
