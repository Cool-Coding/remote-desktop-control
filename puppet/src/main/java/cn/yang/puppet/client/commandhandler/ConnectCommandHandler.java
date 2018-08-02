package cn.yang.puppet.client.commandhandler;

import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.command.Commands;
import cn.yang.common.util.PropertiesUtil;
import cn.yang.puppet.client.constant.ConfigConstants;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.puppet.client.constant.MessageConstants;
import cn.yang.puppet.client.exception.HeartBeatException;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class ConnectCommandHandler extends AbstractPuppetCommandHandler {

    private String host;
    private int port;

    public ConnectCommandHandler() throws CommandHandlerException{
        try {
            host = PropertiesUtil.getString(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.SERVER_IP);
            port = PropertiesUtil.getInt(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.SERVER_PORT);
        }catch (IOException e){
            throw new CommandHandlerException(e.getMessage(),e);
        }
    }

    @Override
    protected void handle0(ChannelHandlerContext ctx, Response response) throws Exception {
        sendHeartBeat(ctx,response);
    }

    private void sendHeartBeat(ChannelHandlerContext ctx,Response response) throws HeartBeatException {
        try {
            //发送心跳
            int interval = PropertiesUtil.getInt(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.HEARTBEAT_INTERVAL);
            ctx.executor().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Request heartBeatRequest;
                    if(isUnderControlled()){
                        final byte[] bytes = REPLAY.getScreenSnapshot();
                        heartBeatRequest = buildRequest(Commands.SCREEN, bytes);
                    }else {
                        heartBeatRequest=getHeartBeatRequest();
                        debug(response, MessageConstants.SEND_A_HEARTBEAT,host,String.valueOf(port));
                    }

                    if (heartBeatRequest!=null) {
                        if (ctx.channel()!=null && ctx.channel().isOpen()) {
                            ctx.writeAndFlush(heartBeatRequest);
                        }
                    }
                }
            }, 0, interval, TimeUnit.MILLISECONDS);
        }catch (IOException e){
            ctx.executor().shutdownGracefully();
            throw new HeartBeatException(e.getMessage(),e);
        }
    }

    public Request getHeartBeatRequest(){
        return buildRequest(Commands.HEARTBEAT,null);
    }
}
