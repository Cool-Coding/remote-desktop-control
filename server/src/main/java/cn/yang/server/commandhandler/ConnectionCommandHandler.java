package cn.yang.server.commandhandler;

import cn.yang.common.command.Commands;
import cn.yang.common.constant.Constants;
import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.generator.PuppetNameGenerate;
import cn.yang.common.util.BeanUtil;
import cn.yang.server.netty.ChannelPair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.util.StringUtils;

import static cn.yang.common.constant.ExceptionMessageConstants.*;
import static cn.yang.server.constant.MessageConstants.CONNECTION_SUCCEED;

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
        String puppetName = request.getPuppetName();
        if(!StringUtils.isEmpty(puppetName)){
            if(request.getValue()!=null){
                if (request.getValue() instanceof Integer){
                    Integer count = (Integer)request.getValue();
                    if (count <= 1){
                        error(request,ILLEGAL_STATUS);
                        sendError(request,ctx,ILLEGAL_STATUS);
                        return;
                    }
                }
            }

            ChannelPair channelPair;
            if(!CONNECTED_CHANNELPAIRS.containsKey(puppetName)){
                channelPair=new ChannelPair();
                channelPair.setPuppetChannel(ctx.channel());
                CONNECTED_CHANNELPAIRS.put(puppetName,channelPair);
            }else {
                channelPair = CONNECTED_CHANNELPAIRS.get(puppetName);
                final Channel puppetChannel = channelPair.getPuppetChannel();
                if(puppetChannel!=null && puppetChannel.isOpen()){
                    error(request,CONNECTION_EXIST);
                    sendError(request,ctx,CONNECTION_EXIST);
                    return;
                }else{
                    channelPair.setPuppetChannel(ctx.channel());
                }
            }

            info(request,CONNECTION_SUCCEED);
            final Channel masterChannel = channelPair.getMasterChannel();
            //如果傀儡掉线后，再次重连，发现控制端在线,并且没有终止控制傀儡，则继续发送屏幕截图
            if (masterChannel!=null && masterChannel.isOpen()){
                ctx.writeAndFlush(buildResponse(request, Commands.CONTROL));
              //否则，如果控制端不在线并且傀儡是断线重连的情况，则向其发送终止命令，停止其向服务器发送屏幕截图
            }else{
               ctx.writeAndFlush(buildResponse(request, Commands.TERMINATE));
            }
        }else {
            ChannelPair pair=new ChannelPair();
            pair.setPuppetChannel(ctx.channel());
            puppetName = BeanUtil.getBean(PuppetNameGenerate.class).getPuppetName(ctx);
            CONNECTED_CHANNELPAIRS.put(puppetName,pair);
            request.setPuppetName(puppetName);
            info(request,CONNECTION_SUCCEED);
        }

        Response response = buildResponse(request, Commands.CONNECT,request.getValue());
        ctx.writeAndFlush(response);
    }
}
