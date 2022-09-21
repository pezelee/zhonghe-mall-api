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
import mall.api.admin.param.*;
import mall.common.Constants;
import mall.common.ServiceResultEnum;
import mall.config.annotation.TokenToAdminUser;
import mall.entity.*;
import mall.service.ActivityService;
import mall.service.AdminLogService;
import mall.service.ZhongHeMallPrizeService;
import mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "9-3.后台管理系统活动模块接口")
@RequestMapping("/manage-api/v1")
public class ZhongHeAdminActivityAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeAdminActivityAPI.class);

    @Resource
    private ZhongHeMallPrizeService zhongHeMallPrizeService;
    @Resource
    private AdminLogService adminLogService;
    @Resource
    private ActivityService activityService;

    @RequestMapping(value = "/activity/list", method = RequestMethod.GET)
    @ApiOperation(value = "活动列表", notes = "可根据名称和上架状态筛选")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码，最小1") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "活动名称") String activityName,
                       @RequestParam(required = false) @ApiParam(value = "开始日期") String queryStartDate,
                       @RequestParam(required = false) @ApiParam(value = "结束日期") String queryEndDate,
                       @RequestParam(required = false) @ApiParam(value = "上架状态 0-上架 1-下架") Integer activityStatus,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("活动列表接口：AdminUser:{}",adminUser);
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        logger.info("列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (!StringUtils.isEmpty(activityName)) {
            params.put("activityName", activityName);
        }
        if (!StringUtils.isEmpty(queryStartDate)) {
            params.put("startDate", queryStartDate + " 00:00:00" );
        }
        if (!StringUtils.isEmpty(queryEndDate)) {
            params.put("endDate", queryEndDate + " 23:59:59" );
        }
        if (activityStatus != null) {
            params.put("status", activityStatus);
        }
        Byte role=adminUser.getRole();
        Long organizationId=adminUser.getOrganizationId();
        params.put("role", role);
        params.put("organizationId", organizationId);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        PageResult result = activityService.getActivityPage(pageUtil);
