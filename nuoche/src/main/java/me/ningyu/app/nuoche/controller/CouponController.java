package me.ningyu.app.nuoche.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.StringExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.Coupon;
import me.ningyu.app.easymonger.domain.QCoupon;
import me.ningyu.app.easymonger.model.CouponDto;
import me.ningyu.app.easymonger.model.CouponVo;
import me.ningyu.app.easymonger.service.CouponService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@Slf4j
public class CouponController
{
    private final CouponService couponService;


    @PostMapping
    public ResponseEntity<CouponVo> add(@Validated @RequestBody CouponDto dto)
    {
        CouponVo vo = couponService.add(dto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", UriComponentsBuilder.fromUriString("/coupons/{couponId}").buildAndExpand(vo.getId()).toUriString());

        return new ResponseEntity<>(vo, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id)
    {
        couponService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CouponVo> update(@PathVariable String id, @RequestBody CouponDto dto)
    {
        CouponVo vo = couponService.update(id, dto);
        return ResponseEntity.ok(vo);
    }

    @GetMapping
    public Page<CouponVo> list(@QuerydslPredicate(root = Coupon.class, bindings = CouponSearchBinding.class) Predicate predicate, @PageableDefault(size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "modifiedDate", direction = Sort.Direction.DESC), @SortDefault(sort = "id", direction = Sort.Direction.ASC)}) Pageable pageable)
    {
        return couponService.list(predicate, pageable);
    }

    @GetMapping("/{id}")
    public CouponVo get(@PathVariable String id)
    {
        return couponService.get(id);
    }

    @PostMapping("/images")
    public ResponseEntity<List<CouponDto>> uploadImages(@RequestPart(name = "file") List<MultipartFile> files)
    {
        List<CouponDto> list = couponService.uploadImages(files);

        return new ResponseEntity<>(list, HttpStatus.CREATED);
    }

    private static class CouponSearchBinding implements QuerydslBinderCustomizer<QCoupon>
    {
        @Override
        public void customize(QuerydslBindings bindings, QCoupon root)
        {
            bindings.bind(root.account.id).first(StringExpression::eq);
            bindings.bind(root.id).first(StringExpression::eq);
            bindings.bind(root.code).first(StringExpression::eq);
            bindings.bind(root.description).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.orderNO).first(StringExpression::eq);
            bindings.bind(root.suppliers.any().brand.name).first(StringExpression::eq);
            bindings.bind(root.status).first(EnumExpression::eq);
        }
    }
}
