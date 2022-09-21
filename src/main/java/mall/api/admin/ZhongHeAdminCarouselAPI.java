/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.admin.param.BatchIdParam;
import mall.api.admin.param.CarouselAddParam;
import mall.api.admin.param.CarouselEditParam;
import mall.common.ServiceResultEnum;
import mall.config.annotation.TokenToAdminUser;
import mall.entity.AdminUserToken;
import mall.entity.Carousel;
import mall.service.AdminLogService;
import mall.service.ZhongHeMallCarouselService;
import mall.util.BeanUtil;
import mall.util.PageQueryUtil;
import mall.util.Result;
import mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "8-1.后台管理系统轮播图模块接口")
@RequestMapping("/manage-api/v1")
public class ZhongHeAdminCarouselAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeAdminCarouselAPI.class);

    @Resource
    ZhongHeMallCarouselService zhongHeMallCarouselService;
    @Resource
    private AdminLogService adminLogService;

    /**
     * 列表
     */
    @RequestMapping(value = "/carousels", method = RequestMethod.GET)
    @ApiOperation(value = "轮播图列表", notes = "轮播图列表")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码，最小1 ") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("轮播图列表接口  adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        logger.info("列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        Map params = new HashMap(4);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        adminLogService.addSuccessLog(adminUser,"轮播图列表接口",params.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(zhongHeMallCarouselService.getCarouselPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/carousels", method = RequestMethod.POST)
    @ApiOperation(value = "新增轮播图", notes = "新增轮播图")
    public Result save(@RequestBody @Valid CarouselAddParam carouselAddParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("新增轮播图接口  adminUser:{}", adminUser.toString());
        Carousel carousel = new Carousel();
        BeanUtil.copyProperties(carouselAddParam, carousel);
        String result = zhongHeMallCarouselService.saveCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"新增轮播图接口",carouselAddParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/carousels", method = RequestMethod.PUT)
    @ApiOperation(value = "修改轮播图信息", notes = "修改轮播图信息")
    public Result update(@RequestBody CarouselEditParam carouselEditParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("修改轮播图信息接口  adminUser:{}", adminUser.toString());
        Carousel carousel = new Carousel();
        BeanUtil.copyProperties(carouselEditParam, carousel);
        String result = zhongHeMallCarouselService.updateCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"修改轮播图信息接口",carouselEditParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @RequestMapping(value = "/carousels/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取单条轮播图信息", notes = "根据id查询")
    public Result info(@PathVariable("id") Integer id, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("获取单条轮播图信息接口  adminUser:{}", adminUser.toString());
        Carousel carousel = zhongHeMallCarouselService.getCarouselById(id);
        if (carousel == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        adminLogService.addSuccessLog(adminUser,"获取单条轮播图信息接口","id:"+id.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(carousel);
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/carousels", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除轮播图信息", notes = "批量删除轮播图信息")
    public Result delete(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("批量删除轮播图信息接口  adminUser:{}", adminUser.toString());
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (zhongHeMallCarouselService.deleteBatch(batchIdParam.getIds())) {
            adminLogService.addSuccessLog(adminUser,"批量删除轮播图信息接口",batchIdParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

}