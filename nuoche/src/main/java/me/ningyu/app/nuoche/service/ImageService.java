package me.ningyu.app.nuoche.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService
{

    private final ITesseract tess4jInstance;


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
            log.error("上传文件失败", e);
            throw new RuntimeException(e);
        }
    }

    public void parseImage(File imageFile, Rectangle rectangle)
    {
        if (rectangle == null)
        {
             rectangle = new Rectangle(78, 373, 400, 190);
        }

        try
        {
            String result = tess4jInstance.doOCR(imageFile);
            log.info("全图解析结果：{}", result);


            String code = tess4jInstance.doOCR(imageFile, rectangle);
            log.info("解析结果：{}", code);

            code = StringUtils.deleteWhitespace(code);
        }
        catch (TesseractException e)
        {
            log.error("无法解析图片：{}", imageFile, e);
            throw new RuntimeException(e);
        }
    }

}
