package cn.yang.puppet.client.robot;


import cn.yang.common.InputEvent.MasterKeyEvent;
import cn.yang.common.InputEvent.MasterMouseEvent;
import cn.yang.common.util.BeanUtil;
import cn.yang.common.util.ImageUtils;
import cn.yang.puppet.client.PuppetStarter;
import cn.yang.puppet.client.constant.PuppetDynamicSetting;
import cn.yang.puppet.client.ui.IReplay;
import com.google.protobuf.ByteString;
import service.GoRobotGrpc;
import service.GoRobotOuterClass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Cool-Coding
 * 2021/1/23
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class GoRobotReplay implements IReplay {

    /**
     * 因go与java的键盘对照不一致,直接使用java的robot操作按键
     */
    protected static final IReplay defaultRobot = BeanUtil.getBean("javaRobot");


    @Override
    public void keyPress(MasterKeyEvent keyEvent) {
        defaultRobot.keyPress(keyEvent);
    }

    @Override
    public void keyRelease(MasterKeyEvent keyEvent) {
        defaultRobot.keyRelease(keyEvent);
    }

    @Override
    public void mouseClick(MasterMouseEvent mouseEvent) {
        GoRobotOuterClass.MouseButton mouseButton = GoRobotOuterClass.MouseButton.newBuilder().setX(mouseEvent.getMouseButton()).build();
        GoRobotGrpc.newBlockingStub(PuppetStarter.goRobotChannel).mouseClick(mouseButton);
    }


    @Override
    public void mouseWheel(MasterMouseEvent mouseEvent) {
        int mouseWheel = mouseEvent.getMouseWheel();
        if (mouseWheel < 0) {
            GoRobotOuterClass.MouseScrolledRequest mouseScrolledRequest = GoRobotOuterClass.MouseScrolledRequest.newBuilder().setDistance(mouseWheel).build();
            GoRobotGrpc.newBlockingStub(PuppetStarter.goRobotChannel).mouseScrolledUp(mouseScrolledRequest);
        } else {
            GoRobotOuterClass.MouseScrolledRequest mouseScrolledRequest = GoRobotOuterClass.MouseScrolledRequest.newBuilder().setDistance(mouseWheel * -1).build();
            GoRobotGrpc.newBlockingStub(PuppetStarter.goRobotChannel).mouseScrolledDown(mouseScrolledRequest);
        }
    }

    @Override
    public void mousePress(MasterMouseEvent mouseEvent) {
        GoRobotOuterClass.MouseButton mouseButton = GoRobotOuterClass.MouseButton.newBuilder().setX(mouseEvent.getMouseButton()).build();
        GoRobotGrpc.newBlockingStub(PuppetStarter.goRobotChannel).mousePressed(mouseButton);
    }

    @Override
    public void mouseRelease(MasterMouseEvent mouseEvent) {
        GoRobotOuterClass.MouseButton mouseButton = GoRobotOuterClass.MouseButton.newBuilder().setX(mouseEvent.getMouseButton()).build();
        GoRobotGrpc.newBlockingStub(PuppetStarter.goRobotChannel).mouseReleased(mouseButton);
    }

    @Override
    public void mouseMove(int[] site) {
        GoRobotOuterClass.Point endPoint = GoRobotOuterClass.Point.newBuilder().setX(site[0]).setY(site[1]).build();
        GoRobotGrpc.newBlockingStub(PuppetStarter.goRobotChannel).mouseMove(endPoint);
    }

    @Override
    public void mouseDoubleClick(MasterMouseEvent mouseEvent) {
        final int mouseButton1 = mouseEvent.getMouseButton();
        GoRobotOuterClass.MouseButton mouseButton = GoRobotOuterClass.MouseButton.newBuilder().setX(mouseButton1).build();
        GoRobotGrpc.newBlockingStub(PuppetStarter.goRobotChannel).mouseDoubleClick(mouseButton);
    }

    @Override
    public void mouseDragged(MasterMouseEvent mouseEvent, int[] site) {
        final int mouseButton1 = mouseEvent.getMouseButton();

        GoRobotOuterClass.MouseButton mouseButton = GoRobotOuterClass.MouseButton.newBuilder().setX(mouseButton1).build();
        GoRobotOuterClass.Point endPoint = GoRobotOuterClass.Point.newBuilder().setX(site[0]).setY(site[1]).build();
        GoRobotGrpc.newBlockingStub(PuppetStarter.goRobotChannel).mousePressed(mouseButton);
        GoRobotGrpc.newBlockingStub(PuppetStarter.goRobotChannel).mouseMove(endPoint);
        GoRobotGrpc.newBlockingStub(PuppetStarter.goRobotChannel).mouseReleased(mouseButton);
    }

    @Override
    public byte[] getScreenSnapshot() {
        //截图（截取整个屏幕图片）
        GoRobotOuterClass.CaptureScreenRequest captureScreenRequest =
                GoRobotOuterClass.CaptureScreenRequest
                .newBuilder()
                .setWidth(0)
                .setHeight(0)
                .build();
        Iterator<GoRobotOuterClass.CaptureScreenResponse> captureScreenResponseIterator = GoRobotGrpc
                .newBlockingStub(PuppetStarter.goRobotChannel)
                .captureScreen(captureScreenRequest);

        List<Byte> byteList = new ArrayList<>();
        while (captureScreenResponseIterator.hasNext()) {
            ByteString bitmap = captureScreenResponseIterator.next().getBitmap();
            byte[] bytes = bitmap.toByteArray();
            for (byte b : bytes) {
                byteList.add(b);
            }
        }

        byte[] bitMapBits = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            bitMapBits[i] = byteList.get(i);
        }
        return ImageUtils.compressedImageAndGetByteArray(ImageUtils.getImageFromByteArray(bitMapBits), PuppetDynamicSetting.quality / 100.0f);
    }
}