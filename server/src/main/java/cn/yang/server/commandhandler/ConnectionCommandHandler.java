package cn.yang.server.commandhandler;

import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.command.Commands;
import cn.yang.common.constant.Constants;
import cn.yang.server.netty.ChannelPair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import static cn.yang.server.constant.MessageConstants.CONNECTION_SUCCEED;
import static cn.yang.common.constant.ExceptionMessageConstants.CONNECTION_EXIST;
import static cn.yang.common.constant.ExceptionMessageConstants.WRONG_CLIENT_TYPE;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class ConnectionCommandHandler extends AbstractServerCommandHandler {
    @Override
    public void handle0(ChannelHandlerContext ctx, Request request) throws Exception {
        String requestId=request.getId();
        switch (requestId.charAt(0)){
            case Constants.MASTER:
                handleMaster(ctx,request);
                break;
            case Constants.PUPPET:
                handlePuppet(ctx,request);
                break;
            default:
                error(request,WRONG_CLIENT_TYPE);
                sendError(request,ctx,WRONG_CLIENT_TYPE);
        }
    }

    private void handleMaster(ChannelHandlerContext ctx,Request request){
        Response response = buildResponse(request, Commands.CONNECT);
        ctx.writeAndFlush(response);
    }

    private void handlePuppet(ChannelHandlerContext ctx,Request request){
        final String puppetName = request.getPuppetName();
        if(CONNECTED_CHANNELPAIRS.containsKey(puppetName)){
            final ChannelPair channelPair = CONNECTED_CHANNELPAIRS.get(puppetName);
            final Channel puppetChannel = channelPair.getPuppetChannel();
            if(puppetChannel!=null && puppetChannel.isOpen()){
                error(request,CONNECTION_EXIST);
                sendError(request,ctx,CONNECTION_EXIST);
            }else{
                channelPair.setPuppetChannel(ctx.channel());
                info(request,CONNECTION_SUCCEED);
                final Channel masterChannel = channelPair.getMasterChannel();
                //如果傀儡掉线后，再次重连，发现控制端在线,并且没有终止控制傀儡，则继续发送屏幕截图
                if (masterChannel!=null && masterChannel.isOpen()){
                    ctx.writeAndFlush(buildResponse(request, Commands.CONTROL));
                }
            }
        }else {
            ChannelPair pair=new ChannelPair();
            pair.setPuppetChannel(ctx.channel());
            CONNECTED_CHANNELPAIRS.put(puppetName,pair);
            info(request,CONNECTION_SUCCEED);
        }

        Response response = buildResponse(request, Commands.CONNECT);
        ctx.writeAndFlush(response);
    }
}
