package cn.yang.common.util;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

/**
 * Utilities methods for image manipulation. It does not support writting of GIF images, but it can read from. GIF images will be saved as PNG.
 * 图像处理工具类
 */
public class ImageUtils {
	public static final int IMAGE_JPEG = 0;
	public static final int IMAGE_PNG = 1;


	/**
	 * Resizes an image
	 * 
	 * @param imgName
	 *            The image name to resize. Must be the complet path to the file
	 * @param type
	 *            int
	 * @param maxWidth
	 *            The image's max width
	 * @param maxHeight
	 *            The image's max height
	 * @return A resized <code>BufferedImage</code>
	 */
	public static BufferedImage resizeImage(String imgName, int type, int maxWidth, int maxHeight) {
		try {
			return resizeImage(ImageIO.read(new File(imgName)), type, maxWidth, maxHeight);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Resizes an image.
	 * 
	 * @param image
	 *            The image to resize
	 * @param maxWidth
	 *            The image's max width
	 * @param maxHeight
	 *            The image's max height
	 * @return A resized <code>BufferedImage</code>
	 * @param type
	 *            int
	 */
	public static BufferedImage resizeImage(BufferedImage image, int type, int maxWidth, int maxHeight) {
		Dimension largestDimension = new Dimension(maxWidth, maxHeight);
		
		// Original size
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		
		float aspectRatio = (float) imageWidth / imageHeight;
		
		if (imageWidth > maxWidth || imageHeight > maxHeight) {
			if ((float) largestDimension.width / largestDimension.height > aspectRatio) {
				largestDimension.width = (int) Math.ceil(largestDimension.height * aspectRatio);
			} else {
				largestDimension.height = (int) Math.ceil(largestDimension.width / aspectRatio);
			}
			
			imageWidth = largestDimension.width;
			imageHeight = largestDimension.height;
		}
		
		return createHeadlessSmoothBufferedImage(image, type, imageWidth, imageHeight);
	}
	
	/**
	 * Saves an image to the disk.
	 * 
	 * @param image
	 *            The image to save
	 * @param toFileName
	 *            The filename to use
	 * @param type
	 *            The image type. Use <code>ImageUtils.IMAGE_JPEG</code> to save as JPEG images, or <code>ImageUtils.IMAGE_PNG</code> to save as PNG.
	 * @return <code>false</code> if no appropriate writer is found
	 */
	public static boolean saveImage(BufferedImage image, String toFileName, int type) {
		try {
			return ImageIO.write(image, type == IMAGE_JPEG ? "jpg" : "png", new File(toFileName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Compress and save an image to the disk. Currently this method only supports JPEG images.
	 * 
	 * @param image
	 *            The image to save
	 * @param toFileName
	 *            The filename to use
	 * @param type
	 *            The image type. Use <code>ImageUtils.IMAGE_JPEG</code> to save as JPEG images, or <code>ImageUtils.IMAGE_PNG</code> to save as PNG.
	 */
	@SuppressWarnings("unchecked")
	public static void saveCompressedImage(BufferedImage image, String toFileName, int type) {
		try {
			if (type == IMAGE_PNG) {
				throw new UnsupportedOperationException("PNG compression not implemented");
			}
			
			Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
			ImageWriter writer;
			writer = (ImageWriter) iter.next();

            ImageOutputStream ios = ImageIO.createImageOutputStream(new File(toFileName));
			writer.setOutput(ios);
			
			ImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
			
			iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwparam.setCompressionQuality(0.2F);
			
			writer.write(null, new IIOImage(image, null, null), iwparam);
			
			ios.flush();
			writer.dispose();
			ios.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    /**
     * 保持宽高比压缩图像
     * @param image
     *          图像
     * @param quality
     *          质量(0,1]
     * @return
     *          压缩后的图像
     */
    public static BufferedImage compressedImage(BufferedImage image, float quality) {
        try {
            return Thumbnails.of(image).scale(1f).outputQuality(quality).asBufferedImage();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 保持宽高比压缩图像
     * @param image
     *          图像
     * @param quality
     *          质量(0,1]
     * @return
     *          压缩后图像的字节数组
     */
	public static byte[] compressedImageAndGetByteArray(BufferedImage image, float quality) {

		try {
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			Thumbnails.of(image).scale(1f).outputFormat("jpg").outputQuality(quality).toOutputStream(byteArrayOutputStream);
			return byteArrayOutputStream.toByteArray();
		}catch (IOException e){
			throw new RuntimeException(e);
		}
	}

    /**
     * 字节数组转化为图像
     * @param data
     *          字节数组
     * @return
     *           图像
     */
	public static BufferedImage getImageFromByteArray(byte[] data){
		try {
			final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
			return Thumbnails.of(byteArrayInputStream).scale(1f).outputFormat("jpg").asBufferedImage();
		}catch (IOException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a <code>BufferedImage</code> from an <code>Image</code>. This method can function on a completely headless system. This especially includes Linux and Unix systems that do not have
	 * the X11 libraries installed, which are required for the AWT subsystem to operate. This method uses nearest neighbor approximation, so it's quite fast. Unfortunately, the result is nowhere near
	 * as nice looking as the createHeadlessSmoothBufferedImage method.
	 * 
	 * @param image
	 *            The image to convert
	 * @param width
	 *            The desired image width
	 * @param height
	 *            The desired image height
	 * @return The converted image
	 * @param type
	 *            int
	 */
	public static BufferedImage createHeadlessBufferedImage(BufferedImage image, int type, int width, int height) {
		if (type == ImageUtils.IMAGE_PNG && hasAlpha(image)) {
			type = BufferedImage.TYPE_INT_ARGB;
		} else {
			type = BufferedImage.TYPE_INT_RGB;
		}
		
		BufferedImage bi = new BufferedImage(width, height, type);
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				bi.setRGB(x, y, image.getRGB(x * image.getWidth() / width, y * image.getHeight() / height));
			}
		}
		
		return bi;
	}
	
	/**
	 * Creates a <code>BufferedImage</code> from an <code>Image</code>. This method can function on a completely headless system. This especially includes Linux and Unix systems that do not have
	 * the X11 libraries installed, which are required for the AWT subsystem to operate. The resulting image will be smoothly scaled using bilinear filtering.
	 * 
	 * @param source
	 *            The image to convert
	 * @param width
	 *            The desired image width
	 * @param height
	 *            The desired image height
	 * @return The converted image
	 * @param type
	 *            int
	 */
	public static BufferedImage createHeadlessSmoothBufferedImage(BufferedImage source, int type, int width, int height) {
		if (type == ImageUtils.IMAGE_PNG && hasAlpha(source)) {
			type = BufferedImage.TYPE_INT_ARGB;
		} else {
			type = BufferedImage.TYPE_INT_RGB;
		}
		
		BufferedImage dest = new BufferedImage(width, height, type);
		
		int sourcex;
		int sourcey;
		
		double scalex = (double) width / source.getWidth();
		double scaley = (double) height / source.getHeight();
		
		int x1;
		int y1;
		
		double xdiff;
		double ydiff;
		
		int rgb;
		int rgb1;
		int rgb2;
		
		for (int y = 0; y < height; y++) {
			sourcey = y * source.getHeight() / dest.getHeight();
			ydiff = scale(y, scaley) - sourcey;
			
			for (int x = 0; x < width; x++) {
				sourcex = x * source.getWidth() / dest.getWidth();
				xdiff = scale(x, scalex) - sourcex;
				
				x1 = Math.min(source.getWidth() - 1, sourcex + 1);
				y1 = Math.min(source.getHeight() - 1, sourcey + 1);
				
				rgb1 = getRGBInterpolation(source.getRGB(sourcex, sourcey), source.getRGB(x1, sourcey), xdiff);
				rgb2 = getRGBInterpolation(source.getRGB(sourcex, y1), source.getRGB(x1, y1), xdiff);
				
				rgb = getRGBInterpolation(rgb1, rgb2, ydiff);
				
				dest.setRGB(x, y, rgb);
			}
		}
		
		return dest;
	}
	
	private static double scale(int point, double scale) {
		return point / scale;
	}
	
	private static int getRGBInterpolation(int value1, int value2, double distance) {
		int alpha1 = (value1 & 0xFF000000) >>> 24;
		int red1 = (value1 & 0x00FF0000) >> 16;
		int green1 = (value1 & 0x0000FF00) >> 8;
		int blue1 = (value1 & 0x000000FF);
		
		int alpha2 = (value2 & 0xFF000000) >>> 24;
		int red2 = (value2 & 0x00FF0000) >> 16;
		int green2 = (value2 & 0x0000FF00) >> 8;
		int blue2 = (value2 & 0x000000FF);
		
		int rgb = ((int) (alpha1 * (1.0 - distance) + alpha2 * distance) << 24) | ((int) (red1 * (1.0 - distance) + red2 * distance) << 16)
				| ((int) (green1 * (1.0 - distance) + green2 * distance) << 8) | (int) (blue1 * (1.0 - distance) + blue2 * distance);
		
		return rgb;
	}
	
	/**
	 * Determines if the image has transparent pixels.
	 * 
	 * @param image
	 *            The image to check for transparent pixel.s
	 * @return <code>true</code> of <code>false</code>, according to the result
	 */
	public static boolean hasAlpha(Image image) {
		try {
			PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
			pg.grabPixels();
			
			return pg.getColorModel().hasAlpha();
		} catch (InterruptedException e) {
			return false;
		}
	}

    /**
     * 图像转化成字节数组
     * @param image
     * @param format
     * @return
     */
	public static byte[] ConvertImageToByteArray(BufferedImage image, String format){
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, format, byteArrayOutputStream);
			return byteArrayOutputStream.toByteArray();
		}catch (IOException e){
			throw new RuntimeException(e);
		}
	}


    /**
     * 字节数组转换成BufferedImage
     * @param bytes
     * @return
     */
	public static BufferedImage convertByteArrayToImage(byte[] bytes){
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		try {
			return ImageIO.read(byteArrayInputStream);
		}catch (IOException e){
			throw new RuntimeException(e);
		}
	}
}
