package cn.yang.common.sequence;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Cool-Coding
 *         2018/7/25
 */
public class SimpleSequenceGenerator implements SequenceGenerator {
    /**
     * 简单的序号生成器
     */
    private final AtomicInteger sequence=new AtomicInteger();

    @Override
    public int next() {
        return sequence.getAndIncrement();
    }
}
