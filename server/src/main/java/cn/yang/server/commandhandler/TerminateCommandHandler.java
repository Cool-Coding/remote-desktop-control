package cn.yang.server.commandhandler;

import cn.yang.common.constant.ExceptionMessageConstants;
import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.server.netty.ChannelPair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import static cn.yang.common.constant.ExceptionMessageConstants.PUPPET_CONNECTION_LOST;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class TerminateCommandHandler extends AbstractServerCommandHandler{
    @Override
    public void handle0(ChannelHandlerContext ctx, Request request) throws Exception {
        final String puppetName = request.getPuppetName();

        //检查傀儡端连接是否正常
        ChannelPair channelPair = CONNECTED_CHANNELPAIRS.get(puppetName);
        if (channelPair==null || channelPair.getPuppetChannel()==null || !channelPair.getPuppetChannel().isOpen()){
            error(request, ExceptionMessageConstants.PUPPET_CONNECTION_LOST);
            sendError(request, ctx,PUPPET_CONNECTION_LOST);
            return;
        }

        // 删除链接
        channelPair.setMasterChannel(null);

        //发送数据
        final Channel puppetChannel = channelPair.getPuppetChannel();
        Response response=null;
        response = buildResponse(request,request.getCommand(),request.getValue());
        puppetChannel.writeAndFlush(response);
    }
}
