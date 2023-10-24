package me.ningyu.app.nuoche.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.model.CouponDto;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Rational;
import org.jcodec.scale.AWTUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService
{

    public File uploadFileToTempDirectory(MultipartFile file)
    {
        String userCode = "mynameisny"; //todo 获得当前登陆用户

        try
        {
            String originalFilename = file.getOriginalFilename();
            File tempDirectory = FileUtils.getTempDirectory();

            File userDirectory = new File(tempDirectory.getAbsolutePath() + "/" + userCode);
            if (!userDirectory.exists())
            {
                FileUtils.forceMkdir(userDirectory);
            }

            File uploadedFile = new File(userDirectory + "/" + originalFilename);

            FileUtils.copyInputStreamToFile(file.getInputStream(), uploadedFile);
            log.info("文件[{}]已被上传至：{}", originalFilename, uploadedFile);

            return uploadedFile;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


}
