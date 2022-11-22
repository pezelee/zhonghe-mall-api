package mall.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.admin.param.LotterydrawMailParam;
import mall.api.admin.param.NoticeAddParam;
import mall.api.mall.vo.ZhongHeMallUserVO;
import mall.common.ServiceResultEnum;
import mall.config.annotation.TokenToAdminUser;
import mall.entity.*;
import mall.entity.excel.*;
import mall.service.*;
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
@Api(value = "v1", tags = "9-4.后台管理系统抽奖模块接口")
@RequestMapping("/manage-api/v1")
public class ZhongHeAdminLotterydrawAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeAdminLotterydrawAPI.class);

    @Resource
    private LotterydrawService lotterydrawService;
    @Resource
    private AdminLogService adminLogService;
    @Resource
    private ZhongHeMallUserService userService;
    @Resource
    private NoticeService noticeService;


    /**
     * 列表
     */
    @RequestMapping(value = "/lotterydraw/list", method = RequestMethod.GET)
    @ApiOperation(value = "抽奖记录列表", notes = "可根据活动名称、客户经理号、奖品名称、用户名称和上架状态筛选")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码，最小1 ") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "活动名称") String activityName,
                       @RequestParam(required = false) @ApiParam(value = "奖品名称") String prizeName,
                       @RequestParam(required = false) @ApiParam(value = "奖品种类") Integer prizeType,
                       @RequestParam(required = false) @ApiParam(value = "用户名称") String nickName,
                       @RequestParam(required = false) @ApiParam(value = "开始日期") String queryStartDate,
                       @RequestParam(required = false) @ApiParam(value = "结束日期") String queryEndDate,
