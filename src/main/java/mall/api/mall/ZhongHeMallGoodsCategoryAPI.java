package mall.api.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mall.api.mall.vo.ZhongHeMallIndexCategoryVO;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallException;
import mall.service.ZhongHeMallCategoryService;
import mall.util.Result;
import mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(value = "v1", tags = "3.众鹤商城分类页面接口")
@RequestMapping("/api/v1")
public class ZhongHeMallGoodsCategoryAPI {
    private static final Logger logger = LoggerFactory.getLogger(ZhongHeMallGoodsCategoryAPI.class);

    @Resource
    private ZhongHeMallCategoryService zhongHeMallCategoryService;

    @GetMapping("/categories")
    @ApiOperation(value = "用户获取分类数据", notes = "分类页面使用")
    public Result<List<ZhongHeMallIndexCategoryVO>> getCategories() {
        logger.info("用户获取分类数据接口  ");
        List<ZhongHeMallIndexCategoryVO> categories = zhongHeMallCategoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            ZhongHeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
//        logger.info("用户获取分类数据：categories:{}", categories.toString());
        return ResultGenerator.genSuccessResult(categories);
    }
}
