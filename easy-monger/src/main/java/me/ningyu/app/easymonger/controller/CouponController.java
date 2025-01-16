package me.ningyu.app.easymonger.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.coupon.Coupon;
import me.ningyu.app.easymonger.domain.coupon.QCoupon;
import me.ningyu.app.easymonger.model.dto.CouponDto;
import me.ningyu.app.easymonger.model.enums.CouponStatus;
import me.ningyu.app.easymonger.model.vo.CouponVo;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.Optional;

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
    public Page<CouponVo> list(@QuerydslPredicate(root = Coupon.class, bindings = CouponSearchBinding.class) Predicate predicate, @PageableDefault(size = 5) @SortDefault.SortDefaults({@SortDefault(sort = "createdDate", direction = Sort.Direction.DESC), @SortDefault(sort = "id", direction = Sort.Direction.ASC)}) Pageable pageable)
    {
        return couponService.list(predicate, pageable);
    }

    @GetMapping("/{id}")
    public CouponVo get(@PathVariable String id)
    {
        return couponService.get(id);
    }

    private static class CouponSearchBinding implements QuerydslBinderCustomizer<QCoupon>
    {
        @Override
        public void customize(QuerydslBindings bindings, QCoupon root)
        {
            bindings.bind(root.account.id).first(StringExpression::eq);
            bindings.bind(root.id).first(StringExpression::eq);
            bindings.bind(root.code).all(this::multiInQuery);
            bindings.bind(root.name).first(StringExpression::eq);
            bindings.bind(root.description).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.orderNO).all(this::multiInQuery);
            bindings.bind(root.suppliers.any().brand.name).first(StringExpression::eq);
            bindings.bind(root.status).first(EnumExpression::eq);

            bindings.bind(root.name).as("search").first((path, value) ->
            {
                BooleanExpression e1 = path.contains(value);
                BooleanExpression e2 = root.code.contains(value);
                BooleanExpression e3 = root.description.contains(value);
                BooleanExpression e4 = root.orderNO.contains(value);

                BooleanExpression condition = e1.or(e2).or(e3).or(e4);
                CouponStatus couponStatus = CouponStatus.findByValue(value);
                if (couponStatus != null)
                {
                    BooleanExpression e5 = root.status.eq(couponStatus);
                    condition = condition.or(e5);
                }

                return condition;
            });
        }

        public Optional<Predicate> multiInQuery(StringPath path, Collection<? extends String> value)
        {
            if (value.size() == 1)
            {
                return Optional.of(path.eq(value.iterator().next()));
            }
            else
            {
                return Optional.of(path.in(value));
            }
        }
    }
}
