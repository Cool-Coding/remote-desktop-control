package cn.yang.server.commandhandler;

import cn.yang.common.command.Commands;
import cn.yang.common.constant.ExceptionConstants;
import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.server.netty.ChannelPair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import static cn.yang.common.constant.ExceptionConstants.PUPPET_CONNECTION_LOST;
import static cn.yang.common.constant.ExceptionConstants.WRONG_CONNECT_VALUE;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class QualityCommandHandler extends AbstractServerCommandHandler{
    @Override
    public void handle0(ChannelHandlerContext ctx, Request request) throws Exception {
        final String puppetName = request.getPuppetName();

        if (!(request.getValue() instanceof Integer)){
            final String should_be_integer_type = String.format("%s %s", WRONG_CONNECT_VALUE, "should be Integer type");
            error(request,should_be_integer_type);
            sendError(request,ctx,should_be_integer_type);
            return;
        }

        //检查傀儡端连接是否正常
        ChannelPair channelPair = CONNECTED_CHANNELPAIRS.get(puppetName);
        if (channelPair==null || channelPair.getPuppetChannel()==null || !channelPair.getPuppetChannel().isOpen()){
            error(request,ExceptionConstants.PUPPET_CONNECTION_LOST);
            sendError(request, ctx,PUPPET_CONNECTION_LOST);
            return;
        }

        //发送数据
        final Channel puppetChannel = channelPair.getPuppetChannel();
        Response response=null;
        response = buildResponse(request, Commands.QUALITY, request.getValue());
        puppetChannel.writeAndFlush(response);

    }
}
