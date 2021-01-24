package cn.yang.master.client.commandhandler;

import cn.yang.common.command.Commands;
import cn.yang.common.dto.Response;
import cn.yang.master.client.constant.ExceptionMessageConstants;
import cn.yang.master.client.exception.FireCommandHandlerException;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public abstract class AbstractMasterFireCommandHandler<T> extends AbstractMasterCommandHandler {

    @Override
    protected void handle0(ChannelHandlerContext ctx, Response response) throws Exception {
        throw new UnsupportedOperationException(String.format("%s %s", ExceptionMessageConstants.MASTER_FIRE_COMMAND_ERROR,response.getCommand()));
    }

    /**
     * 控制端向服务器发送命令
     * @param puppetName 傀儡名称
     * @param command 命令
     * @param data 数据
     *
     */
    public abstract void fire(String puppetName, Enum<Commands> command, T data) throws FireCommandHandlerException;
}
