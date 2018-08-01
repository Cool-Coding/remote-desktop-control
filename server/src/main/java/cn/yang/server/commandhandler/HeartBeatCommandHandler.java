package cn.yang.server.commandhandler;

import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.command.Commands;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class HeartBeatCommandHandler extends AbstractServerCommandHandler {
    @Override
    public void handle0(ChannelHandlerContext ctx, Request request) throws Exception {
        final String puppetName = request.getPuppetName();
        info(request,"receive a heartbeat");
        if(PRECONTROL_PUPPETS.size() > 0 && PRECONTROL_PUPPETS.contains(puppetName)){
            Response response=buildResponse(request, Commands.CONTROL);
            ctx.writeAndFlush(response).addListener((f)->{
                //如果通知傀儡连接成功，则从待控制集合中移除
                if(f.isSuccess()){
                    PRECONTROL_PUPPETS.remove(puppetName);
                }
            });
        }
    }
}