//                       @RequestParam(required = false) @ApiParam(value = "客户经理号") String sponsor,
                       @RequestParam(required = false) @ApiParam(value = "邮寄单号") String mailNo,
                       @RequestParam(required = false) @ApiParam(value = "状态 0.待抽奖 1.已抽奖 2.待送货 3:送货中" +
                               " 4.送货完成  5:已接收 -1.抽奖失败 -2.用户关闭 -3.商家关闭") Integer status,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("抽奖记录列表接口  adminUser:{}", adminUser.toString());
        if (pageNumber < 1 || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        logger.info("列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (!StringUtils.isEmpty(activityName)) {
            params.put("activityName", activityName);
        }
        if (!StringUtils.isEmpty(prizeName)) {
            params.put("prizeName", prizeName);
        }
        if (!StringUtils.isEmpty(prizeType)) {
            params.put("prizeType", prizeType);
        }
        if (!StringUtils.isEmpty(nickName)) {
            params.put("nickName", nickName);
        }
        if (!StringUtils.isEmpty(mailNo)) {
            params.put("mailNo", mailNo);
        }
        if (!StringUtils.isEmpty(queryStartDate)) {
            params.put("startDate", queryStartDate + " 00:00:00" );
        }
        if (!StringUtils.isEmpty(queryEndDate)) {
            params.put("endDate", queryEndDate + " 23:59:59" );
        }
//        if (sponsor != null) {
//            params.put("sponsor", sponsor);
//        }
        if (status != null) {
            params.put("status", status);
        }
        Byte role = adminUser.getRole();
        Long organizationId = adminUser.getOrganizationId();
        params.put("role", role);
        params.put("organizationId", organizationId);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        adminLogService.addSuccessLog(adminUser,"抽奖记录列表接口",params.toString(),"");
        return ResultGenerator.genSuccessResult(lotterydrawService.getLotteryDrawPage(pageUtil));
    }

    /**
     * 导出抽奖记录
     */
    @RequestMapping(value = "/lotterydraw/export", method = RequestMethod.GET)
    @ApiOperation(value = "导出抽奖记录", notes = "可根据活动名称、客户经理号、奖品名称、用户名称和上架状态筛选")
    public void export(@RequestParam(required = false) @ApiParam(value = "活动名称") String activityName,
                       @RequestParam(required = false) @ApiParam(value = "奖品名称") String prizeName,
                       @RequestParam(required = false) @ApiParam(value = "奖品种类") Integer prizeType,
                       @RequestParam(required = false) @ApiParam(value = "用户名称") String nickName,
                       @RequestParam(required = false) @ApiParam(value = "开始日期") String queryStartDate,
                       @RequestParam(required = false) @ApiParam(value = "结束日期") String queryEndDate,
//                       @RequestParam(required = false) @ApiParam(value = "客户经理号") String sponsor,
                       @RequestParam(required = false) @ApiParam(value = "邮寄单号") String mailNo,
                       @RequestParam(required = false) @ApiParam(value = "状态 0.待抽奖 1.已抽奖 2.待送货 3:送货中" +
                               " 4.送货完成  5:已接收 -1.抽奖失败 -2.用户关闭 -3.商家关闭") Integer status,
                       @TokenToAdminUser AdminUserToken adminUser,
                       HttpServletResponse response) {
        logger.info("导出抽奖记录  adminUser:{}", adminUser.toString());

        Map params = new HashMap(8);
        params.put("page", 1);
        params.put("limit", 10);
        if (!StringUtils.isEmpty(activityName)) {
            params.put("activityName", activityName);
        }
        if (!StringUtils.isEmpty(prizeName)) {
            params.put("prizeName", prizeName);
        }
        if (!StringUtils.isEmpty(prizeType)) {
            params.put("prizeType", prizeType);
        }
        if (!StringUtils.isEmpty(nickName)) {
            params.put("nickName", nickName);
        }
        if (!StringUtils.isEmpty(mailNo)) {
            params.put("mailNo", mailNo);
        }
        if (!StringUtils.isEmpty(queryStartDate)) {
            params.put("startDate", queryStartDate + " 00:00:00" );
        }
        if (!StringUtils.isEmpty(queryEndDate)) {
            params.put("endDate", queryEndDate + " 23:59:59" );
        }
//        if (sponsor != null) {
//            params.put("sponsor", sponsor);
//        }
        if (status != null) {
            params.put("status", status);
        }
        Byte role = adminUser.getRole();
        Long organizationId = adminUser.getOrganizationId();
        params.put("role", role);
        params.put("organizationId", organizationId);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        adminLogService.addSuccessLog(adminUser,"抽奖记录列表接口",params.toString(),"");
        List<ExportLotterydraw> result = lotterydrawService.getLotteryDrawExport(pageUtil);// 导出数据
        ExcelUtils.export(response, "抽奖记录列表", result,ExportLotterydraw.class);
//        return ResultGenerator.genSuccessResult(lotterydrawService.getLotteryDrawPage(pageUtil));
    }


    /**
     * 详情
     */
    @GetMapping("/lotterydraw/{id}")
    @ApiOperation(value = "获取单条抽奖记录", notes = "根据id查询")
    public Result info(@PathVariable("id") Long id, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("获取单条抽奖记录接口    id:{}", id.toString());
//        Map lotterydrawInfo = new HashMap(8);
        LotteryDraw lotterydraw = lotterydrawService.getLotteryDrawById(id);
        if (lotterydraw == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        byte role = adminUser.getRole();
        if (role != 0) {//非总管理员
            if (!CheckUtils.isSameId(adminUser.getOrganizationId(),lotterydraw.getOrganizationId())) {//组织不符合
                return ResultGenerator.genFailResult(ServiceResultEnum.OTHER_ORG.getResult());
            }
        }
        logger.info("抽奖记录:{}", lotterydraw.toString());

//        lotterydrawInfo.put("lotterydraw", lotterydraw);
        adminLogService.addSuccessLog(adminUser,"获取单条抽奖记录接口","id:"+id.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(lotterydraw);
    }

    /**
     * 管理员发送VIP会员卡号
     */
    @RequestMapping(value = "/lotterydraw/sendVIP", method = RequestMethod.PUT)
    @ApiOperation(value = "管理员发送VIP会员卡号", notes = "管理员发送VIP会员卡号")
    public Result sendVIP(
            @RequestParam @ApiParam(value = "抽奖记录ID") Long id,
            @RequestParam @ApiParam(value = "会员卡号") String VIPKEY,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("管理员发送VIP会员卡号接口    id:{},VIPKEY:{},adminUser:{}", id.toString(),VIPKEY,adminUser.toString());

        LotteryDraw lotterydraw = lotterydrawService.getLotteryDrawById(id);
        if (lotterydraw == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        if (lotterydraw.getPrizeType() != 3) {
            //非VIP卡类奖品
            return ResultGenerator.genFailResult(ServiceResultEnum.PRIZE_TYPE_ERROR.getResult());
        }
        logger.info("抽奖记录:{}", lotterydraw.toString());
        //判定权限是否符合--总管理员或相应的分行管理员
        String isAdmin = CheckUtils.isAdmin(adminUser,lotterydraw.getOrganizationId());
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            return ResultGenerator.genFailResult(isAdmin);
        }
        ZhongHeMallUserVO user = userService.info(lotterydraw.getUserId());
        NoticeAddParam addParam = new NoticeAddParam();
        addParam.setTitle("会员卡号领取");
        addParam.setSender("会员卡发放中心");
        addParam.setNotice("您在"+ lotterydraw.getActivityName() +"活动中获得的 " + lotterydraw.getPrizeName() +
                " 已发送, 兑换码： "+VIPKEY+"，请及时领取。");
        addParam.setNoticeType((byte)0);
        noticeService.saveNotice(addParam,lotterydraw.getUserId());
        //修改状态已接收
        if (lotterydrawService.received(id)) {
            adminLogService.addSuccessLog(adminUser,"管理员发送VIP会员卡号接口","id:"+id.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.LOTTERY_STATUS_ERROR.getResult());
    }

    /**
     * 添加邮寄单号
     */
    @RequestMapping(value = "/lotterydraw", method = RequestMethod.PUT)
    @ApiOperation(value = "管理员添加邮寄单号", notes = "管理员添加邮寄单号")
    public Result mail(@RequestBody LotterydrawMailParam mailParam,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("抽奖记录添加邮寄单号接口    mailParam:{},adminUser:{}", mailParam.toString(),adminUser.toString());

        Long id=mailParam.getLotteryDrawId();
        LotteryDraw lotterydraw = lotterydrawService.getLotteryDrawById(id);
        if (lotterydraw == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        byte role = adminUser.getRole();
        if (role != 0) {//非总管理员
            if (!CheckUtils.isSameId(adminUser.getOrganizationId(),lotterydraw.getOrganizationId())) {//组织不符合
                return ResultGenerator.genFailResult(ServiceResultEnum.OTHER_ORG.getResult());
            }
        }
        //填入运单号，修改状态为发送中
        if (lotterydrawService.sending(mailParam)) {
            adminLogService.addSuccessLog(adminUser,"添加邮寄单号接口",mailParam.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.MAIL_NO_ERROR.getResult());
//        lotterydrawService.sending(mailParam);
//        logger.info("抽奖记录:{}", lotterydraw.toString());
//        return ResultGenerator.genSuccessResult(lotterydrawService.sending(mailParam));
    }

    /**
     * 管理员修改抽奖记录为用户已接收
     */
    @RequestMapping(value = "/lotterydraw/received/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "管理员修改抽奖记录为用户已接收", notes = "根据ID修改")
    public Result received(@PathVariable("id") Long id,
                       @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("修改抽奖记录为用户已接收接口    id:{},adminUser:{}", id.toString(),adminUser.toString());

//        Long id=mailParam.getLotteryDrawId();
        LotteryDraw lotterydraw = lotterydrawService.getLotteryDrawById(id);
        if (lotterydraw == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        logger.info("抽奖记录:{}", lotterydraw.toString());
        //判定权限是否符合--总管理员或相应的分行管理员
        String isAdmin = CheckUtils.isAdmin(adminUser,lotterydraw.getOrganizationId());
        if (!isAdmin.equals(ServiceResultEnum.SUCCESS.getResult())) {
            return ResultGenerator.genFailResult(isAdmin);
        }
//        byte role = adminUser.getRole();
//        if (role != 0) {//非总管理员
//            if (!CheckUtils.isSameId(adminUser.getOrganizationId(),lotterydraw.getOrganizationId())) {//组织不符合
//                return ResultGenerator.genFailResult(ServiceResultEnum.OTHER_ORG.getResult());
//            }
//        }
        //修改状态
        if (lotterydrawService.received(id)) {
            adminLogService.addSuccessLog(adminUser,"修改抽奖记录为用户已接收接口","id:"+id.toString(),"SUCCESS");
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.LOTTERY_STATUS_ERROR.getResult());
    }

    /**
     * 订单导入邮寄单号表
     */
    @PostMapping(value = "/lotterydraw/import")
    @ApiOperation(value = "抽奖记录导入邮寄单号表", notes = "抽奖记录导入邮寄单号表")
    public Result mailNoImport(@RequestPart("file") MultipartFile file,
                             @TokenToAdminUser AdminUserToken adminUser) throws Exception {
        logger.info("抽奖记录导入邮寄单号表接口  ,adminUser={}", adminUser.toString());
        List<ImportLotterydraw> lotterydraws = ExcelUtils.readMultipartFile(file,ImportLotterydraw.class);
        List<ImportError> errors = new ArrayList<>();
        if (lotterydraws.size() == 0) {
            errors.add(ExcelUtils.newError(1,"导入表为空"));
        }
        logger.info("lotterydraws{}",lotterydraws.toString());
        for(ImportLotterydraw lotterydraw:lotterydraws){
            if (lotterydraw.getRowTips().equals("")) {//通过导入格式校验
                logger.info(lotterydraw.toString());
                String addResult = lotterydrawService.setMailNoImport(lotterydraw);
                if (!ServiceResultEnum.SUCCESS.getResult().equals(addResult)) {
                    //新增错误
                    errors.add(ExcelUtils.newError(lotterydraw.getRowNum(),addResult));
                }
            }else {
                //新增错误
                errors.add(ExcelUtils.newError(lotterydraw.getRowNum(),lotterydraw.getRowTips()));
            }
        }
        return ResultGenerator.genSuccessResult(errors);
    }

    /**
     * 下载抽奖记录导入邮寄单号模板
     */
    @GetMapping(value = "/lotterydraw/template")
    @ApiOperation(value = "下载抽奖记录导入邮寄单号模板", notes = "下载抽奖记录导入邮寄单号模板")
    public void template(HttpServletResponse response){
        // 导出数据
        ExcelUtils.exportTemplate(response, "抽奖记录邮寄单号导入模板", ExampleLotterydraw.class,true);
    }

}