package mall.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.admin.param.BatchIdParam;
import mall.api.admin.param.OrderMailParam;
import mall.api.mall.vo.ZhongHeMallOrderDetailVO;
import mall.api.mall.vo.ZhongHeMallUserAddressVO;
import mall.api.mall.vo.ZhongHeMallUserVO;
import mall.common.ServiceResultEnum;
import mall.config.annotation.TokenToAdminUser;
import mall.entity.AdminUserToken;
import mall.entity.excel.*;
import mall.service.AdminLogService;
import mall.service.ZhongHeMallOrderService;
import mall.service.ZhongHeMallUserService;
import mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "8-5.后台管理系统订单模块接口")
@RequestMapping("/manage-api/v1")
public class ZhongHeAdminOrderAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeAdminOrderAPI.class);

    @Resource
    private ZhongHeMallOrderService zhongHeMallOrderService;
    @Resource
    private AdminLogService adminLogService;
    @Resource
    private ZhongHeMallUserService zhongHeMallUserService;

    /**
     * 列表
     */
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    @ApiOperation(value = "订单列表", notes = "可根据订单号和订单状态筛选")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码，最小1 ") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "订单号") String orderNo,
                       @RequestParam(required = false) @ApiParam(value = "组织ID") Long organizationId,
                       @RequestParam(required = false) @ApiParam(value = "用户手机号") String loginName,
                       @RequestParam(required = false) @ApiParam(value = "用户昵称") String nickName,
                       @RequestParam(required = false) @ApiParam(value = "邮寄单号") String mailNo,
                       @RequestParam(required = false) @ApiParam(value = "订单状态") Integer orderStatus,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("订单列表接口  adminUser:{}", adminUser.toString());
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        logger.info("列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        Byte role=adminUser.getRole();
        params.put("role", role);
        if (role == 0) {//总管理员
            if (organizationId != null) {// 填入组织ID
                params.put("organizationId", organizationId);
            }
        }
        else {
            params.put("organizationId", adminUser.getOrganizationId());
        }
        if (!StringUtils.isEmpty(loginName)) {
            params.put("loginName", loginName);
        }
        if (!StringUtils.isEmpty(nickName)) {
            params.put("nickName", nickName);
        }
        if (!StringUtils.isEmpty(mailNo)) {
            params.put("mailNo", mailNo);
        }
        if (!StringUtils.isEmpty(orderNo)) {
            params.put("orderNo", orderNo);
        }
        if (orderStatus != null) {
            params.put("orderStatus", orderStatus);
        }
//        Long organizationId=adminUser.getOrganizationId();

        PageQueryUtil pageUtil = new PageQueryUtil(params);
        adminLogService.addSuccessLog(adminUser,"订单列表接口",params.toString(),"");
        return ResultGenerator.genSuccessResult(zhongHeMallOrderService.getZhongHeMallOrdersPage(pageUtil));
    }

    /**
     * 导出订单列表
     */
    @RequestMapping(value = "/orders/export", method = RequestMethod.GET)
    @ApiOperation(value = "导出订单列表", notes = "可根据订单号和订单状态筛选")
    public void export(@RequestParam(required = false) @ApiParam(value = "组织ID") Long organizationId,
                       @RequestParam(required = false) @ApiParam(value = "用户手机号") String loginName,
                       @RequestParam(required = false) @ApiParam(value = "用户昵称") String nickName,
                       @RequestParam(required = false) @ApiParam(value = "邮寄单号") String mailNo,
                       @RequestParam(required = false) @ApiParam(value = "订单号") String orderNo,
                       @RequestParam(required = false) @ApiParam(value = "订单状态") Integer orderStatus,
                       @RequestParam(required = false) @ApiParam(value = "开始日期") String queryStartDate,
                       @RequestParam(required = false) @ApiParam(value = "结束日期") String queryEndDate,
                       @TokenToAdminUser AdminUserToken adminUser,
                       HttpServletResponse response) {
        logger.info("导出订单列表  adminUser:{}", adminUser.toString());
        Map params = new HashMap(8);
        params.put("page", 1);
        params.put("limit", 10);
        Byte role=adminUser.getRole();
        params.put("role", role);
        if (role == 0) {//总管理员
            if (organizationId != null) {// 填入组织ID
                params.put("organizationId", organizationId);
            }
        }
        else {
            params.put("organizationId", adminUser.getOrganizationId());
        }
        if (!StringUtils.isEmpty(loginName)) {
            params.put("loginName", loginName);
        }
        if (!StringUtils.isEmpty(nickName)) {
            params.put("nickName", nickName);
        }
        if (!StringUtils.isEmpty(mailNo)) {
            params.put("mailNo", mailNo);
        }
        if (!StringUtils.isEmpty(orderNo)) {
            params.put("orderNo", orderNo);
        }
        if (orderStatus != null) {
            params.put("orderStatus", orderStatus);
        }
        if (!StringUtils.isEmpty(queryStartDate)) {
            params.put("startDate", queryStartDate + " 00:00:00" );
        }
        if (!StringUtils.isEmpty(queryEndDate)) {
            params.put("endDate", queryEndDate + " 23:59:59" );
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        List<ExportOrder> result = zhongHeMallOrderService.getZhongHeMallOrdersExport(pageUtil);
        // 导出数据
        ExcelUtils.export(response, "订单列表", result,ExportOrder.class);
    }



    @GetMapping("/orders/{orderId}")
    @ApiOperation(value = "订单详情接口", notes = "传参为订单号")
    public Result orderDetailPage(@ApiParam(value = "订单号") @PathVariable("orderId") Long orderId,
                                                            @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("订单详情接口  adminUser:{}", adminUser.toString());
        ZhongHeMallOrderDetailVO orderDetailVO = zhongHeMallOrderService.getOrderDetailByOrderId(orderId);
        ZhongHeMallUserVO userVO = zhongHeMallUserService.info(orderDetailVO.getUserId());
        ZhongHeMallUserAddressVO addressVO = zhongHeMallOrderService.getAddressByOrderId(orderId);
        Map info = new HashMap(8);
        info.put("orderDetail", orderDetailVO);
        info.put("user", userVO);
        info.put("address", addressVO);
        adminLogService.addSuccessLog(adminUser,"订单详情接口","id:"+orderId.toString(),"");
        return ResultGenerator.genSuccessResult(info);
    }

    /**
     * 配货
     */
    @RequestMapping(value = "/orders/checkDone", method = RequestMethod.PUT)
    @ApiOperation(value = "修改订单状态为配货成功", notes = "批量修改")
    public Result checkDone(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("修改订单状态为配货成功接口  adminUser:{}", adminUser.toString());
        if (batchIdParam==null||batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = zhongHeMallOrderService.checkDone(batchIdParam.getIds());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"修改订单状态为配货成功接口",
                    "ids:"+batchIdParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 出库
     */
//    @RequestMapping(value = "/orders/checkOut", method = RequestMethod.PUT)
//    @ApiOperation(value = "修改订单状态为已出库", notes = "批量修改")
//    public Result checkOut(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUser) {
//        logger.info("修改订单状态为已出库接口  adminUser:{}", adminUser.toString());
//        if (batchIdParam==null||batchIdParam.getIds().length < 1) {
//            return ResultGenerator.genFailResult("参数异常！");
//        }
//        String result = zhongHeMallOrderService.checkOut(batchIdParam.getIds());
//        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
//            adminLogService.addSuccessLog(adminUser,"修改订单状态为已出库接口",
//                    "ids:"+batchIdParam.toString(),"SUCCESS");
//            return ResultGenerator.genSuccessResult();
//        } else {
//            return ResultGenerator.genFailResult(result);
//        }
//    }

    /**
     * 出库
     */
    @RequestMapping(value = "/orders/mailNo", method = RequestMethod.PUT)
    @ApiOperation(value = "订单添加邮寄单号", notes = "添加邮寄单号")
    public Result setMailNo(@RequestBody OrderMailParam orderMailParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("订单添加邮寄单号接口  adminUser:{}", adminUser.toString());
        if (orderMailParam==null) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = zhongHeMallOrderService.setMailNo(orderMailParam);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"订单添加邮寄单号接口",
                    "ids:"+orderMailParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 关闭订单
     */
    @RequestMapping(value = "/orders/close", method = RequestMethod.PUT)
    @ApiOperation(value = "修改订单状态为商家关闭", notes = "批量修改")
    public Result closeOrder(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("修改订单状态为商家关闭接口  adminUser:{}", adminUser.toString());
        if (batchIdParam==null||batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = zhongHeMallOrderService.closeOrder(batchIdParam.getIds());
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            adminLogService.addSuccessLog(adminUser,"修改订单状态为商家关闭接口",
                    "ids:"+batchIdParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 订单导入邮寄单号表
     */
    @PostMapping(value = "/orders/import")
    @ApiOperation(value = "订单导入邮寄单号表", notes = "订单导入邮寄单号表")
    public Result mailNoImport(@RequestPart("file") MultipartFile file,
                             @TokenToAdminUser AdminUserToken adminUser) throws Exception {
        logger.info("订单导入邮寄单号表  ,adminUser={}", adminUser.toString());
        List<ImportOrder> orders = ExcelUtils.readMultipartFile(file,ImportOrder.class);
        logger.info("orders{}",orders.toString());
        List<ImportError> errors = new ArrayList<>();
        for(ImportOrder order:orders){
            if (order.getRowTips().equals("")) {//通过导入格式校验
                logger.info(order.toString());
                String addResult = zhongHeMallOrderService.setMailNoImport(order);
                if (!ServiceResultEnum.SUCCESS.getResult().equals(addResult)) {
                    //新增错误
                    errors.add(ExcelUtils.newError(order.getRowNum(),addResult));
                }
            }else {
                //新增错误
                errors.add(ExcelUtils.newError(order.getRowNum(),order.getRowTips()));
            }
        }
        return ResultGenerator.genSuccessResult(errors);
    }

    /**
     * 下载订单导入邮寄单号模板
     */
    @GetMapping(value = "/orders/template")
    @ApiOperation(value = "下载订单导入邮寄单号模板", notes = "下载订单导入邮寄单号模板")
    public void template(HttpServletResponse response){
        // 导出数据
        ExcelUtils.exportTemplate(response, "订单邮寄单号导入模板", ExampleOrder.class,true);
    }
}