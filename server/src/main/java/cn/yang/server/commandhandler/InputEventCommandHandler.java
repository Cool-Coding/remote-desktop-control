package cn.yang.server.commandhandler;

import cn.yang.common.InputEvent.MasterKeyEvent;
import cn.yang.common.InputEvent.MouseEvent;
import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.command.Commands;
import cn.yang.common.constant.ExceptionMessageConstants;
import cn.yang.server.netty.ChannelPair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import static cn.yang.common.constant.ExceptionMessageConstants.PUPPET_CONNECTION_LOST;
import static cn.yang.common.constant.ExceptionMessageConstants.WRONG_CONNECT_VALUE;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class InputEventCommandHandler  extends AbstractServerCommandHandler{
    @Override
    public void handle0(ChannelHandlerContext ctx, Request request) throws Exception {
        final String puppetName = request.getPuppetName();

        if (!(request.getValue() instanceof MasterKeyEvent || request.getValue() instanceof MouseEvent)){
            final String should_be_inputEvent_type = String.format("%s %s", WRONG_CONNECT_VALUE, "should be InputEvent type");
            error(request,should_be_inputEvent_type);
            sendError(request,ctx,should_be_inputEvent_type);
            return;
        }

        //检查傀儡端连接是否正常
        ChannelPair channelPair = CONNECTED_CHANNELPAIRS.get(puppetName);
        if (channelPair==null || channelPair.getPuppetChannel()==null || !channelPair.getPuppetChannel().isOpen()){
            error(request, ExceptionMessageConstants.PUPPET_CONNECTION_LOST);
            sendError(request, ctx,PUPPET_CONNECTION_LOST);
            return;
        }

        //发送数据
        final Channel puppetChannel = channelPair.getPuppetChannel();
         Response response=null;
        if(request.getValue() instanceof MasterKeyEvent){
            response = buildResponse(request, Commands.KEYBOARD, request.getValue());
        }else if(request.getValue() instanceof MouseEvent){
            response = buildResponse(request, Commands.MOUSE, request.getValue());
        }
        puppetChannel.writeAndFlush(response);

    }
}
