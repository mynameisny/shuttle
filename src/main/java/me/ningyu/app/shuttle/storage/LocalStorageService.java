package me.ningyu.app.shuttle.storage;

import me.ningyu.app.shuttle.config.UploadProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地磁盘存储实现：压缩图片后写入配置目录，返回相对 URL。
 */
@Service
public class LocalStorageService implements StorageService
{
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/MM");

    private final UploadProperties props;

    public LocalStorageService(UploadProperties props)
    {
        this.props = props;
    }

    @Override
    public String store(MultipartFile file, String subDir)
    {
        if (file == null || file.isEmpty())
        {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (subDir == null || subDir.isBlank() || subDir.contains("..") || subDir.contains("/") || subDir.contains("\\"))
        {
            throw new IllegalArgumentException("非法存储目录");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
        {
            throw new IllegalArgumentException("仅支持图片上传");
        }
        try
        {
            byte[] bytes = compress(file);
            String dateDir = LocalDate.now().format(DATE_FMT);
            String fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
            Path target = root().resolve(subDir).resolve(dateDir).normalize();
            Files.createDirectories(target);
            Files.write(target.resolve(fileName), bytes);
            return "/uploads/" + subDir + "/" + dateDir + "/" + fileName;
        }
        catch (IOException e)
        {
            throw new RuntimeException("图片处理失败", e);
        }
    }

    @Override
    public void delete(String url)
    {
        if (url == null || !url.startsWith("/uploads/"))
        {
            return;
        }
        Path root = root();
        Path target = root.resolve(url.substring("/uploads/".length())).normalize();
        if (!target.startsWith(root))
        {
            return; // 防路径穿越
        }
        try
        {
            Files.deleteIfExists(target);
        }
        catch (IOException ignored)
        {
            // 删除失败不抛异常
        }
    }

    private Path root()
    {
        return Paths.get(props.getDir()).toAbsolutePath().normalize();
    }

    /**
     * 等比压缩（不放大），透明图转白底，输出 JPEG。
     */
    private byte[] compress(MultipartFile file) throws IOException
    {
        BufferedImage src = ImageIO.read(file.getInputStream());
        if (src == null)
        {
            throw new IllegalArgumentException("无法识别的图片格式");
        }
        int w = src.getWidth(), h = src.getHeight();
        double scale = Math.min(1.0, Math.min((double) props.getMaxWidth() / w, (double) props.getMaxHeight() / h));
        int nw = Math.max(1, (int) Math.round(w * scale));
        int nh = Math.max(1, (int) Math.round(h * scale));

        BufferedImage out = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, nw, nh);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(src, 0, 0, nw, nh, null);
        g.dispose();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(bos))
        {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed())
            {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(props.getQuality());
            }
            writer.write(null, new IIOImage(out, null, null), param);
        }
        finally
        {
            writer.dispose();
        }
        return bos.toByteArray();
    }
}
