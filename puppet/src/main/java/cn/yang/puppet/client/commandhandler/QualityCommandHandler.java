package cn.yang.puppet.client.commandhandler;

import cn.yang.common.dto.Response;
import cn.yang.puppet.client.constant.ExceptionMessageConstants;
import cn.yang.puppet.client.constant.PuppetDynamicSetting;
import cn.yang.puppet.client.exception.NullValueException;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class QualityCommandHandler extends AbstractPuppetCommandHandler {


    @Override
    protected void handle0(ChannelHandlerContext ctx, Response request) throws Exception {
        final Object result = request.getValue();
        if (result==null){
            error(request,ExceptionMessageConstants.QUALITY_EVENT_VALUE_NULL);
            throw new NullValueException(ExceptionMessageConstants.QUALITY_EVENT_VALUE_NULL);
        }

        if (!(request.getValue() instanceof Integer)){
            error(request,ExceptionMessageConstants.QUALITY_EVENT_VALUE_ERROR);
            throw new ClassCastException(ExceptionMessageConstants.QUALITY_EVENT_VALUE_ERROR);
        }

        PuppetDynamicSetting.quality = ((Integer)result);
    }
}
