package cn.yang.master.client.ui;

/**
 * @author Cool-Coding
 *         2018/8/2
 */
public interface IDisplayPuppet {
    /**
     * 启动窗口显示傀儡桌面
     */
    void lanuch();

    /**
     * 刷新桌面
     * @param bytes
     */
    void refresh(byte[] bytes);

    /**
     *
     * @return 傀儡名称
     */
    String getPuppetName();
}
