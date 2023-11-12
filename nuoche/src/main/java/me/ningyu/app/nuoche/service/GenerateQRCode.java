package me.ningyu.app.nuoche.service;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * 一个简单的二维码生成
 *
 * @author Anhui OuYang
 * @version 1.0
 **/
public class GenerateQRCode {

    private static final Integer CODE_WIDTH = 300;                 // 基础属性：二维码宽度，单位像素
    private static final Integer CODE_HEIGHT = 300;                // 基础属性：二维码高度，单位像素
    private static final Integer FRONT_COLOR = 0x000000;           // 基础属性：二维码前景色，0x000000 表示黑色
    private static final Integer BACKGROUND_COLOR = 0xFFFFFF;      // 基础属性：二维码背景色，0xFFFFFF 表示白色

    public static void main(String[] args) throws WriterException, IOException {

        //获取当前项目的路径根目录
        String path = Objects.requireNonNull(GenerateQRCode.class.getResource("/")).getPath();
        //生成文件名及文件的路径
        File filePathAndName = new File(path, new Date().getTime() + ".png");
        //设置生成二维码的内容
        String qrMessage = "https://www.baidu.com";

        // EncodeHintType：编码提示类型,枚举类型
        //    EncodeHintType.CHARACTER_SET：设置字符编码类型
        //    EncodeHintType.ERROR_CORRECTION：设置误差校正
        //    ErrorCorrectionLevel：误差校正等级，
        //          L = ~7% correction、M = ~15% correction、
        //          Q = ~25% correction、H = ~30% correction
        //      不设置时，默认为 L 等级，等级不一样，生成的图案不同，但扫描的结果是一样的
        //    EncodeHintType.MARGIN：设置二维码边距，单位像素，值越小，二维码距离四周越近
        HashMap<EncodeHintType, Object> typeObjectHashMap = new HashMap<>();
        typeObjectHashMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        typeObjectHashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        typeObjectHashMap.put(EncodeHintType.MARGIN, 1);

        // MultiFormatWriter:多格式写入，这是一个工厂类，里面重载了两个 encode 方法，用于写入条形码或二维码
        // encode(String contents,BarcodeFormat format,int width, int height,Map<EncodeHintType,?> hints)
        //      参数1：contents:条形码/二维码内容
        //      参数2：format：编码类型，如 条形码，二维码 等
        //      参数3：width：码的宽度
        //      参数4：height：码的高度
        //      参数5：hints：码内容的编码类型
        // BarcodeFormat：枚举该程序包已知的条形码格式，即创建何种码，如 1 维的条形码，2 维的二维码 等
        // BitMatrix：位(比特)矩阵或叫2D矩阵，也就是需要的二维码
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter
                .encode(qrMessage, BarcodeFormat.QR_CODE, CODE_WIDTH, CODE_HEIGHT, typeObjectHashMap);

        // java.awt.image.BufferedImage：具有图像数据的可访问缓冲图像，实现了 RenderedImage 接口
        // BitMatrix 的 get(int x, int y) 获取比特矩阵内容，指定位置有值，则返回true，将其设置为前景色，否则设置为背景色
        // BufferedImage 的 setRGB(int x, int y, int rgb) 方法设置图像像素
        //      参数1 x：像素位置的横坐标，即列
        //      参数2 y：像素位置的纵坐标，即行
        //      参数3 rgb：像素的值，采用 16 进制,如 0xFFFFFF 白色
        BufferedImage bufferedImage = new BufferedImage(CODE_WIDTH, CODE_HEIGHT, BufferedImage.TYPE_INT_BGR);
        for (int x = 0; x < CODE_WIDTH; x++) {
            for (int y = 0; y < CODE_HEIGHT; y++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? FRONT_COLOR : BACKGROUND_COLOR);
            }
        }

        // 把logo图片设置到二维码中间位置(若不想在二维码中间插入logo图片则不执行此条代码)
        insertLogoImage(bufferedImage, GenerateQRCode.class.getResourceAsStream("/logo.png"));

        // javax.imageio.ImageIO java扩展的图像IO
        // write(RenderedImage im,String formatName,File output)
        //      im：待写入的图像
        //      formatName：图像写入的格式
        //      output：写入的图像文件，文件不存在时会自动创建
        // 即将保存的二维码图片文件
        ImageIO.write(bufferedImage, "png", filePathAndName);
        System.out.println("当前写入图片在：" + filePathAndName.getPath());
    }

    /***
     * 为二维码插入中间的logo图片
     * @param bufferedImage 二维码图片信息
     * @param logoInput logo图片位置
     * @throws IOException  抛出IO异常
     */
    private static void insertLogoImage(BufferedImage bufferedImage, InputStream logoInput) throws IOException {
        // 加载Logo图片到Image对象
        Image logoImage = ImageIO.read(logoInput);
        // 设置二维码中间的Logo图片(设置logo图片的宽高需要保持和二维码宽高的一半宽高)
        int logoWidth = CODE_WIDTH / 5;
        int logoHeight = CODE_HEIGHT / 5;
        // 按照指定的宽高进行等比缩放(若设置的宽高比之前大则代表放大)，并将缩放后的图片绘制到一个新的BufferedImage对象中
        Image image = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
        BufferedImage logoBufferedImage = new BufferedImage(logoWidth, logoHeight, BufferedImage.TYPE_INT_RGB);
        // 获取图形上下文对象，用于后续将缩放后的图片绘制在 logoBufferedImage 对象上
        Graphics graphics = logoBufferedImage.getGraphics();
        // 绘制缩放后的图片到 logoBufferedImage 对象上，使其填满整个画布
        graphics.drawImage(image, 0, 0, null);
        // 释放图形上下文对象，避免内存泄漏
        graphics.dispose();
        // 把处理好的logo图片再次设置给之前的logo图片对象
        logoImage = image;

        // 开始把logo图片插入到二维码的中间位置
        // 获取 Graphics2D 对象，用于后续在 bufferedImage 上绘制二维码和 logo 图片
        Graphics2D graph = bufferedImage.createGraphics();
        // 计算出 logo 图片在二维码中间的坐标点 (x, y)，以便后续将 logo 图片插入到正确的位置。
        int x = (CODE_WIDTH - logoWidth) / 2;
        int y = (CODE_HEIGHT - logoHeight) / 2;
        // 绘制缩放后的 logo 图片到二维码中间位置
        graph.drawImage(logoImage, x, y, logoWidth, logoHeight, null);
        // 设置边框的线条粗细为 3 像素，并且设置线条是一个圆角矩形，6表示圆角的半径。并关闭资源
        graph.setStroke(new BasicStroke(3f));
        graph.draw(new RoundRectangle2D.Float(x, y, logoWidth, logoHeight, 6, 6));
        graph.dispose();
    }
}
