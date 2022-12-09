package mall.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.admin.param.BatchIdParam;
import mall.api.admin.param.IndexConfigAddParam;
import mall.api.admin.param.IndexConfigEditParam;
import mall.common.IndexConfigTypeEnum;
import mall.common.ServiceResultEnum;
import mall.config.annotation.TokenToAdminUser;
import mall.entity.AdminUserToken;
import mall.entity.IndexConfig;
import mall.service.AdminLogService;
import mall.service.ZhongHeMallIndexConfigService;
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
@Api(value = "v1", tags = "8-4.后台管理系统首页配置模块接口")
@RequestMapping("/manage-api/v1")
public class ZhongHeAdminIndexConfigAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeAdminIndexConfigAPI.class);

    @Resource
    private ZhongHeMallIndexConfigService zhongHeMallIndexConfigService;
    @Resource
    private AdminLogService adminLogService;

    /**
     * 列表
     */
    @RequestMapping(value = "/indexConfigs", method = RequestMethod.GET)
    @ApiOperation(value = "首页配置列表", notes = "首页配置列表")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码，最小1 ") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "1-搜索框热搜 2-搜索下拉框热搜 3-(首页)热销商品 4-(首页)新品上线 5-(首页)为你推荐") Integer configType, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("首页配置列表接口  adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
//        logger.info("列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        IndexConfigTypeEnum indexConfigTypeEnum = IndexConfigTypeEnum.getIndexConfigTypeEnumByType(configType);
        if (indexConfigTypeEnum.equals(IndexConfigTypeEnum.DEFAULT)) {
            return ResultGenerator.genFailResult("非法参数！");
        }
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        params.put("configType", configType);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
//        adminLogService.addSuccessLog(adminUser,"首页配置列表接口",params.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(zhongHeMallIndexConfigService.getConfigsPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/indexConfigs", method = RequestMethod.POST)
    @ApiOperation(value = "新增首页配置项", notes = "新增首页配置项")
    public Result save(@RequestBody @Valid IndexConfigAddParam indexConfigAddParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("新增首页配置项接口  adminUser:{}", adminUser.toString());
        IndexConfig indexConfig = new IndexConfig();
        BeanUtil.copyProperties(indexConfigAddParam, indexConfig);
        String result = zhongHeMallIndexConfigService.saveIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"新增首页配置项接口",indexConfigAddParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/indexConfigs", method = RequestMethod.PUT)
    @ApiOperation(value = "修改首页配置项", notes = "修改首页配置项")
    public Result update(@RequestBody @Valid IndexConfigEditParam indexConfigEditParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("修改首页配置项接口  adminUser:{}", adminUser.toString());
        IndexConfig indexConfig = new IndexConfig();
        BeanUtil.copyProperties(indexConfigEditParam, indexConfig);
        String result = zhongHeMallIndexConfigService.updateIndexConfig(indexConfig);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"修改首页配置项接口",indexConfigEditParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @RequestMapping(value = "/indexConfigs/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取单条首页配置项信息", notes = "根据id查询")
    public Result info(@PathVariable("id") Long id, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("获取单条首页配置项信息接口  adminUser:{}", adminUser.toString());
        IndexConfig config = zhongHeMallIndexConfigService.getIndexConfigById(id);
        if (config == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
//        adminLogService.addSuccessLog(adminUser,"获取单条首页配置项信息接口","id:"+id.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(config);
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/indexConfigs", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除首页配置项信息", notes = "批量删除首页配置项信息")
    public Result delete(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("批量删除首页配置项信息接口  adminUser:{}", adminUser.toString());
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (zhongHeMallIndexConfigService.deleteBatch(batchIdParam.getIds())) {
            adminLogService.addSuccessLog(adminUser,"批量删除首页配置项信息接口",batchIdParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

}