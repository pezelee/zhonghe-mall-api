package mall.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.admin.param.BatchIdParam;
import mall.api.admin.param.UserAddParam;
import mall.common.ServiceResultEnum;
import mall.config.annotation.TokenToAdminUser;
import mall.entity.AdminUserToken;
import mall.service.AdminLogService;
import mall.service.ZhongHeMallUserService;
import mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "8-6.后台管理系统注册用户模块接口")
@RequestMapping("/manage-api/v1")
public class ZhongHeAdminRegisteUserAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeAdminRegisteUserAPI.class);

    @Resource
    private ZhongHeMallUserService zhongHeMallUserService;
    @Resource
    private AdminLogService adminLogService;

    /**
     * 列表
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ApiOperation(value = "商城注册用户列表", notes = "商城注册用户列表")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码，最小1") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "用户状态") Integer lockStatus,
                       @RequestParam(required = false) @ApiParam(value = "用户昵称") String nickName,
                       @RequestParam(required = false) @ApiParam(value = "登录名") String loginName,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("商城注册用户列表接口   adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        logger.info("列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        Map params = new HashMap(8);
        //判定权限是否符合--总管理员
        Long organizationId = adminUser.getOrganizationId();
        String isAdmin = CheckUtils.isChiefAdmin(adminUser);
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            params.put("organizationId", organizationId);//非总管理员，仅能看到自己组织所属用户
        }
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (lockStatus != null) {
            params.put("lockStatus", lockStatus);
        }
        if (loginName != null) {
            params.put("loginName", loginName);
        }
        if (nickName != null) {
            params.put("nickName", nickName);
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        adminLogService.addSuccessLog(adminUser,"商城注册用户列表接口",params.toString(),"");
        return ResultGenerator.genSuccessResult(zhongHeMallUserService.getZhongHeMallUsersPage(pageUtil));
    }

    /**
     * 用户禁用与解除禁用(0-未锁定 1-已锁定)
     */
    @RequestMapping(value = "/users/{lockStatus}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户状态", notes = "批量修改，用户禁用与解除禁用(0-未锁定 1-已锁定)")
    public Result lockUser(@RequestBody BatchIdParam batchIdParam, @PathVariable int lockStatus,
                           @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("修改用户状态接口    adminUser:{}", adminUser.toString());
        if (batchIdParam==null||batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (lockStatus != 0 && lockStatus != 1) {
            return ResultGenerator.genFailResult("操作非法！");
        }
        if (zhongHeMallUserService.lockUsers(batchIdParam.getIds(), lockStatus)) {
            adminLogService.addSuccessLog(adminUser,"修改用户状态接口",
                    "ids:"+batchIdParam.toString()+",status:"+lockStatus,"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("禁用失败");
        }
    }

    @PostMapping("/user/add")
    @ApiOperation(value = "新增用户", notes = "")
    public Result add(@RequestBody @Valid UserAddParam userAddParam,
                           @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("新增用户接口  loginName={},loginResult={}", userAddParam.toString(), adminUser.toString());
        if (!NumberUtil.isPhone(userAddParam.getLoginUserName())){
            return ResultGenerator.genFailResult(ServiceResultEnum.LOGIN_NAME_IS_NOT_PHONE.getResult());
        }
        if (userAddParam.getOrganizationId() != null && !userAddParam.getOrganizationId().equals(adminUser.getOrganizationId())) {
            //分行ID填写为其他分行，判断是否总管理员
//            Long organizationId = adminUser.getOrganizationId();
            String isAdmin = CheckUtils.isChiefAdmin(adminUser);
            if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
                return ResultGenerator.genFailResult(isAdmin);
            }
        }
        String addResult = zhongHeMallUserService.addUser(userAddParam,adminUser);

        logger.info("新增用户结果 api,loginResult={}", addResult);


        //注册成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(addResult)) {
            adminLogService.addSuccessLog(adminUser,"新增用户接口",userAddParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(addResult);
    }
}