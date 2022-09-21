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
import mall.entity.AdminUser;
import mall.entity.AdminUserToken;
import mall.service.AdminLogService;
import mall.service.AdminUserService;
import mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 13
 * @qq交流群 796794009
 * @email 2449207463@qq.com
 */
@RestController
@Api(value = "v1", tags = "8-0.后台管理系统管理员模块接口")
@RequestMapping("/manage-api/v1")
public class ZhongHeAdminManageUserAPI {

    @Resource
    private AdminUserService adminUserService;
    @Resource
    private AdminLogService adminLogService;

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeAdminManageUserAPI.class);

    @RequestMapping(value = "/adminUser", method = RequestMethod.GET)
    @ApiOperation(value = "商城管理员列表", notes = "商城管理员列表")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码，最小1 ") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "用户状态") Byte lockStatus, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("查看管理员列表接口  adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        logger.info("列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        params.put("organizationId", adminUser.getOrganizationId());//组织ID
        if (lockStatus != null) {
            params.put("lockStatus", lockStatus);
        }else {
            params.put("lockStatus", 0);
        }
        params.put("role", adminUser.getRole());//角色
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        logger.info("查看分页信息，pageUtil:{}", pageUtil.toString());
        adminLogService.addSuccessLog(adminUser,"查看管理员列表接口",params.toString(),"");
        return ResultGenerator.genSuccessResult(adminUserService.getZhongHeMallAdminsPage(pageUtil));
    }

    @RequestMapping(value = "/adminUser/login", method = RequestMethod.POST)
    public Result<String> login(@RequestBody @Valid AdminLoginParam adminLoginParam) {
        logger.info("管理员登录接口  adminLoginParam={}", adminLoginParam.toString());
        String loginResult = adminUserService.login(adminLoginParam.getUserName(), adminLoginParam.getPasswordMd5());
        logger.info("管理员登录 api,adminName={},loginResult={}", adminLoginParam.getUserName(), loginResult);

        //登录成功
        if (!StringUtils.isEmpty(loginResult) && loginResult.length() == Constants.TOKEN_LENGTH) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(loginResult);
//            adminLogService.addSuccessLog(adminUser,"查看管理员列表接口",params.toString(),"");
            return result;
        }
        //登录失败
        return ResultGenerator.genFailResult(loginResult);
    }

    @RequestMapping(value = "/adminUser/profile", method = RequestMethod.GET)
    @ApiOperation(value = "查看本管理员信息", notes = "查看本管理员信息")
    public Result profile(@TokenToAdminUser AdminUserToken adminUser) {
        logger.info("查看本管理员信息接口   adminUser:{}", adminUser.toString());
        AdminUser adminUserEntity = adminUserService.getUserDetailById(adminUser.getAdminUserId());
        if (adminUserEntity != null) {
            adminUserEntity.setLoginPassword("******");
            Result result = ResultGenerator.genSuccessResult();
            result.setData(adminUserEntity);
            adminLogService.addSuccessLog(adminUser,"查看本管理员信息接口","","SUCCESS");
            return result;
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }

    @RequestMapping(value = "/adminUser/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "查看其他管理员信息", notes = "查看其他管理员信息")
    public Result update(@PathVariable("id") Long id,@TokenToAdminUser AdminUserToken adminUser) {
        logger.info("查看其他管理员信息接口   adminUser:{}，id:{}", adminUser.toString(),id);
        AdminUser adminUserEntity = adminUserService.getUserDetailById(id);
        if (adminUserEntity != null) {
            adminUserEntity.setLoginPassword("******");
            Result result = ResultGenerator.genSuccessResult();
            result.setData(adminUserEntity);
            adminLogService.addSuccessLog(adminUser,"查看其他管理员信息接口","id:"+id.toString(),"SUCCESS");
            return result;
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }

    @RequestMapping(value = "/adminUser/password", method = RequestMethod.PUT)
    public Result passwordUpdate(@RequestBody @Valid UpdateAdminPasswordParam adminPasswordParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("修改管理员密码接口    adminUser:{}", adminUser.toString());
        if (adminUserService.updatePassword(adminUser.getAdminUserId(), adminPasswordParam.getOriginalPassword(), adminPasswordParam.getNewPassword())) {
            adminLogService.addSuccessLog(adminUser,"修改管理员密码接口",adminPasswordParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    @RequestMapping(value = "/adminUser/info", method = RequestMethod.PUT)
    @ApiOperation(value = "修改管理员信息", notes = "修改管理员信息")
    public Result nameUpdate(@RequestBody @Valid UpdateAdminInfoParam adminInfoParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("修改管理员信息接口   adminUser:{}", adminUser.toString());

        //判定权限是否符合--总管理员或相应的分行管理员
        Long organizationId = adminInfoParam.getOrganizationId();
        String isAdmin = CheckUtils.isAdmin(adminUser,organizationId);
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            return ResultGenerator.genFailResult(isAdmin);
        }
        if (adminUserService.updateName(adminInfoParam.getAdminUserId(), adminInfoParam.getLoginUserName(), adminInfoParam.getNickName())) {
            adminLogService.addSuccessLog(adminUser,"修改管理员信息接口",adminInfoParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    public Result logout(@TokenToAdminUser AdminUserToken adminUser) {
        logger.info("登出接口  adminUser:{}", adminUser.toString());
        adminUserService.logout(adminUser.getAdminUserId());
        adminLogService.addSuccessLog(adminUser,"登出接口",adminUser.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult();
    }

    @RequestMapping(value = "/adminUser", method = RequestMethod.POST)
    @ApiOperation(value = "新增管理员", notes = "新增管理员")
    public Result save(@RequestBody @Valid AdminAddParam adminAddParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("新增管理员接口   adminUser:{}", adminUser.toString());
        if (!NumberUtil.isPhone(adminAddParam.getLoginUserName())){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        //判定权限是否符合--总管理员或相应的分行管理员
        Long organizationId = adminAddParam.getOrganizationId();
        String isAdmin = CheckUtils.isAdmin(adminUser,organizationId);
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            return ResultGenerator.genFailResult(isAdmin);
        }
        AdminUser temp = new AdminUser();
        BeanUtil.copyProperties(adminAddParam, temp);
        temp.setCreateUser(adminUser.getAdminUserId());
        temp.setUpdateUser(adminUser.getAdminUserId());
        logger.info("新增管理员信息：info:{}", temp.toString());
        String result = adminUserService.saveAdmin(temp);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"新增管理员接口",adminAddParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @RequestMapping(value = "/adminUser", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除管理员信息", notes = "批量删除管理员信息")
    public Result delete(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("批量删除管理员接口   adminUser:{}", adminUser.toString());
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        Byte lockStatus=1;
        if (adminUserService.deleteBatch(batchIdParam.getIds(),lockStatus)) {
            adminLogService.addSuccessLog(adminUser,"批量删除管理员接口","ids:"+batchIdParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

}