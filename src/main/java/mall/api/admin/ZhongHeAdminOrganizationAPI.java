package mall.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.admin.param.*;
import mall.common.ServiceResultEnum;
import mall.config.annotation.TokenToAdminUser;
import mall.entity.AdminUserToken;
import mall.entity.Organization;
import mall.service.AdminLogService;
import mall.service.ZhongHeMallOrganizationService;
import mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "9-1.后台管理系统分行组织模块接口")
@RequestMapping("/manage-api/v1")
public class ZhongHeAdminOrganizationAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeAdminOrganizationAPI.class);

    @Resource
    ZhongHeMallOrganizationService zhongHeMallOrganizationService;
    @Resource
    private AdminLogService adminLogService;

    /**
     * 列表
     */
    @RequestMapping(value = "/organizations", method = RequestMethod.GET)
    @ApiOperation(value = "分行组织列表", notes = "分行组织列表")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码，最小1 ") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("分行组织列表接口  adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
//        logger.info("列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        Map params = new HashMap(4);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
//        adminLogService.addSuccessLog(adminUser,"分行组织列表接口",params.toString(),"");
        return ResultGenerator.genSuccessResult(zhongHeMallOrganizationService.getOrganizationPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/organizations", method = RequestMethod.POST)
    @ApiOperation(value = "新增分行组织", notes = "新增分行组织")
    public Result save(@RequestBody @Valid OrganizationAddParam organizationAddParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("新增分行接口  adminUser:{}", adminUser.toString());
        Byte role = adminUser.getRole();
        if (role != 0) {
            //非总管理员
            return ResultGenerator.genFailResult(ServiceResultEnum.PERMISSION_DENIED.getResult());
        }
        Organization organization = new Organization();
        BeanUtil.copyProperties(organizationAddParam, organization);
        organization.setCreateUser(adminUser.getAdminUserId());
        organization.setUpdateUser(adminUser.getAdminUserId());
        logger.info("新增分行信息：organization:{}", organization.toString());
        String result = zhongHeMallOrganizationService.saveOrganization(organization);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"新增分行接口",organizationAddParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/organizations", method = RequestMethod.PUT)
    @ApiOperation(value = "修改分行组织", notes = "修改分行组织")
    public Result update(@RequestBody OrganizationEditParam organizationEditParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("修改分行组织接口  adminUser:{}，organizationEditParam:{}", adminUser.toString(),organizationEditParam.toString());
        Byte role = adminUser.getRole();
        if (role != 0) {
            //非总管理员
            return ResultGenerator.genFailResult(ServiceResultEnum.PERMISSION_DENIED.getResult());
        }
        Organization organization = new Organization();
        BeanUtil.copyProperties(organizationEditParam, organization);
        String result = zhongHeMallOrganizationService.updateOrganization(organization);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"修改分行组织接口",organizationEditParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @RequestMapping(value = "/organizations/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取单条分行组织信息", notes = "根据id查询")
    public Result info(@PathVariable("id") Long id, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("获取单条分行组织信息接口   adminUser:{}", adminUser.toString());
        //判定权限是否符合--总管理员或相应的分行管理员
        String isAdmin = CheckUtils.isAdmin(adminUser,id);
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            return ResultGenerator.genFailResult(isAdmin);
        }
        Organization organization = zhongHeMallOrganizationService.getOrganizationById(id);
        if (organization == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
//        adminLogService.addSuccessLog(adminUser,"获取单条分行组织信息接口","id:"+id.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(organization);
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/organizations", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除分行组织信息", notes = "批量删除分行组织信息")
    public Result delete(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("批量删除分行组织接口    adminUser:{}", adminUser.toString());
        Byte role = adminUser.getRole();
        if (role != 0) {
            //非总管理员
            return ResultGenerator.genFailResult(ServiceResultEnum.PERMISSION_DENIED.getResult());
        }
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        Long updateUser = adminUser.getAdminUserId();
        if (zhongHeMallOrganizationService.deleteBatch(batchIdParam.getIds(),updateUser)) {
            adminLogService.addSuccessLog(adminUser,"批量删除分行组织接口","ids:"+batchIdParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

}