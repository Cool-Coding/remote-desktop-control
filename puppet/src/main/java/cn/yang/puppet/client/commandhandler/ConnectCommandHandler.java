package cn.yang.puppet.client.commandhandler;

import cn.yang.common.dto.Request;
import cn.yang.common.dto.Response;
import cn.yang.common.command.Commands;
import cn.yang.common.exception.ConnectionException;
import cn.yang.common.util.PropertiesUtil;
import cn.yang.puppet.client.constant.ConfigConstants;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.puppet.client.constant.ExceptionMessageConstants;
import cn.yang.puppet.client.constant.MessageConstants;
import cn.yang.puppet.client.exception.HeartBeatException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
        if(StringUtils.isEmpty(getPuppetName())){
            setPuppetName(response.getPuppetName());
            info(response,MessageConstants.PUPPET_NAME_FROM_SERVER,response.getPuppetName());
            popMessageDialog(MessageConstants.PUPPET_NAME_FROM_SERVER,response.getPuppetName());
        }

        try {
            //为减少带宽负载，发送屏幕截图时不再发送心跳，当屏幕截图发送完后，继续发送心跳，
            //启动一个线程，进行周期性检查，管理心跳与屏幕截图任务
            final TaskManagement taskManagement = new TaskManagement(ctx, response);
            int interval = PropertiesUtil.getInt(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.TASK_CHECK_INTERVAL);
            ctx.executor().scheduleAtFixedRate(()->{
                    try {
                        taskManagement.check();
                    }catch (IOException e){
                       error(response,e.getMessage());
                       throw new RuntimeException(e);
                    }
            },0,interval,TimeUnit.MILLISECONDS);

        }catch (IOException e){
            ctx.executor().shutdownGracefully();
            throw new HeartBeatException(e.getMessage(),e);
        }
    }


    private class TaskManagement {
        private final Map<Runnable,ScheduledFuture> tasks=new HashMap<>();
        private ChannelHandlerContext ctx;
        private Response response;

        private TaskManagement(ChannelHandlerContext ctx,Response response){
            this.ctx=ctx;
            this.response=response;
        }

        private void check() throws IOException{
            if(isUnderControlled()){
                if(tasks.get(heartBeatTask)!=null && !tasks.get(heartBeatTask).isCancelled()){
                    tasks.get(heartBeatTask).cancel(true);
                }

                if (tasks.get(screenSnapShotTask)==null || tasks.get(screenSnapShotTask).isCancelled()){
                    tasks.put(screenSnapShotTask,startScreenSnapShotTask());
                }
            }else{
                if(tasks.get(screenSnapShotTask)!=null && !tasks.get(screenSnapShotTask).isCancelled()){
                    tasks.get(screenSnapShotTask).cancel(true);
                }

                if (tasks.get(heartBeatTask)==null || tasks.get(heartBeatTask).isCancelled()){
                    tasks.put(heartBeatTask,startHeartBeatTask());
                }
            }
        }

        /**
         * 开始发送屏幕截图
         * @return 返回任务执行器
         * @throws IOException 读取配置文件异常
         */
        private ScheduledFuture<?> startScreenSnapShotTask() throws IOException{
            int interval = PropertiesUtil.getInt(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.SCREEN_REFRESH_FREQUENCY);
            return ctx.executor().scheduleAtFixedRate(screenSnapShotTask, 0, interval, TimeUnit.MILLISECONDS);
        }


        /**
         * 开始发送心跳
         * @throws IOException  读取配置文件异常
         * @return 返回任务执行器
         */
        private ScheduledFuture<?> startHeartBeatTask() throws IOException{
            int interval = PropertiesUtil.getInt(ConfigConstants.CONFIG_FILE_PATH, ConfigConstants.HEARTBEAT_INTERVAL);
            return ctx.executor().scheduleAtFixedRate(heartBeatTask, 0, interval, TimeUnit.MILLISECONDS);
        }

        /**
         * 心跳任务
         */
        private Runnable heartBeatTask = new Runnable() {
            @Override
            public void run() {
                Request heartBeatRequest = AbstractPuppetCommandHandler.buildRequest(Commands.HEARTBEAT,null);
                debug(response, MessageConstants.SEND_A_HEARTBEAT, host, String.valueOf(port));

                if (heartBeatRequest != null) {
                    if (ctx.channel() != null && ctx.channel().isOpen()) {
                        ctx.writeAndFlush(heartBeatRequest);
                    }
                }
            }
        };

        /**
         * 屏幕截图任务
         */
        private Runnable screenSnapShotTask = new Runnable(){
            @Override
            public void run() {
                final byte[] bytes = REPLAY.getScreenSnapshot();
                final Request request = buildRequest(Commands.SCREEN, bytes);
                debug(response,MessageConstants.SEND_A_SCREENSNAPSHOT, host, String.valueOf(port));
                if (request != null) {
                    if (ctx.channel() != null && ctx.channel().isOpen()) {
                        ctx.writeAndFlush(request);
                    }
                }
            }
        };
    }
}
