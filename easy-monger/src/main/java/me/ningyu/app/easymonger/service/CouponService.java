package me.ningyu.app.easymonger.service;

import com.github.dozermapper.core.Mapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.domain.auth.UserRepository;
import me.ningyu.app.easymonger.domain.coupon.Account;
import me.ningyu.app.easymonger.domain.coupon.Coupon;
import me.ningyu.app.easymonger.domain.coupon.CouponRepository;
import me.ningyu.app.easymonger.domain.coupon.QCoupon;
import me.ningyu.app.easymonger.exception.DuplicateException;
import me.ningyu.app.easymonger.exception.NotFoundException;
import me.ningyu.app.easymonger.exception.UnauthorizedException;
import me.ningyu.app.easymonger.model.vo.CouponVo;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService
{
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Transactional
    public CouponVo add(CouponDto dto)
    {
        if (couponRepository.exists(QCoupon.coupon.code.eq(dto.getCode())))
        {
            throw new DuplicateException("券已存在");
        }

        Coupon entity = dtoToEntity(dto);

        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByCode(userCode).orElseThrow(() -> new NotFoundException("用户不存在"));
        List<Account> accounts = user.getAccounts();
        if (ObjectUtils.isEmpty(accounts))
        {
            throw new NotFoundException("用户暂时没有配置账号");
        }

        Account account = accounts.stream().filter(item -> item.getId().equals(dto.getAccountId())).findFirst().orElseThrow(() -> new NotFoundException("账号不存在"));
        entity.setAccount(account);
        entity.setStatus(CouponStatusEnum.ON_SALE);

        couponRepository.save(entity);

        return entityToVo(entity);
    }

    @Transactional
    public void delete(String id)
    {
        Coupon entity = getAuthorizedCoupon(id);
        couponRepository.delete(entity);
    }

    @Transactional
    public CouponVo update(String id, CouponDto dto)
    {
        Coupon entity = getAuthorizedCoupon(id);

        checkUpdateParam(dto);

        entity.setBrand(dto.getBrand());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setExpireTime(dto.getExpireTime());
        entity.setOrderNO(dto.getOrderNO());
        entity.setOrderTime(dto.getOrderTime());
        entity.setOriginalPrice(dto.getOriginalPrice());
        entity.setSellingTime(dto.getSellingTime());
        entity.setSellingPrice(dto.getSellingPrice());
        entity.setStatus(dto.getStatus());
        entity.setExpiredAutoRefund(dto.isExpiredAutoRefund());
        entity.setNoAppointment(dto.isNoAppointment());
        entity.setNoReasonRefund(dto.isNoReasonRefund());
        entity.setMoreShopSupport(dto.isMoreShopSupport());
        entity.setRemark(dto.getRemark());

        couponRepository.save(entity);

        return entityToVo(entity);
    }

    public Page<CouponVo> list(Predicate predicate, Pageable pageable)
    {
        if (predicate == null)
        {
            predicate = new BooleanBuilder();
        }

        User user = userRepository.findByCode(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new NotFoundException("用户不存在"));
        predicate = QCoupon.coupon.account.user.id.eq(user.getId()).and(predicate);

        Page<Coupon> coupons = couponRepository.findAll(predicate, pageable);
        return coupons.map(this::entityToVo);
    }

    public CouponVo get(String id)
    {
        Coupon entity = getAuthorizedCoupon(id);
        return entityToVo(entity);
    }

    private Coupon getAuthorizedCoupon(String id)
    {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new NotFoundException("券不存在"));

        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByCode(userCode).orElseThrow(() -> new NotFoundException("用户不存在"));

        if (!coupon.getAccount().getUser().getId().equals(user.getId()))
        {
            throw new UnauthorizedException("无权查看此券");
        }

        return coupon;
    }

    private CouponVo entityToVo(Coupon entity)
    {
        CouponVo vo = new CouponVo();
        mapper.map(entity, vo);
        vo.setStatusName(vo.getStatus().getName());
        vo.setStatusDescription(vo.getStatus().getDescription());
        vo.setAccountMobile(entity.getAccount().getMobile());
        vo.setAccountPlatform(entity.getAccount().getPlatform().getName());
        vo.setImageId(entity.getImage().getId());
        vo.setImagePath(entity.getImage().getPath());
        return vo;
    }

    /*public List<CouponDto> uploadImages1(List<MultipartFile> files) throws IOException
    {
        List<CouponDto> result = new ArrayList<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userCode = authentication.getName();

        for (MultipartFile file : files)
        {
            File uploadedFile = imageService.uploadFileToTempDirectory(file);

            CouponDto couponDto = new CouponDto();
            couponDto.setImagePath(uploadedFile.getAbsolutePath());
            couponDto.setAccountId(userCode);

            // OCR识别图片
            imageService.parseImage(uploadedFile, couponDto);

            BufferedImage srcImage = ImageIO.read(file.getInputStream());
            BufferedImage dstImage = srcImage.getSubimage(228, 705, 359, 287);

            File outFile = new File("/Users/ningyu/Downloads/output.png");
            ImageIO.write(dstImage, "png", outFile);


            result.add(couponDto);
        }

        return result;
    }*/

    private Coupon dtoToEntity(CouponDto dto)
    {
        Coupon entity = new Coupon();
        mapper.map(dto, entity);
        return entity;
    }

    private void checkUpdateParam(CouponDto dto)
    {
        if (dto.getStatus() == CouponStatusEnum.SOLD)
        {
            if (dto.getSellingTime() == null || StringUtils.isBlank(dto.getSellingTime().toString()))
            {
                throw new IllegalArgumentException("售出时间不能为空");
            }

            if (dto.getSellingPrice() == 0)
            {
                throw new IllegalArgumentException("售出价格不能为空");
            }
        }
    }
}
