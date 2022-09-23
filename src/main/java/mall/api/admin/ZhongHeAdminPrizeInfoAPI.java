package mall.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.admin.param.*;
import mall.common.Constants;
import mall.common.ServiceResultEnum;
import mall.config.annotation.TokenToAdminUser;
import mall.entity.AdminUserToken;
import mall.entity.GoodsCategory;
import mall.entity.ZhongHeMallPrize;
import mall.service.AdminLogService;
import mall.service.ZhongHeMallCategoryService;
import mall.service.ZhongHeMallPrizeService;
import mall.util.BeanUtil;
import mall.util.PageQueryUtil;
import mall.util.Result;
import mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "9-2.后台管理系统奖品模块接口")
@RequestMapping("/manage-api/v1")
public class ZhongHeAdminPrizeInfoAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeAdminPrizeInfoAPI.class);

    @Resource
    private ZhongHeMallPrizeService zhongHeMallPrizeService;
    @Resource
    private ZhongHeMallCategoryService zhongHeMallCategoryService;
    @Resource
    private AdminLogService adminLogService;

    /**
     * 列表
     */
    @RequestMapping(value = "/prize/list", method = RequestMethod.GET)
    @ApiOperation(value = "奖品列表", notes = "可根据名称和上架状态筛选")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码，最小1 ") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "奖品名称") String prizeName,
                       @RequestParam(required = false) @ApiParam(value = "奖品等级") Integer prizeLevel,
                       @RequestParam(required = false) @ApiParam(value = "发放方式") Integer prizeType,
                       @RequestParam(required = false) @ApiParam(value = "上架状态 0-上架 1-下架") Integer prizeSellStatus,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("奖品列表接口   AdminUser:{}",adminUser);
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        logger.info("奖品列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (!StringUtils.isEmpty(prizeName)) {
            params.put("prizeName", prizeName);
        }
        if (prizeLevel != null) {
            params.put("prizeLevel", prizeLevel);
        }
        if (prizeType != null) {
            params.put("prizeType", prizeType);
        }
        if (prizeSellStatus != null) {
            params.put("prizeSellStatus", prizeSellStatus);
        }
        Byte role=adminUser.getRole();
        Long organizationId=adminUser.getOrganizationId();
        params.put("role", role);
        params.put("organizationId", organizationId);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        adminLogService.addSuccessLog(adminUser,"奖品列表接口",params.toString(),"");
        return ResultGenerator.genSuccessResult(zhongHeMallPrizeService.getZhongHeMallPrizePage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/prize", method = RequestMethod.POST)
    @ApiOperation(value = "新增奖品信息", notes = "新增奖品信息")
    public Result save(@RequestBody @Valid PrizeAddParam prizeAddParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("新增奖品信息接口  adminUser:{},prizeAddParam:{}", adminUser.toString(), prizeAddParam.toString());

        byte role = adminUser.getRole();
        if (role == 2) {
            //非管理员没有权限
            return ResultGenerator.genFailResult(ServiceResultEnum.PERMISSION_DENIED.getResult());
        }else if(role == 1) {
            //分行管理员
            Long organizationId=adminUser.getOrganizationId();
            if (!organizationId.equals(prizeAddParam.getOrganizationId())) {
                //分行管理员不可在其他分行活动
                return ResultGenerator.genFailResult(ServiceResultEnum.OTHER_ORG.getResult());
            }
        }
        ZhongHeMallPrize zhongHeMallPrize = new ZhongHeMallPrize();
        BeanUtil.copyProperties(prizeAddParam, zhongHeMallPrize);
        zhongHeMallPrize.setCreateUser(adminUser.getAdminUserId());
        zhongHeMallPrize.setUpdateUser(adminUser.getAdminUserId());
        if (prizeAddParam.getOrganizationId() == null) {
            zhongHeMallPrize.setOrganizationId(adminUser.getOrganizationId());
        }
        String result = zhongHeMallPrizeService.saveZhongHeMallPrize(zhongHeMallPrize);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"新增奖品信息接口",prizeAddParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/prize", method = RequestMethod.PUT)
    @ApiOperation(value = "修改奖品信息", notes = "修改奖品信息")
    public Result update(@RequestBody @Valid PrizeEditParam prizeEditParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("修改奖品信息接口    adminUser:{}", adminUser.toString());

        byte role = adminUser.getRole();
        if (role == 2) {
            //非管理员没有权限
            return ResultGenerator.genFailResult(ServiceResultEnum.PERMISSION_DENIED.getResult());
        }

        ZhongHeMallPrize zhongHeMallPrize = new ZhongHeMallPrize();
        BeanUtil.copyProperties(prizeEditParam, zhongHeMallPrize);
        zhongHeMallPrize.setUpdateUser(adminUser.getAdminUserId());
        String result = zhongHeMallPrizeService.updateZhongHeMallPrize(zhongHeMallPrize);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"修改奖品信息接口",prizeEditParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/prize/{id}")
    @ApiOperation(value = "获取单条奖品信息", notes = "根据id查询")
    public Result info(@PathVariable("id") Long id, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("获取单条奖品信息接口     id:{}", id.toString());
        Map prizeInfo = new HashMap(8);
        ZhongHeMallPrize prize = zhongHeMallPrizeService.getZhongHeMallPrizeById(id);
        if (prize == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        byte role = adminUser.getRole();
        if (role != 0) {
            //非总管理员
            if (!prize.getOrganizationId().equals(adminUser.getOrganizationId())) {
                //奖品存在但属于其他分行
                return ResultGenerator.genFailResult(ServiceResultEnum.OTHER_ORG.getResult());
            }
        }

        logger.info("奖品信息:{}", prize.toString());
        prizeInfo.put("prize", prize);
        GoodsCategory thirdCategory;
        GoodsCategory secondCategory;
        GoodsCategory firstCategory;
        thirdCategory = zhongHeMallCategoryService.getGoodsCategoryById(prize.getPrizeCategoryId());
        if (thirdCategory != null) {
            prizeInfo.put("thirdCategory", thirdCategory);
            secondCategory = zhongHeMallCategoryService.getGoodsCategoryById(thirdCategory.getParentId());
            if (secondCategory != null) {
                prizeInfo.put("secondCategory", secondCategory);
                firstCategory = zhongHeMallCategoryService.getGoodsCategoryById(secondCategory.getParentId());
                if (firstCategory != null) {
                    prizeInfo.put("firstCategory", firstCategory);
                }
            }
        }

        adminLogService.addSuccessLog(adminUser,"获取单条奖品信息接口","id:"+id.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(prizeInfo);
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/prize/status/{sellStatus}", method = RequestMethod.PUT)
    @ApiOperation(value = "批量修改奖品状态", notes = "批量修改奖品状态")
    public Result delete(@RequestBody BatchIdParam batchIdParam, @PathVariable("sellStatus") int sellStatus, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("批量修改奖品状态接口    adminUser:{}", adminUser.toString());
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        byte role = adminUser.getRole();
        if (role == 2) {
            //非管理员没有权限
            return ResultGenerator.genFailResult(ServiceResultEnum.PERMISSION_DENIED.getResult());
        }
        if (zhongHeMallPrizeService.batchUpdateStatus(batchIdParam.getIds(), sellStatus, adminUser.getAdminUserId(),adminUser.getOrganizationId(),adminUser.getRole())) {
            adminLogService.addSuccessLog(adminUser,"批量修改奖品状态接口",
                    "ids:"+batchIdParam.toString()+",status:"+sellStatus,"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

}