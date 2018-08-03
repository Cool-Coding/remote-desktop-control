package cn.yang.common.generator;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/8/3
 */
public interface PuppetNameGenerate {
    String getPuppetName(ChannelHandlerContext ctx);
}
