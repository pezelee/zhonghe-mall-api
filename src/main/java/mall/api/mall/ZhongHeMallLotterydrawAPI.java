package mall.api.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.mall.param.SaveLotteryDrawParam;
import mall.api.mall.vo.ZhongHeMallPrizeDetailVO;
import mall.common.ServiceResultEnum;
import mall.config.annotation.TokenToAdminUser;
import mall.config.annotation.TokenToMallUser;
import mall.entity.*;
import mall.service.*;
import mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.text.DecimalFormat;
import java.util.*;

@RestController
@Api(value = "v1", tags = "6-1.众鹤商城抽奖相关接口")
@RequestMapping("/api/v1")
public class ZhongHeMallLotterydrawAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeMallLotterydrawAPI.class);

    @Resource
    private ZhongHeMallPrizeService zhongHeMallPrizeService;

    @Resource
    private LotterydrawService lotterydrawService;

    @Resource
    private ActivityService activityService;

    @Resource
    private ZhongHeMallUserService userService;

    @Resource
    private ZhongHeMallUserAddressService zhongHeMallUserAddressService;

    /**
     * 个人中心抽奖记录列表
     */
    @RequestMapping(value = "/lotterydraw/mylist", method = RequestMethod.GET)
    @ApiOperation(value = "个人中心抽奖记录列表", notes = "可根据活动名称、客户经理号、奖品名称、用户名称和上架状态筛选")
    public Result personList(@RequestParam(required = false) @ApiParam(value = "页码，最小1 ") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "活动名称") String activityName,
                       @RequestParam(required = false) @ApiParam(value = "奖品名称") String prizeName,
                       @RequestParam(required = false) @ApiParam(value = "状态 0.待抽奖 1.已抽奖 2.待送货 3:送货中" +
                               " 4.送货完成  5:已接收  -1.抽奖失败 -2.用户关闭 -3.商家关闭") Integer status,
                       @TokenToMallUser MallUser loginMallUser) {
        logger.info("个人中心抽奖记录列表接口  adminUser:{}", loginMallUser.toString());
        if (pageNumber < 1 || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
//        logger.info("列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        Long userId = loginMallUser.getUserId();
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (!StringUtils.isEmpty(activityName)) {
            params.put("activityName", activityName);
        }
        if (!StringUtils.isEmpty(prizeName)) {
            params.put("prizeName", prizeName);
        }
        if (status != null) {
            params.put("status", status);
        }
        Byte role = -1;
        params.put("role", role);
        params.put("userId", userId);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(lotterydrawService.getLotteryDrawPage(pageUtil));
    }

    /**
     * 个人中心积分收入记录列表
     */
    @RequestMapping(value = "/lotterydraw/pointlist", method = RequestMethod.GET)
    @ApiOperation(value = "个人中心积分收入记录列表", notes = "可根据活动名称、客户经理号、奖品名称、用户名称和上架状态筛选")
    public Result pointList(@RequestParam(required = false) @ApiParam(value = "页码，最小1 ") Integer pageNumber,
                            @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                            @TokenToMallUser MallUser loginMallUser) {
        logger.info("个人中心积分收入列表接口  adminUser:{}", loginMallUser.toString());
        if (pageNumber < 1 || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
//        logger.info("列表参数：pageNumber:{},pageSize:{}", pageNumber.toString(),pageSize.toString());
        Long userId = loginMallUser.getUserId();
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        params.put("userId", userId);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(lotterydrawService.getPointList(pageUtil));
    }

    /**
     * 活动抽奖记录列表
     */
    @RequestMapping(value = "/lotterydraw/alllist", method = RequestMethod.GET)
    @ApiOperation(value = "本次活动所有人抽奖记录列表", notes = "可根据活动名称、客户经理号、奖品名称、用户名称和上架状态筛选")
    public Result allList(@RequestParam(required = false) @ApiParam(value = "页码，最小1 ") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数，最小10条") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "活动Id") Long activityId,
                       @TokenToMallUser MallUser loginMallUser) {
        logger.info("alllist本次活动所有人抽奖记录列表接口  adminUser:{}", loginMallUser.toString());
        if (pageNumber < 1 || pageSize < 10) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        if (activityId ==null) {
            return ResultGenerator.genFailResult("活动ID不能为空！");
        }
//        logger.info("列表参数：pageNumber:{},pageSize:{},activityId:{}", pageNumber.toString(),pageSize.toString(),activityId.toString());
        Long userId = loginMallUser.getUserId();
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        if (!StringUtils.isEmpty(activityId)) {
            params.put("activityId", activityId);
        }
        Byte role = -2;
        params.put("role", role);
//        params.put("userId", userId);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(lotterydrawService.getLotteryDrawPage(pageUtil));
    }

    /**
     * 抽奖记录列表
     */
    @RequestMapping(value = "/lotterydraw/avtivity", method = RequestMethod.GET)
    @ApiOperation(value = "活动列表", notes = "当前可用的活动列表")
    public Result avtivityList( @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户当前可用的活动列表接口  User:{}", loginMallUser.toString());
        Long orgId = loginMallUser.getOrganizationId();
        List<Activity> activityList = activityService.getActivityList(orgId);
        if (activityList == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(activityList);
    }

    /**
     * 查询抽奖次数
     */
    @GetMapping("/lotterydraw/drawtimes/{id}")
    @ApiOperation(value = "查询抽奖次数", notes = "查询抽奖次数")
    public Result drawtimes(@PathVariable("id") Long id, @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户查询抽奖次数接口  User:{}，activityId:{}", loginMallUser.toString(),id);
        ActivityDraw activityDraw =activityService.getActivityDraws(loginMallUser.getUserId(),id);
        if (activityDraw == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.ERROR_DRAW_TIMES.getResult());
        }
        return ResultGenerator.genSuccessResult(activityDraw);
    }

    /**
     * 抽奖
     */
    @RequestMapping(value = "/lotterydraw/draw/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "抽奖", notes = "抽奖")
    public Result draw(@PathVariable("id") Long id, @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户抽奖接口  User:{}，activityId:{}", loginMallUser.toString(),id);
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult());
        }
        logger.info("活动信息:{}", activity.toString());
        //------------------抽奖前核对--------------------
        if (!CheckUtils.isSameId(loginMallUser.getOrganizationId(),activity.getOrganizationId())) {//组织不符合
            return ResultGenerator.genFailResult(ServiceResultEnum.OTHER_ORG.getResult());
        }
        if (!CheckUtils.isTime(activity)) {//是否抽奖时段
            return ResultGenerator.genFailResult(ServiceResultEnum.OUT_DRAW_TIME.getResult());
        }
        if (activity.getPrizes().equals("") || activity.getPrizes() == null) {//奖池为空
            return ResultGenerator.genFailResult(ServiceResultEnum.PRIZES_NOT_EXIST.getResult());
        }
        if (activity.getStatus() != 0 ) {//活动不可用
            return ResultGenerator.genFailResult(ServiceResultEnum.ACTIVITY_PUT_DOWN.getResult());
        }
        //核对用户抽奖次数
        ActivityDraw activityDraw = activityService.getActivityDraws(loginMallUser.getUserId(),id);
        if (activityDraw == null) {
            //用户不在抽奖名单中
            return ResultGenerator.genFailResult(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        if (activityDraw.getDraws() <= 0) {
            //用户没有抽奖次数
            return ResultGenerator.genFailResult(ServiceResultEnum.ERROR_DRAW_TIMES.getResult());
        }
        //------------------核对完成--------------------

        //获得抽奖规则
        Rule rule = activityService.getRuleByActivityId(id);
        //获得奖品初始化
        ZhongHeMallPrize prizeResult;
        Long lotteryDrawId;

        //同步锁起点
        synchronized (this) {
            //获得奖品列表
            List<ZhongHeMallPrize> prizeList = getPrizeList(activity.getPrizes());
            int stocktotal = 0;
            for (ZhongHeMallPrize prize : prizeList) {
                stocktotal += prize.getStockNum();
            }
            //奖品池已空
            if (stocktotal == 0) return ResultGenerator.genFailResult(ServiceResultEnum.PRIZES_NOT_EXIST.getResult());

            //计算总权重
            double sumWeight = sumWeight(prizeList);
            //抽奖错误累计
            int drawCount = 0;
            //抽奖
            while (true) {
                ZhongHeMallPrize temp = draw(sumWeight, prizeList);
                if (temp == null) return ResultGenerator.genFailResult(ServiceResultEnum.ERROR_DRAW.getResult());
                logger.info("第 {} 次抽奖结果 :  {}", drawCount+1, temp.toString());

                //判定是否重新抽取
                if (temp.getStockNum() == 0) {
                    logger.info("奖品库存已空");
                } else {
                    logger.info("获得奖品:{}", temp.toString());
                    prizeResult = temp;

                    //奖品归档
                    lotteryDrawId = updateDrawResult(loginMallUser.getUserId(), activity, prizeResult);
                    if (lotteryDrawId != null) {
                        //归档成功，扣减抽奖次数，退出循环
                        if (activityService.updateActivityDraws(activityDraw.getId(),
                                activityDraw.getDraws() -1) > 0) {
                            logger.info("扣减抽奖次数完成");
                            break;
                        }
                        logger.info("扣减抽奖次数失败");
                    }
                    drawCount += 1;//出错时最多抽奖10次
                    logger.info("归档出错，次数:{}", drawCount);
                    //次数达到后
                    if (drawCount >= 10) {
//                    prizeResult=prizeList.get(prizeList.size()-1);//发参与奖
                        return ResultGenerator.genFailResult(ServiceResultEnum.ERROR_DRAW_MAX.getResult());
                    }
                }
            }//抽奖循环结束
        }//同步锁结束

        Map prizeInfo = new HashMap(8);
        prizeInfo.put("prize", prizeResult);
        prizeInfo.put("lotteryDrawId", lotteryDrawId);
        //获得中奖词
        String notice;
        Byte prizeLevel = prizeResult.getPrizeLevel();
        if (prizeLevel == 0) {
            notice = rule.getNoticeEmpty();
        }else if(prizeLevel == 1){
            notice = rule.getNoticeLv1();
        }else if(prizeLevel == 2){
            notice = rule.getNoticeLv2();
        }else if(prizeLevel == 3){
            notice = rule.getNoticeLv3();
        }else {
            notice = rule.getNoticeOther();
        }
        prizeInfo.put("notice", notice);
        return ResultGenerator.genSuccessResult(prizeInfo);
//        return ResultGenerator.genSuccessResult(prizeResult);
    }

    /**
     * 抽奖记录详情
     */
    @GetMapping("/lotterydraw/{id}")
    @ApiOperation(value = "获取单条抽奖记录", notes = "根据id查询")
    public Result info(@PathVariable("id") Long id, @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户获取单条抽奖记录接口    id:{}", id.toString());
//        Map lotterydrawInfo = new HashMap(8);
        LotteryDraw lotterydraw = lotterydrawService.getLotteryDrawById(id);
        if (lotterydraw == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        if (!CheckUtils.isSameId(loginMallUser.getUserId(),lotterydraw.getUserId())) {//用户ID不符合
            return ResultGenerator.genFailResult(ServiceResultEnum.OTHER_USER.getResult());
        }
//        logger.info("抽奖记录:{}", lotterydraw.toString());
        ZhongHeMallPrize prize = zhongHeMallPrizeService.getZhongHeMallPrizeById(lotterydraw.getPrizeId());
        LotteryDrawAddress address = lotterydrawService.getAddress(id);
        Map lotterydrawInfo = new HashMap(8);
        lotterydrawInfo.put("prize", prize);
        lotterydrawInfo.put("lotterydraw", lotterydraw);
        lotterydrawInfo.put("address", address);
//        lotterydrawInfo.put("lotterydraw", lotterydraw);
        return ResultGenerator.genSuccessResult(lotterydrawInfo);
    }

    @GetMapping("/lotterydraw/model/{id}")
    @ApiOperation(value = "获取活动的模板信息", notes = "根据id查询")
    public Result getModel(@PathVariable("id") Long id, @TokenToMallUser MallUser loginMallUser) {
        logger.info("获取活动配置的模板信息接口    id:{}", id.toString());
        Activity activity = activityService.getActivityById(id);
        String check = checkActivity(activity,id,loginMallUser);
        if (!check.equals(ServiceResultEnum.SUCCESS.getResult())) {
            return ResultGenerator.genFailResult(check);
        }
        Model model = activityService.getModelByActivityId(id);
        logger.info("模板信息:{}", model.toString());
//        adminLogService.addSuccessLog(adminUser,"获取活动配置的模板信息接口","id:"+id.toString(),"SUCCESS");
        return ResultGenerator.genSuccessResult(model);
    }

    /**
     * 奖池详情
     */
    @GetMapping("/lotterydraw/jackpot/{id}")
    @ApiOperation(value = "获取活动的奖池", notes = "根据id查询")
    public Result jackpot(@PathVariable("id") Long id, @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户获取活动的奖池接口    活动id:{}", id.toString());
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
//        logger.info("活动信息:{}", activity.toString());

        //获得奖品列表
        List<ZhongHeMallPrize> prizeList = getPrizeList(activity.getPrizes());

        List<ZhongHeMallPrizeDetailVO> prizeVOList=new ArrayList<>();

        for(ZhongHeMallPrize prize : prizeList){
            ZhongHeMallPrizeDetailVO tempVO = new ZhongHeMallPrizeDetailVO();
            BeanUtil.copyProperties(prize, tempVO);
            prizeVOList.add(tempVO);
        }
        return ResultGenerator.genSuccessResult(prizeVOList);

    }

    @PostMapping("/lotterydraw/address")
    @ApiOperation(value = "奖品添加地址", notes = "传参为地址id和抽奖记录id数组")
    public Result saveLotteryDraw(@ApiParam(value = "地址参数") @RequestBody SaveLotteryDrawParam saveLotteryDrawParam,
                                    @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户奖品添加地址接口  User:{}，saveOrderParam:{}", loginMallUser.toString(),saveLotteryDrawParam.toString());
        Long lotteryDrawId = saveLotteryDrawParam.getLotteryDrawId();
        LotteryDraw lotterydraw = lotterydrawService.getLotteryDrawById(lotteryDrawId);
        String check = check(lotterydraw,loginMallUser,"address");
        if (!"success".equals(check)) {
            return ResultGenerator.genFailResult(check);
        }
        MallUserAddress address = zhongHeMallUserAddressService.getMallUserAddressById(saveLotteryDrawParam.getAddressId());
        if (!loginMallUser.getUserId().equals(address.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.OTHER_USER.getResult());
        }
        //添加地址
        String result = lotterydrawService.saveAddress(lotteryDrawId,address);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @PostMapping("/lotterydraw/receiveVIP")
    @ApiOperation(value = "领取VIP卡", notes = "传参为抽奖记录id")
    public Result receiveVIP(@RequestParam @ApiParam(value = "抽奖记录id") Long lotteryDrawId,
                                  @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户领取VIP卡接口  User:{}，id:{}", loginMallUser.toString(),lotteryDrawId.toString());
//        Long lotteryDrawId = saveLotteryDrawParam.getLotteryDrawId();
        LotteryDraw lotterydraw = lotterydrawService.getLotteryDrawById(lotteryDrawId);
        String check = check(lotterydraw,loginMallUser,"receiveVIP");
        if (!"success".equals(check)) {
            return ResultGenerator.genFailResult(check);
        }
        //发出领取申请，改变记录状态
        String result = lotterydrawService.receiveVIP(lotteryDrawId);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    private String check(LotteryDraw lotterydraw,MallUser loginMallUser,String type){
        if (lotterydraw == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (!CheckUtils.isSameId(loginMallUser.getUserId(),lotterydraw.getUserId())) {//用户ID不符合
            return ServiceResultEnum.OTHER_USER.getResult();
        }
        if (lotterydraw.getExpireTime().before(new Date())) {//奖品过期
            return ServiceResultEnum.PRIZE_OUT_TIME.getResult();
        }
        if (lotterydraw.getStatus() != 1 && "receiveVIP".equals(type)) {//奖品已领取
            return ServiceResultEnum.PRIZE_GETED.getResult();
        }
        if (lotterydraw.getPrizeType() != 3 && "receiveVIP".equals(type)) {//非VIP卡类奖品
            return ServiceResultEnum.PRIZE_TYPE_ERROR.getResult();
        }
        return "success";
    }

    private String checkActivity(Activity activity,Long id,MallUser loginMallUser){
        if (activity == null) {
            logger.info("配置规则信息 错误:{}", ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult());
            return ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult();
        }
        Long organizationId = activity.getOrganizationId();
        if(!organizationId.equals(loginMallUser.getOrganizationId())){
            return ServiceResultEnum.OTHER_ORG.getResult();
        }
        return ServiceResultEnum.SUCCESS.getResult();
    }


        /**
         * 抽奖记录状态修改测试
         */
//    @RequestMapping(value = "/lotterydraw/status/{id}", method = RequestMethod.PUT)
//    @ApiOperation(value = "抽奖记录状态修改测试", notes = "抽奖记录状态修改")
//    public Result status(@PathVariable("id") Long id, @TokenToMallUser MallUser loginMallUser) {
////        lotterydrawService.received(id);
//        if (lotterydrawService.received(id)) {
//            logger.info("积分已添加到账户");
//            return ResultGenerator.genSuccessResult();
//        }else {
//            logger.info("积分添加失败");
//            return ResultGenerator.genFailResult(ServiceResultEnum.ERROR_DRAW.getResult());
//        }
//
//    }

    //抽奖--获得抽奖活动可抽奖品类列表
    private List<ZhongHeMallPrize> getPrizeList(String prizes){
//        String[] prizeIdList = (prizes.substring(1, prizes.length() - 1)).split(",");
        String[] prizeIdList = prizes.split(",");
        List<ZhongHeMallPrize> prizeList = new ArrayList<>();
        for (String s : prizeIdList) {
            Long prizeId = Long.valueOf(s);
            ZhongHeMallPrize prizetemp = zhongHeMallPrizeService.getZhongHeMallPrizeById(prizeId);
            if (prizetemp != null ) {
                prizeList.add(prizetemp);
            }
        }
        logger.info("奖品列表:{}", prizeList.toString());
        return prizeList;
    }

    //抽奖--计算权重
    private double sumWeight(List<ZhongHeMallPrize> prizeList){
        double sumWeight = 0;
        for (ZhongHeMallPrize prize : prizeList) {
            sumWeight += prize.getPrizeWeight()*prize.getStockNum();
//            sumWeight += prize.getPrizeWeight();
        }
//        logger.info("总权重:{}", sumWeight);
        List<Double> weightList = new ArrayList<Double>();
        weightList.add((double) 0);
        for (ZhongHeMallPrize prize : prizeList) {
//            double temp = Double.parseDouble(String.valueOf(prize.getPrizeWeight()*prize.getStockNum()))/sumWeight;
            double temp = Double.parseDouble(String.valueOf(prize.getPrizeWeight()))/sumWeight;
            weightList.add(temp);
        }
        logger.info("权重列表:{}", weightList.toString());
        return sumWeight;
    }

    //抽奖--计算随机数抽取奖品
    private ZhongHeMallPrize draw(double sumWeight,List<ZhongHeMallPrize> prizeList){

        //产生随机数
        double randomNumber;
        randomNumber = Math.random();

        //根据随机数在所有奖品分布的区域并确定所抽奖品
        double d1 = 0;
        double d2 = 0;

        DecimalFormat df = new DecimalFormat("######0.00");
        int random = -1;

        try{
            for(int i=0;i<prizeList.size();i++){
                d2 += Double.parseDouble(String.valueOf(prizeList.get(i).getPrizeWeight()))/sumWeight;
                if(i==0){
                    d1 = 0;
                }else{
                    d1 +=Double.parseDouble(String.valueOf(prizeList.get(i-1).getPrizeWeight()))/sumWeight;
                }
                if(randomNumber >= d1 && randomNumber <= d2){
                    random = i;
                    break;
                }
            }
        }catch(Exception e){
            System.out.println("生成抽奖随机数出错，出错原因：" +e.getMessage());
            return null;
        }
        logger.info("随机数:{}", randomNumber);

        return prizeList.get(random);
    }

    //抽奖--奖品结果归档
    private Long updateDrawResult(Long userId,Activity activity,ZhongHeMallPrize prizeResult){
        //更新到抽奖记录
        LotteryDraw lotteryDraw = lotterydrawService.updateDrawResult(userId,prizeResult,activity);
        if (lotteryDraw == null) {
            logger.info("添加到抽奖记录失败");
            return null;
        }
        logger.info("已添加到抽奖记录:{}",lotteryDraw.toString());

        //扣减用户抽奖次数

        //更新到用户背包
        Byte type = prizeResult.getPrizeType();
        if (type == 0) {
            //实物快递
            logger.info("奖品类型为实物（邮寄）");


        }else if (type ==1){
            //实物现场
            logger.info("奖品类型为实物（现场）");
            //设置抽奖记录状态为已完成
            lotterydrawService.received(lotteryDraw.getLotteryDrawId());

        }else if(type==2){
            //积分
            logger.info("奖品类型为积分");
            Integer point = prizeResult.getPrizeValue();
            Date expiretime = activity.getExpiretime();
            PointDTO pointDTO = new PointDTO();
            pointDTO.setPoint(point);
            pointDTO.setExpiretime(expiretime);
            if (userService.addPoint(userId, pointDTO)) {
                logger.info("积分已添加到账户");
            }else {
                logger.info("积分添加失败");
                return null;
            }
            //设置抽奖记录状态为已完成
            if (lotterydrawService.received(lotteryDraw.getLotteryDrawId())) {
                logger.info("设置抽奖记录状态为已完成");
            }else {
                logger.info("设置抽奖记录状态失败");
                return null;
            }
//            return lotterydrawService.received(lotteryDraw.getLotteryDrawId());

        }else if(type==3){
            //会员卡
            logger.info("奖品类型为会员卡");
            //抽奖记录状态为默认的 1：已抽奖
//            String result = lotterydrawService.receiveVIP(lotteryDraw.getLotteryDrawId());
//            return ServiceResultEnum.SUCCESS.getResult().equals(result);
//            if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
//                logger.info("设置抽奖记录状态为待发送");
//            }else {
//                logger.info("设置抽奖记录状态失败");
//                return null;
//            }
        }
        return lotteryDraw.getLotteryDrawId();
    }

}