//        logger.info("活动列表:{}", result.getList().toString());
        adminLogService.addSuccessLog(adminUser,"活动列表接口",params.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(result);
    }

    @RequestMapping(value = "/activity", method = RequestMethod.POST)
    @ApiOperation(value = "新增活动信息", notes = "新增活动信息")
    public Result save(@RequestBody @Valid ActivityAddParam activityAddParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("新增活动信息接口  adminUser:{}", adminUser.toString());

        //判定权限是否符合--总管理员或相应的分行管理员
        Long organizationId = activityAddParam.getOrganizationId();
        String isAdmin = CheckUtils.isAdmin(adminUser,organizationId);
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            logger.info("新增活动信息 错误:{}", isAdmin);
            return ResultGenerator.genFailResult(isAdmin);
        }
        //新增活动信息
        Activity activity = new Activity();
        BeanUtil.copyProperties(activityAddParam, activity);
        activity.setCreateUser(adminUser.getAdminUserId());
        activity.setUpdateUser(adminUser.getAdminUserId());
        String result = activityService.saveActivity(activity);
        logger.info("新增活动信息:{}", result);
        adminLogService.addSuccessLog(adminUser,"新增活动信息接口",activityAddParam.toString(),result.toString());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @RequestMapping(value = "/activity", method = RequestMethod.PUT)
    @ApiOperation(value = "修改活动信息", notes = "修改活动信息")
    public Result update(@RequestBody @Valid ActivityEditParam activityEditParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("修改活动信息接口  adminUser:{}", adminUser.toString());
        //判定权限是否符合--总管理员或相应的分行管理员
        Long organizationId = activityEditParam.getOrganizationId();
        String isAdmin = CheckUtils.isAdmin(adminUser,organizationId);
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            logger.info("修改活动信息 错误:{}", isAdmin);
            return ResultGenerator.genFailResult(isAdmin);
        }

        Activity activity = new Activity();
        BeanUtil.copyProperties(activityEditParam, activity);
        activity.setUpdateUser(adminUser.getAdminUserId());
        String result = activityService.updateActivity(activity);
        logger.info("修改活动信息:{}", result);
        adminLogService.addSuccessLog(adminUser,"修改活动信息接口",activityEditParam.toString(),result.toString());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @GetMapping("/activity/{id}")
    @ApiOperation(value = "获取单条活动信息", notes = "根据id查询")
    public Result info(@PathVariable("id") Long id, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("获取单条活动信息接口    id:{}", id.toString());
        Map activityInfo = new HashMap(8);
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            logger.info("获取单条活动信息 错误:{}", ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult());
            return ResultGenerator.genFailResult(ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult());
        }
        //判定权限是否符合--总管理员或相应的分行管理员
        Long organizationId = activity.getOrganizationId();
        String isAdmin = CheckUtils.isAdmin(adminUser,organizationId);
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            logger.info("配置规则信息 错误:{}", isAdmin);
            return ResultGenerator.genFailResult(isAdmin);
        }
        logger.info("活动信息:{}", activity.toString());
        String prizes=activity.getPrizes();
        String[] prizeIdList = prizes.split(",");
        List<ZhongHeMallPrize> prizeList = new ArrayList<>();
        for (String s : prizeIdList) {
            Long prizeId = Long.valueOf(s);
            ZhongHeMallPrize prizetemp = zhongHeMallPrizeService.getZhongHeMallPrizeById(prizeId);
            prizeList.add(prizetemp);
        }
        logger.info("奖池信息:{}", prizeList.toString());
        activityInfo.put("activity", activity);
        activityInfo.put("prizeList", prizeList);
        adminLogService.addSuccessLog(adminUser,"获取单条活动信息接口",
                "id:"+id.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(activityInfo);
    }

    @RequestMapping(value = "/activity/status/{status}", method = RequestMethod.PUT)
    @ApiOperation(value = "批量修改活动状态", notes = "批量修改活动状态")
    public Result updateStatus(@RequestBody BatchIdParam batchIdParam, @PathVariable("status") int status, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("批量修改活动状态接口  adminUser:{}", adminUser.toString());
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            logger.info("批量修改活动状态 参数异常");
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (status != Constants.SELL_STATUS_UP && status != Constants.SELL_STATUS_DOWN) {
            logger.info("批量修改活动状态 状态异常");
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (activityService.batchUpdateStatus(batchIdParam.getIds(), status, adminUser.getAdminUserId())) {
            logger.info("配置规则信息 结果:{}", ServiceResultEnum.SUCCESS.getResult());
            adminLogService.addSuccessLog(adminUser,"批量修改活动状态接口",
                    "ids:"+batchIdParam.toString()+",status:"+status,"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            logger.info("配置规则信息 结果:{}", "修改失败");
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    @RequestMapping(value = "/activity/draws/{times}", method = RequestMethod.PUT)
    @ApiOperation(value = "批量添加用户参加活动抽奖次数", notes = "批量添加用户参加活动抽奖次数")
    public Result insertDraws(@RequestBody DrawsParam drawsParam, @PathVariable("times") int times,
                              @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("批量添加用户参加活动抽奖次数接口  adminUser:{}", adminUser.toString());
        if (drawsParam == null || drawsParam.getIds().length < 1) {
            logger.info("批量添加用户参加活动抽奖次数 参数异常");
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (times < 0) {
            logger.info("批量添加用户参加活动抽奖次数 次数异常");
            return ResultGenerator.genFailResult("次数异常！");
        }
        //判定权限是否符合--总管理员或相应的分行管理员
        String isAdmin = CheckUtils.isAdmin(adminUser,drawsParam.getOrganizationId());
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            logger.info("批量添加用户参加活动抽奖次数 错误:{}", isAdmin);
            return ResultGenerator.genFailResult(isAdmin);
        }
        if (activityService.batchInsertDraws(drawsParam.getIds(),drawsParam.getActivityId(),times,adminUser.getAdminUserId())) {
            logger.info("配置规则信息 结果:{}", ServiceResultEnum.SUCCESS.getResult());
            adminLogService.addSuccessLog(adminUser,"批量添加用户参加活动抽奖次数接口",drawsParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            logger.info("配置规则信息 结果:{}", "添加失败");
            return ResultGenerator.genFailResult("添加失败");
        }
    }

    @RequestMapping(value = "/activity/drawList", method = RequestMethod.GET)
    @ApiOperation(value = "活动用户可抽奖次数列表", notes = "根据活动ID筛选")
    public Result drawList(@RequestParam(required = false) @ApiParam(value = "页码，最小1") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "活动ID") Long activityId,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("活动用户可抽奖次数列表接口  adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10 || activityId == null) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        logger.info("活动列表：pageNumber:{},pageSize:{}，活动ID{}", pageNumber.toString(),pageSize.toString(),activityId.toString());
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        params.put("role", adminUser.getRole());
        params.put("activityId", activityId);
        params.put("organizationId", adminUser.getOrganizationId());
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        PageResult result = activityService.getActivityDrawsPage(pageUtil);
        logger.info("活动用户可抽奖次数列表:{}", result.toString());
        adminLogService.addSuccessLog(adminUser,"活动用户可抽奖次数列表接口",params.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(result);
    }

    @GetMapping("/activity/rule/{id}")
    @ApiOperation(value = "获取活动配置的规则信息", notes = "根据id查询")
    public Result getRule(@PathVariable("id") Long id, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("获取活动配置的规则信息接口    id:{}", id.toString());
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            logger.info("配置规则信息 错误:{}", ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult());
            return ResultGenerator.genFailResult(ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult());
        }
        //判定权限是否符合--总管理员或相应的分行管理员
        Long organizationId = activity.getOrganizationId();
        String isAdmin = CheckUtils.isAdmin(adminUser,organizationId);
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            logger.info("配置规则信息 错误:{}", isAdmin);
            return ResultGenerator.genFailResult(isAdmin);
        }
        Rule rule = activityService.getRuleByActivityId(id);
        logger.info("规则信息:{}", rule.toString());
        adminLogService.addSuccessLog(adminUser,"获取活动配置的规则信息接口","id:"+id.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(rule);
    }

    @RequestMapping(value = "/activity/rule", method = RequestMethod.PUT)
    @ApiOperation(value = "配置活动的规则信息", notes = "配置活动的规则信息")
    public Result updateRule(@RequestBody @Valid RuleEditParam ruleEditParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("配置规则信息接口  adminUser:{}", adminUser.toString());
        //判定权限是否符合--总管理员或相应的分行管理员
        Activity activity = activityService.getActivityById(ruleEditParam.getActivityId());
        if (activity == null) {
            logger.info("配置规则信息 错误:{}", ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult());
            return ResultGenerator.genFailResult(ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult());
        }
        Long organizationId = activity.getOrganizationId();
        String isAdmin = CheckUtils.isAdmin(adminUser,organizationId);
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            logger.info("配置规则信息 错误:{}", isAdmin);
            return ResultGenerator.genFailResult(isAdmin);
        }

        Rule rule = new Rule();
        BeanUtil.copyProperties(ruleEditParam, rule);
        rule.setUpdateUser(adminUser.getAdminUserId());
        String result = activityService.updateRule(rule);
        logger.info("配置规则信息 结果:{}", result);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"配置规则信息接口",ruleEditParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @GetMapping("/activity/model/{id}")
    @ApiOperation(value = "获取活动的模板信息", notes = "根据id查询")
    public Result getModel(@PathVariable("id") Long id, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("获取活动配置的模板信息接口    id:{}", id.toString());
        Activity activity = activityService.getActivityById(id);
        String check = check(activity,id,adminUser);
        if (!check.equals(ServiceResultEnum.SUCCESS.getResult())) {
            return ResultGenerator.genFailResult(check);
        }
        Model model = activityService.getModelByActivityId(id);
        logger.info("模板信息:{}", model.toString());
        adminLogService.addSuccessLog(adminUser,"获取活动配置的模板信息接口","id:"+id.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(model);
    }

    private String check(Activity activity,Long id,AdminUserToken adminUser){
        if (activity == null) {
            logger.info("配置规则信息 错误:{}", ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult());
            return ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult();
        }
        //判定权限是否符合--总管理员或相应的分行管理员
        Long organizationId = activity.getOrganizationId();
        String isAdmin = CheckUtils.isAdmin(adminUser,organizationId);
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            logger.info("配置模板信息 错误:{}", isAdmin);
            return isAdmin;
        }
        return ServiceResultEnum.SUCCESS.getResult();
    }

//    @RequestMapping(value = "/activity/rule", method = RequestMethod.POST)
//    @ApiOperation(value = "新增活动配置的规则信息", notes = "新增活动配置的规则信息")
//    public Result saveRule(@RequestBody @Valid RuleAddParam ruleAddParam, @TokenToAdminUser AdminUserToken adminUser) {
//        logger.info("新增规则信息  adminUser:{}", adminUser.toString());
//
//        //判定权限是否符合--总管理员或相应的分行管理员
//        Activity activity = activityService.getActivityById(ruleAddParam.getActivityId());
//        if (activity == null) {
//            return ResultGenerator.genFailResult(ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult());
//        }
//        Long organizationId = activity.getOrganizationId();
//        String isAdmin = CheckUtils.isAdmin(adminUser,organizationId);
//        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
//            return ResultGenerator.genFailResult(isAdmin);
//        }
//        //新增活动信息
//        Rule rule = new Rule();
//        BeanUtil.copyProperties(ruleAddParam, rule);
//        rule.setCreateUser(adminUser.getAdminUserId());
//        rule.setUpdateUser(adminUser.getAdminUserId());
//        String result = activityService.saveRule(activity,rule);
//        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
//            return ResultGenerator.genSuccessResult();
//        } else {
//            return ResultGenerator.genFailResult(result);
//        }
//    }

}