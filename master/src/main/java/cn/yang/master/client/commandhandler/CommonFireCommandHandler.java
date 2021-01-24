package cn.yang.master.client.commandhandler;

import cn.yang.common.command.Commands;
import cn.yang.common.dto.Request;
import cn.yang.common.util.TaskExecutors;
import cn.yang.master.client.constant.ExceptionMessageConstants;
import cn.yang.master.client.exception.ConnectionException;
import cn.yang.master.client.exception.FireCommandHandlerException;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Cool-Coding
 *         2018/7/27
 */
public class CommonFireCommandHandler extends AbstractMasterFireCommandHandler<Object> {
   private ArrayBlockingQueue<Request> blockingQueue=new ArrayBlockingQueue<>(100);

   private Runnable task=new Runnable() {
       @Override
       public void run() {
           while (true) {
               Request request=null;
               try {
                   while ((request = blockingQueue.take()) != null) {
                       try {
                           sendRequest(request);
                       } catch (ConnectionException e) {
                           error(request, e.getMessage());
                       }
                   }
               }catch (InterruptedException e){
                   error(this,e.getMessage());
               }
           }
       }
   };

   public CommonFireCommandHandler(){
       TaskExecutors.submit(task,1000);
   }

    @Override
    public void fire(String puppetName,Enum<Commands> command,Object data) throws FireCommandHandlerException {
        if (puppetName==null){
            throw new FireCommandHandlerException(ExceptionMessageConstants.PUPPET_NAME_EMPTY);
        }

        final Request request = buildRequest(command, puppetName, data);

        blockingQueue.offer(request);
    }
}
