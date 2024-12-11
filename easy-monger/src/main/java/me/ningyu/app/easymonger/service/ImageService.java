package me.ningyu.app.easymonger.service;

import com.github.dozermapper.core.Mapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.domain.auth.UserRepository;
import me.ningyu.app.easymonger.domain.coupon.*;
import me.ningyu.app.easymonger.domain.coupon.Image;
import me.ningyu.app.easymonger.exception.NotFoundException;
import me.ningyu.app.easymonger.exception.UnauthorizedException;
import me.ningyu.app.easymonger.model.enums.Field;
import me.ningyu.app.easymonger.model.vo.CouponVo;
import me.ningyu.app.easymonger.model.vo.ImageVo;
import net.sourceforge.tess4j.ITesseract;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Rational;
import org.jcodec.scale.AWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService
{
    public static final String VIDEO_TEMPLATE = "easy-monger";

    private final CouponRepository couponRepository;
    private List<String> DELETE_REGEX_DEFAULT = Collections.singletonList("\\s");

    private final ImageRepository imageRepository;

    private final UserRepository userRepository;

    private final BrandRepository brandRepository;

    private final AccountRepository accountRepository;

    private final ResourceLoader resourceLoader;

    @Value("${custom.file.upload.base-path}")
    private String uploadFileBasePath;

    private final ITesseract tess4jInstance;

    private final Mapper mapper;


    @Transactional
    public List<String> uploadImages(List<MultipartFile> files, String accountId) throws IOException
    {
        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByCode(userCode).orElseThrow(() -> new NotFoundException("用户不存在"));
        Account account = null;
        if (StringUtils.isNotBlank(accountId))
        {
            account = accountRepository.findById(accountId).orElse(null);
        }

        File userDirectory = new File(uploadFileBasePath + File.separator + userCode + File.separator + LocalDate.now(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ISO_DATE));
        FileUtils.forceMkdirParent(userDirectory);

        List<Image> entities = new ArrayList<>();
        for (MultipartFile file : files)
        {
            File dest = new File(userDirectory + File.separator + file.getOriginalFilename());
            FileUtils.copyInputStreamToFile(file.getInputStream(), dest);

            String checksum = Sha512DigestUtils.shaHex(file.getBytes());

            if (imageRepository.exists(QImage.image.checksum.eq(checksum)))
            {
                log.warn("文件[{}]已经上传过，Sha512校验和为：{}", file.getOriginalFilename(), checksum);
            }
            else
            {
                entities.add(Image.builder().name(dest.getName()).path(dest.getPath()).user(user).account(account).checksum(checksum).build());
            }
        }

        imageRepository.saveAll(entities);

        return entities.stream().map(Image::getId).collect(Collectors.toList());
    }

    @Transactional
    public void delete(String id)
    {
        Image image = getAuthorizedImage(id);

        imageRepository.deleteById(id);
        FileUtils.deleteQuietly(new File(image.getPath()));
    }

    public Page<ImageVo> list(Predicate predicate, Pageable pageable)
    {
        if (predicate == null)
        {
            predicate = new BooleanBuilder();
        }

        User user = userRepository.findByCode(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new NotFoundException("用户不存在"));
        predicate = QImage.image.user.id.eq(user.getId()).and(predicate);

        return imageRepository.findAll(predicate, pageable).map(image -> ImageVo.builder()
                .id(image.getId())
                .name(image.getName())
                .remark(image.getRemark())
                .build());
    }

    public void get(String id, HttpServletResponse response)
    {
        Image image = getAuthorizedImage(id);

        File file = new File(image.getPath());
        try (FileInputStream fis = new FileInputStream(file))
        {
            String mimeType = URLConnection.getFileNameMap().getContentTypeFor(image.getName());
            response.setContentType(mimeType);
            fis.getChannel().transferTo(0, fis.available(), Channels.newChannel(response.getOutputStream()));
        }
        catch (Exception e)
        {
            log.error("查看图片出错", e);
        }
    }

    @Transactional
    public List<CouponVo> convertToCoupon(List<String> ids)
    {
        List<Image> images = new ArrayList<>();
        List<Coupon> coupons = new ArrayList<>();
        List<String> brandNames = brandRepository.findAll().stream().map(Brand::getName).collect(Collectors.toList());

        for (String id : ids)
        {
            Image image = getAuthorizedImage(id);
            image.setInUsed(true);
            images.add(image);

            ParseTemplate parseTemplate = getParseTemplate(image);
            if (parseTemplate == null)
            {
                log.warn("未找到图片[{}]的解析模板，无法导入图片", image.getId());
                continue;
            }

            File file = new File(image.getPath());
            if (!file.exists())
            {
                log.warn("找不到文件：{}", file.getPath());
            }

            Coupon coupon = buildCoupon(file, parseTemplate, brandNames);
            if (coupon != null && StringUtils.isNoneBlank(coupon.getCode(), coupon.getName()))
            {
                coupon.setAccount(image.getAccount());
                coupon.setImage(image);
                coupons.add(coupon);
            }
        }

        if (ObjectUtils.isNotEmpty(images))
        {
            imageRepository.saveAll(images);
        }

        if (ObjectUtils.isNotEmpty(coupons))
        {
            couponRepository.saveAll(coupons);
        }

        return coupons.stream().map(this::entityToVo).collect(Collectors.toList());
    }

    @Transactional
    public Resource convertToVideo(String id)
    {
        try
        {
            Image image = getAuthorizedImage(id);
            File imageFile = new File(image.getPath());
            String userCode = SecurityContextHolder.getContext().getAuthentication().getName();

            File tempDirectory = FileUtils.getTempDirectory();
            File userDirectory = new File(tempDirectory.getParent() + File.separator + VIDEO_TEMPLATE + File.separator + userCode);
            File outputFile = new File(userDirectory + File.separator + image.getId() + ".mp4");
            if (!outputFile.exists())
            {
                FileUtils.forceMkdirParent(outputFile);
            }
            else
            {
                log.info("文件[{}]已经存在，不再重新生成", outputFile);
                return new UrlResource(Paths.get(outputFile.getAbsolutePath()).toUri());
            }

            log.info("准备生成视频文件：{}", outputFile.getPath());

            // 设置1秒钟有1帧图片
            SequenceEncoder enc = SequenceEncoder.createWithFps(NIOUtils.writableChannel(outputFile), new Rational(1, 1));

            BufferedImage in = ImageIO.read(imageFile);

            // 重新设置图片的宽度和高度为偶数，否则用图片生成视频帧的时候会报错：Component 1 width should be a multiple of 2 for colorspace: YUV420J
            int width = in.getWidth();
            int height = in.getHeight();
            BufferedImage bufferedImage = new BufferedImage(nearestEvenInt(width), nearestEvenInt(height), BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(in, 0, 0, null);
            g.dispose();

            // 生成2秒钟的视频封面
            generateCover(width, height, enc);

            // 生成3秒钟的视频
            for (int i = 0; i < 3; i++)
            {
                enc.encodeNativeFrame(AWTUtil.fromBufferedImage(bufferedImage, ColorSpace.RGB));
            }
            enc.finish();

            Path path = Paths.get(outputFile.getAbsolutePath());

            Resource resource = new UrlResource(path.toUri());
            log.debug("生成的视频文件信息：{}", resource);

            log.info("视频文件[{}]生成完毕", outputFile.getPath());

            return resource;
        }
        catch (IOException e)
        {
            throw new RuntimeException("生成视频失败", e);
        }
    }

    /**
     * 生成视频封面
     * 视频的前2帧显示为特定的图片，防止被闲鱼识别出是发送的券码，提示非法
     * @param width     视频中实际的宽度，即券码截图的宽度
     * @param height    视频中实际的高度，即券码截图的高度
     * @param enc       jcodec的视频编码器
     * @throws IOException
     */
    private void generateCover(int width, int height, SequenceEncoder enc) throws IOException
    {
        Resource resource = new ClassPathResource("static/assets/image/video-cover-minion.jpg");
        BufferedImage cover = ImageIO.read(resource.getFile());

        BufferedImage coverImage = new BufferedImage(nearestEvenInt(width), nearestEvenInt(height), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = coverImage.createGraphics();
        g.setBackground(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setBackground(Color.BLACK);
        g.drawImage(ImageIO.read(resource.getFile()), (width - cover.getWidth()) / 2, (height - cover.getHeight()) / 2, null);
        g.dispose();

        // 生成2秒钟的视频
        for (int i = 0; i < 2; i++)
        {
            enc.encodeNativeFrame(AWTUtil.fromBufferedImage(coverImage, ColorSpace.RGB));
        }
    }

    @Scheduled(fixedDelay = 8, timeUnit = TimeUnit.HOURS)
    public void scheduleCleanImages() throws IOException
    {
        log.info("定时清理图片开始");

        File tempDirectory = FileUtils.getTempDirectory();
        File videoDirectory = new File(tempDirectory.getParent() + File.separator + VIDEO_TEMPLATE);

        if (videoDirectory.exists())
        {
            FileUtils.cleanDirectory(videoDirectory);
        }

        log.info("定时清理图片完成");
    }

    private ParseTemplate getParseTemplate(Image image)
    {
        Account account = image.getAccount();

        if (account != null)
        {
            Optional<ParseTemplate> templateOptional = account.getParseTemplates().stream().filter(ParseTemplate::isEnabled).findAny();
            if (templateOptional.isPresent())
            {
                return templateOptional.get();
            }
        }

        return null;
    }

    public Coupon buildCoupon(File imageFile, ParseTemplate parseTemplate, List<String> brandNames)
    {
        try
        {
            List<ParseTemplateCoordinate> coordinates = parseTemplate.getCoordinates();
            Map<Field, ParseTemplateCoordinate> coordinateMap = coordinates.stream().collect(Collectors.toMap(ParseTemplateCoordinate::getField, coordinate -> coordinate));

            if (log.isTraceEnabled())
            {
                log.trace("全图解析结果：{}", tess4jInstance.doOCR(imageFile));
            }

            Coupon result = new Coupon();
            result.setCode(parseImage(imageFile, Field.CODE, coordinateMap));
            result.setName(parseImage(imageFile, Field.NAME, coordinateMap));
            result.setDescription(parseImage(imageFile, Field.DESCRIPTION, coordinateMap));

            String statusStr = parseImage(imageFile, Field.STATUS, coordinateMap);
            if (StringUtils.isNotBlank(statusStr))
            {
                if ("待到店使用".equals(statusStr))
                {
                    result.setStatus(CouponStatusEnum.ON_SALE);
                }
                else
                {
                    result.setStatus(CouponStatusEnum.UNKNOWN);
                }
            }

            // 价格
            String originalPriceStr = parseImage(imageFile, Field.ORIGINAL_PRICE, coordinateMap);
            if (StringUtils.isNotBlank(originalPriceStr))
            {
                result.setOriginalPrice(Float.parseFloat(originalPriceStr));
            }

            // 多家门店可用
            String moreShopStr = parseImage(imageFile, Field.MORE_SHOP, coordinateMap);
            if (StringUtils.isNotBlank(moreShopStr))
            {
                if (moreShopStr.contains("更多适用门店"))
                {
                    result.setMoreShopSupport(true);
                }
            }

            // 到期时间
            String expireStr = parseImage(imageFile, Field.EXPIRE, coordinateMap);
            if (StringUtils.isNotBlank(expireStr))
            {
                result.setExpireTime(null);
            }

            // 品牌
            if (StringUtils.isNotBlank(result.getName()))
            {
                String brand = guessBrand(result.getName(), brandNames);
                result.setBrand(brand);
            }

            String tagStr = parseImage(imageFile, Field.TAGS, coordinateMap);
            if (StringUtils.isNotBlank(tagStr))
            {
                result.setTags(tagStr);
            }

            return result;
        }
        catch (Exception e)
        {
            log.error("无法解析图片：{}", imageFile, e);
            return null;
        }
    }

    private String parseImage(File imageFile, Field field, Map<Field, ParseTemplateCoordinate> coordinateMap) throws TesseractException
    {
        ParseTemplateCoordinate coordinate = coordinateMap.getOrDefault(field, null);
        if (coordinate != null)
        {
            Rectangle rectangle = new Rectangle(coordinate.getX(), coordinate.getY(), coordinate.getWidth(), coordinate.getHeight());

            String parsed = tess4jInstance.doOCR(imageFile, rectangle);
            log.info("从图片[{}]中解析出[{}]字段的原始值为：{}", imageFile.getPath(), field.name(), parsed);

            parsed = parsed.replaceAll("[\\r\\n\\t]", "");

            if (coordinate.isDeleteSpace())
            {
                parsed = StringUtils.deleteWhitespace(parsed);
            }

            parsed = StringUtils.deleteWhitespace(parsed);
            log.info("从图片[{}]中解析出[{}]字段处理后为：{}", imageFile.getPath(), field.name(), parsed);

            return parsed;
        }

        return null;
    }

    private Image getAuthorizedImage(String id)
    {
        Image image = imageRepository.findById(id).orElseThrow(() -> new NotFoundException("图片不存在"));

        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByCode(userCode).orElseThrow(() -> new NotFoundException("用户不存在"));

        if (!image.getUser().getId().equals(user.getId()))
        {
            throw new UnauthorizedException("无权查看此图片");
        }

        return image;
    }

    private CouponVo entityToVo(Coupon entity)
    {
        CouponVo vo = new CouponVo();
        mapper.map(entity, vo);
        return vo;
    }

    private String guessBrand(String name, List<String> brandNames)
    {
        for (String brandName : brandNames)
        {
            if (name.contains(brandName))
            {
                return brandName;
            }
        }

        return null;
    }

    int nearestEvenInt(int to)
    {
        return (to % 2 == 0) ? to : (to + 1);
    }



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

    /*public File imageToVideo(File file)
    {
        String userCode = "mynameisny"; //todo 获得当前登陆用户

        try
        {
            File output = null;
            File tempDirectory = FileUtils.getTempDirectory();

            File userDirectory = new File(tempDirectory.getAbsolutePath() + "/" + userCode);
            if (!userDirectory.exists())
            {
                FileUtils.forceMkdir(userDirectory);
                output = new File(file.getName() + ".mp4");
            }

            // 设置1秒钟有多少帧图片
            if (output != null)
            {
                SequenceEncoder enc = SequenceEncoder.createWithFps(NIOUtils.writableChannel(output), new Rational(1, 1));

                BufferedImage bufferedImage = ImageIO.read(file);

                // 生成几秒钟的视频
                for (int i = 0; i < 5; i++)
                {
                    enc.encodeNativeFrame(AWTUtil.fromBufferedImage(bufferedImage, ColorSpace.RGB));
                }
                enc.finish();

                return output;
            }
        }
        catch (IOException e)
        {
            log.error("图片生成视频失败", e);
        }

        return null;
    }*/



    /*public void parseImage1(File imageFile, ParseTemplate dto)
    {
        try
        {
            List<OcrCoordinate> coordinates = new ArrayList<>();
            coordinates.add(OcrCoordinate.builder().field("status").position("91,299,300,60").deleteRegex(DELETE_REGEX_DEFAULT).build());
            coordinates.add(OcrCoordinate.builder().field("name").position("237,459,563,54").deleteRegex(DELETE_REGEX_DEFAULT).build());
            coordinates.add(OcrCoordinate.builder().field("description").position("52,1244,578,48").deleteRegex(DELETE_REGEX_DEFAULT).build());
            coordinates.add(OcrCoordinate.builder().field("code").position("76,1141,218,50").deleteRegex(DELETE_REGEX_DEFAULT).build());
            coordinates.add(OcrCoordinate.builder().field("expire").position("51,1092,218,40").build());
            coordinates.add(OcrCoordinate.builder().field("originalPrice").position("243,595,110,45").build());
            coordinates.add(OcrCoordinate.builder().field("moreShop").position("316,1428,178,43").deleteRegex(DELETE_REGEX_DEFAULT).build());
            coordinates.add(OcrCoordinate.builder().field("tags").position("223,556,390,50").deleteRegex(DELETE_REGEX_DEFAULT).build());

            String result = tess4jInstance.doOCR(imageFile);
            log.trace("全图解析结果：{}", result);

            for (OcrCoordinate coordinate : coordinates)
            {
                String field = coordinate.getField();
                String position = StringUtils.deleteWhitespace(coordinate.getPosition());

                String[] positionArray = position.split(",");
                int x = Integer.parseInt(positionArray[0]);
                int y = Integer.parseInt(positionArray[1]);
                int w = Integer.parseInt(positionArray[2]);
                int h = Integer.parseInt(positionArray[3]);
                Rectangle rectangle = new Rectangle(x, y, w, h);

                String parsed = tess4jInstance.doOCR(imageFile, rectangle);
                parsed = parsed.replaceAll("[\\r\\n\\t]", "");

                List<String> deleteRegexList = coordinate.getDeleteRegex();
                if (ObjectUtils.isNotEmpty(deleteRegexList))
                {
                    for (String regex : deleteRegexList)
                    {
                        parsed = parsed.replaceAll(regex, "");
                    }
                }

                log.info("字段{}的解析结果：{}", field, parsed);

                parsed = StringUtils.deleteWhitespace(parsed);
            }
        }
        catch (TesseractException e)
        {
            log.error("无法解析图片：{}", imageFile, e);
            throw new RuntimeException(e);
        }
    }*/
}
