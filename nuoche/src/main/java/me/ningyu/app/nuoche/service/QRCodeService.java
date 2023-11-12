package me.ningyu.app.nuoche.service;

import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.nuoche.common.QRCodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.ByteBuffer;
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

    public static void createQR(String data, String path, String charset, Map hashMap, int height, int width) throws WriterException, IOException
    {

        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(data.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, width, height);

        MatrixToImageWriter.writeToFile(
                matrix,
                path.substring(path.lastIndexOf('.') + 1),
                new File(path));
    }

    /**
     * 生成二维码
     *
     * @param content 内容
     * @param output  输出流
     * @throws Exception
     */
    public static void encode(String content, OutputStream output) throws Exception
    {
        content = StringUtils.normalizeSpace(content);
        BufferedImage image = createImage(content);
        ImageIO.write(image, "JPG", output);

        BufferedImage dstImage = image.getSubimage(228, 705, 359, 287);

        File outFile = new File("/Users/xxx/Downloads/output.png");
        ImageIO.write(dstImage, "png", outFile);
    }

    public static ByteBuffer getSubImage(ByteBuffer imageContent, int x, int y, int width, int height) throws    Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(imageContent.array());
        BufferedImage image = ImageIO.read(in);

        BufferedImage subImage = image.getSubimage(x, y, width, height);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(subImage, "jpeg", out);

        return ByteBuffer.wrap(out.toByteArray());
    }
}
