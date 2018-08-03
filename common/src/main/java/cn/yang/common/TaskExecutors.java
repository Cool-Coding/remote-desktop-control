package cn.yang.common;

import cn.yang.common.exception.TaskExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author Cool-Coding
 * 2018/7/25
 */
public class TaskExecutors {
    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutors.class);

    /**
     * 计划线程池
     */
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()+1);

    /**
     * 提交任务执行
     * @param callable 任务
     * @param interval 重试间隔
     * @param times 重试次数
     * @param <T> 任务结果类型
     * @return 任务执行结果
     * @throws TaskExecutorException 任务异常
     */
    public static <T> T submit(Callable<T> callable,long interval,int times) throws TaskExecutorException{
        ThreadLocal<Integer> count=new ThreadLocal<>();
        count.set(1);
        //第一次立即执行
        ScheduledFuture<T> schedule = SCHEDULED_EXECUTOR_SERVICE.schedule(callable, 0, TimeUnit.MICROSECONDS);
        //执行失败，则重试
        try {
            if (schedule.get() == null) {
                while (count.get() <= times) {
                    schedule = SCHEDULED_EXECUTOR_SERVICE.schedule(callable, interval, TimeUnit.MILLISECONDS);
                    //注意:要想重试，callable中的返回值必须null
                    if (schedule.get() != null) {
                        return schedule.get();
                    }
                    count.set(count.get()+1);
                }
            }else{
                return schedule.get();
            }
        }catch (ExecutionException | InterruptedException e){
            LOGGER.error(e.getMessage(),e);
            throw new TaskExecutorException(e.getMessage(),e);
        }finally {
            //最后注意清除记数
            count.remove();
        }

        return null;
    }

    public static void submit(Runnable task,long delay){
        SCHEDULED_EXECUTOR_SERVICE.schedule(task,delay,TimeUnit.MILLISECONDS);
    }

    public static void submit(Runnable task,long delay,long period){
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(task,delay,period,TimeUnit.MILLISECONDS);
    }

   public static void shutdown(){
        SCHEDULED_EXECUTOR_SERVICE.shutdown();
   }
}
