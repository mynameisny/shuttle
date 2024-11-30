package me.ningyu.app.easymonger.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.Image;
import me.ningyu.app.easymonger.domain.QImage;
import me.ningyu.app.easymonger.model.CouponVo;
import me.ningyu.app.easymonger.model.ImageVo;
import me.ningyu.app.easymonger.service.ImageService;
import org.springframework.core.io.Resource;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController
{
    private final ImageService imageService;


    @PostMapping
    public ResponseEntity<List<String>> upload(@RequestPart(name = "file") List<MultipartFile> files, @RequestParam(required = false) String accountId) throws IOException
    {
        List<String> list = imageService.uploadImages(files, accountId);

        return new ResponseEntity<>(list, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id)
    {
        imageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public Page<ImageVo> list(@QuerydslPredicate(root = Image.class, bindings = ImageSearchBinding.class) Predicate predicate, @PageableDefault(size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "modifiedDate", direction = Sort.Direction.DESC), @SortDefault(sort = "id", direction = Sort.Direction.ASC)}) Pageable pageable)
    {
        return imageService.list(predicate, pageable);
    }

    @GetMapping("/{id}")
    public void get(@PathVariable String id, HttpServletResponse response)
    {
        imageService.get(id, response);
    }

    /**
     * 将图片转成购物券
     * @param ids   图片的ID
     * @return  购物券ID
     */
    @PostMapping("/coupons")
    public ResponseEntity<List<CouponVo>> convertToCoupon(@RequestBody List<String> ids)
    {
        List<CouponVo> list = imageService.convertToCoupon(ids);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{id}/video")
    public ResponseEntity<Resource> convertToVideo(@PathVariable String id)
    {
        Resource resource = imageService.convertToVideo(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/videos")
    public ResponseEntity<Resource> convertToVideos(@RequestParam List<String> ids)
    {
        Resource resource = null;//todo
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    private static class ImageSearchBinding implements QuerydslBinderCustomizer<QImage>
    {
        @Override
        public void customize(QuerydslBindings bindings, QImage root)
        {
            bindings.bind(root.user.id).first(StringExpression::eq);
            bindings.bind(root.id).first(StringExpression::eq);
            bindings.bind(root.name).first(StringExpression::containsIgnoreCase);
            bindings.bind(root.path).first(StringExpression::eq);
            bindings.bind(root.inUsed).first(BooleanExpression::eq);
            bindings.bind(root.remark).first(StringExpression::contains);
        }
    }
}
