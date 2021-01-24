import cn.yang.common.util.ImageUtils;
import cn.yang.puppet.client.PuppetStarter;
import com.google.protobuf.ByteString;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import service.GoRobotGrpc;
import service.GoRobotOuterClass;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GoRobotReplayTest {

    public static void getImage(String[] args) {
        io.grpc.Channel goRobotChannel;
        goRobotChannel = NettyChannelBuilder.forAddress("127.0.0.1", 12345)
                .negotiationType(NegotiationType.PLAINTEXT)
                .maxInboundMetadataSize(10 * 1024 *1024)
                .build();

        //截图（截取整个屏幕图片）
        GoRobotOuterClass.CaptureScreenRequest captureScreenRequest =
                GoRobotOuterClass.CaptureScreenRequest
                        .newBuilder()
                        .setWidth(0)
                        .setHeight(0)
                        .build();
        Iterator<GoRobotOuterClass.CaptureScreenResponse> captureScreenResponseIterator = GoRobotGrpc
                .newBlockingStub(goRobotChannel)
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


        System.out.println(bitMapBits.length);
//        ImageUtils.saveImage(ImageUtils.getImageFromByteArray(bitMapBits),"E:/robot_win.png",ImageUtils.IMAGE_PNG);
//        BufferedImage bufferedImage = ImageUtils.convertByteArrayToImage();
        BufferedImage imageFromByteArray = ImageUtils.getImageFromByteArray(bitMapBits);
        byte[] bytes1 = ImageUtils.ConvertImageToByteArray(imageFromByteArray,"jpg");
        System.out.println(bytes1.length);
        byte[] bytes = ImageUtils.compressedImageAndGetByteArray(ImageUtils.getImageFromByteArray(bitMapBits), 0.8f);
        System.out.println(bytes.length);
    }

    public static void main(String[] args) {
        URL xmlpath = PuppetStarter.class.getClassLoader().getResource("");
        System.out.println(xmlpath);
    }
}
