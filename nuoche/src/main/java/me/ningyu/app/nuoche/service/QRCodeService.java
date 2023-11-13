package me.ningyu.app.nuoche.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.nuoche.common.QRCodeUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

@Service
@RequiredArgsConstructor
public class QRCodeService
{
    public OutputStream generateBase64String(String content) throws Exception
    {

        QRCodeUtils.encode(content, null);
        return null;
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
