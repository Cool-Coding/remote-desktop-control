package cn.yang.common.generator;

import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

/**
 * @author Cool-Coding
 *         2018/8/3
 */
public class SimplePuppetNameGenerator implements PuppetNameGenerate {
    @Override
    public String getPuppetName(ChannelHandlerContext ctx) {
        return UUID.randomUUID().toString();
    }
}
