package cn.yang.puppet.client.commandhandler;

import cn.yang.common.dto.Response;
import cn.yang.common.exception.CommandHandlerException;
import cn.yang.puppet.client.constant.ExceptionConstants;
import cn.yang.puppet.client.exception.NullValueException;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.cache.support.NullValue;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class QualityCommandHandler extends AbstractPuppetCommandHandler {

    public QualityCommandHandler() throws CommandHandlerException{

    }

    @Override
    protected void handle0(ChannelHandlerContext ctx, Response request) throws Exception {
        final Object result = request.getResult();
        if (result==null){
            LOGGER.error(ExceptionConstants.QUALITY_EVENT_VALUE_NULL);
            throw new NullValueException(ExceptionConstants.QUALITY_EVENT_VALUE_NULL);
        }

        if (!(request.getResult() instanceof Integer)){
            LOGGER.error(ExceptionConstants.QUALITY_EVENT_VALUE_ERROR);
            throw new ClassCastException(ExceptionConstants.QUALITY_EVENT_VALUE_ERROR);
        }


        getPuppetDesktop().setQuality((Integer)result);
    }
}
