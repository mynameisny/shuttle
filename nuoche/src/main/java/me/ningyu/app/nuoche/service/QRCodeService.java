package me.ningyu.app.nuoche.service;

import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.nuoche.common.QRCodeUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Hashtable;

@Service
@RequiredArgsConstructor
public class QRCodeService
{
    public OutputStream generateBase64String(String content) throws Exception
    {

        QRCodeUtils.encode(content, null);
        return null;
    }

    public static BufferedImage createImage(String content) throws Exception {
        // 二维码参数设置
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        // 安全等级，最高h
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 编码设置
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        // 设置margin=0-10
        hints.put(EncodeHintType.MARGIN, 1);
        // 创建矩阵容器
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 矩阵转换图像
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    /**
     * 生成二维码
     *
     * @param content      内容
     * @param output       输出流
     * @throws Exception
     */
    public static void encode(String content, OutputStream output)
            throws Exception {
        BufferedImage image = createImage(content);
        ImageIO.write(image, "JPG", output);
    }
}
