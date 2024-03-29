package mall.service.impl;

import mall.api.admin.param.LotterydrawMailParam;
import mall.api.admin.param.NoticeAddParam;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallException;
import mall.dao.*;
import mall.entity.*;
import mall.entity.excel.ExportLotterydraw;
import mall.entity.excel.ImportLotterydraw;
import mall.service.LotterydrawService;
import mall.service.NoticeService;
import mall.util.BeanUtil;
import mall.util.PageQueryUtil;
import mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class LotterydrawServiceImpl implements LotterydrawService {

    @Autowired
    private LotteryDrawMapper lotteryDrawMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ZhongHeMallPrizeMapper prizeMapper;

    @Autowired
    private MallUserMapper mallUserMapper;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private LotteryDrawAddressMapper lotteryDrawAddressMapper;

    @Resource
    private NoticeService noticeService;

    @Override
    public PageResult getLotteryDrawPage(PageQueryUtil pageUtil) {
        List<LotteryDraw> lotteryDrawList;
        int total;
        byte role = (byte) pageUtil.get("role");
//        Integer all = (int) pageUtil.get("all");//本次活动所有人抽奖记录列表
        if (role == 0) {
            //总管理员
            lotteryDrawList = lotteryDrawMapper.findLotteryDrawList(pageUtil);
            total = lotteryDrawMapper.getTotalLotteryDraw(pageUtil);
        }else if(role == -1){
            //客户个人中心抽奖记录列表
            lotteryDrawList = lotteryDrawMapper.findLotteryDrawListByUserId(pageUtil);
            total = lotteryDrawMapper.getTotalLotteryDrawByUserId(pageUtil);
        }else if(role == -2){
            //本次活动所有人抽奖记录列表
            lotteryDrawList = lotteryDrawMapper.findLotteryDrawListAllUser(pageUtil);
            total = lotteryDrawMapper.getTotalLotteryDrawAllUser(pageUtil);        }
        else {
            //分行人员
            lotteryDrawList = lotteryDrawMapper.findLotteryDrawListByOrg(pageUtil);
            total = lotteryDrawMapper.getTotalLotteryDrawByOrg(pageUtil);
        }
        PageResult pageResult = new PageResult(lotteryDrawList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public PageResult getPointList(PageQueryUtil pageUtil) {
        List<LotteryDraw> lotteryDrawList;
        int total;
        lotteryDrawList = lotteryDrawMapper.getPointList(pageUtil);
        total = lotteryDrawMapper.getTotalPointList(pageUtil);
        PageResult pageResult = new PageResult(lotteryDrawList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public List<ExportLotterydraw> getLotteryDrawExport(PageQueryUtil pageUtil) {
        return lotteryDrawMapper.findLotteryDrawExport(pageUtil);
    }

    @Override
    public String saveLotteryDraw(LotteryDraw lotteryDraw) {

        lotteryDraw.setUpdateTime(new Date());
        lotteryDraw.setCreateTime(new Date());
        if (lotteryDrawMapper.insertSelective(lotteryDraw) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public LotteryDraw getLotteryDrawById(Long id) {
        LotteryDraw lotteryDraw = lotteryDrawMapper.selectByPrimaryKey(id);
        if (lotteryDraw == null) {
            ZhongHeMallException.fail(ServiceResultEnum.DRAW_NOT_EXIST.getResult());
        }
        return lotteryDraw;
    }

    @Override
    public LotteryDraw updateDrawResult(Long userId, ZhongHeMallPrize prize, Activity activity) {

        MallUser mallUser = mallUserMapper.selectByPrimaryKey(userId);
        if (mallUser==null) {
            return null;
        }
        Organization organization = organizationMapper.selectByPrimaryKey(activity.getOrganizationId());

        //奖品库存-1
        PrizeStockNumDTO prizeStockNum = new PrizeStockNumDTO();
        prizeStockNum.setPrizeId(prize.getPrizeId());
        prizeStockNum.setPrizeCount(1);
        boolean updateStockNumResult = prizeMapper.updateStockNum(prizeStockNum);
        if (!updateStockNumResult) {
            return null;
        }
        //添加抽奖记录
        LotteryDraw lotteryDraw=new LotteryDraw();
        lotteryDraw.setUserId(mallUser.getUserId());
        lotteryDraw.setLoginName(mallUser.getLoginName());
        lotteryDraw.setNickName(mallUser.getNickName());
//        lotteryDraw.setSponsor(mallUser.getSponsor());
        lotteryDraw.setOrganizationId(organization.getOrganizationId());
        lotteryDraw.setOrgName(organization.getOrgName());
        lotteryDraw.setActivityId(activity.getActivityId());
        lotteryDraw.setActivityName(activity.getActivityName());
        lotteryDraw.setDrawTime(new Date());
        lotteryDraw.setExpireTime(activity.getExpiretime());
        lotteryDraw.setStatus((byte) 1);//状态设为已抽奖
        lotteryDraw.setPrizeId(prize.getPrizeId());
        lotteryDraw.setPrizeName(prize.getPrizeName());
        lotteryDraw.setPrizeValue(prize.getPrizeValue());
        lotteryDraw.setPrizeType(prize.getPrizeType());
        lotteryDraw.setCreateTime(new Date());
        lotteryDraw.setCreateUser(userId);
        lotteryDraw.setUpdateTime(new Date());
        lotteryDraw.setUpdateUser(userId);
        if (lotteryDrawMapper.insert(lotteryDraw)> 0) {
            return lotteryDraw;
        }
        return null;

    }

    @Override
    public Boolean batchUpdateStatus(Long[] ids, int status, Long adminId) {
        return activityMapper.batchUpdateStatus(ids, status, adminId) > 0;
    }

    @Override
    public String updateLotteryDraw(LotteryDraw lotteryDraw) {

        Activity temp = activityMapper.selectByPrimaryKey(lotteryDraw.getActivityId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        Activity temp2 = activityMapper.selectByOrganizationAndName(lotteryDraw.getActivityName(), lotteryDraw.getOrganizationId());
        if (temp2 != null && !temp2.getActivityId().equals(lotteryDraw.getActivityId())) {
            //name和分行id相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_ACTIVITY_EXIST.getResult();
        }
        lotteryDraw.setUpdateTime(new Date());
        if (lotteryDrawMapper.updateByPrimaryKeySelective(lotteryDraw) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public PageResult getLotteryDrawByUserId(PageQueryUtil pageUtil) {
        List<LotteryDraw> lotteryDrawList = lotteryDrawMapper.findLotteryDrawListByUserId(pageUtil);
        int total = lotteryDrawMapper.getTotalLotteryDrawByUserId(pageUtil);
        PageResult pageResult = new PageResult(lotteryDrawList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String receiveVIP(Long lotteryDrawId) {
        if (lotteryDrawMapper.toBeSent(lotteryDrawId)>0) {//从已抽奖变成待发送
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOTTERY_STATUS_ERROR.getResult();
    }

    @Override
    public String saveAddress(Long id, MallUserAddress address) {
        //保存邮寄地址
        LotteryDrawAddress lotteryDrawAddress = new LotteryDrawAddress();
        BeanUtil.copyProperties(address, lotteryDrawAddress);
        lotteryDrawAddress.setLotteryDrawId(id);
//        lotteryDrawAddressMapper.insertSelective(lotteryDrawAddress);
        if (lotteryDrawAddressMapper.insertSelective(lotteryDrawAddress)>0) {
            //状态更改为待发送
            if (lotteryDrawMapper.toBeSent(id)>0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.LOTTERY_STATUS_ERROR.getResult();
        }
        return ServiceResultEnum.SAVE_ADDRESS_ERROR.getResult();
    }

    //查询奖品邮寄地址
    @Override
    public LotteryDrawAddress getAddress(Long id){
        LotteryDrawAddress lotteryDrawAddress = lotteryDrawAddressMapper.selectByPrimaryKey(id);
        return lotteryDrawAddress;
    }

    @Override
    public String sending(LotterydrawMailParam param) {
        LotteryDraw temp =lotteryDrawMapper.selectByPrimaryKey(param.getLotteryDrawId());
        if (temp != null && temp.getStatus() >= 2 && temp.getStatus() <= 3) {
            LotteryDraw temp2 =lotteryDrawMapper.selectByMailNo(param.getMailNo());
            if (temp2 != null) {
                return ServiceResultEnum.MAIL_NO_EXIST.getResult();
            }
            LotteryDrawMail mail = new LotteryDrawMail();
            mail.setMailNo(param.getMailNo());
            mail.setLotteryDrawId(param.getLotteryDrawId());
            mail.setUpdateTime(new Date());
            int lotteryDraw = lotteryDrawMapper.sending(mail);
            if (lotteryDraw > 0) {
                String mailresult = mailNoNotice(temp,param.getMailNo());//发送通知
                return ServiceResultEnum.SUCCESS.getResult();
            }
        }
        return ServiceResultEnum.MAIL_NO_ERROR.getResult();
    }

    @Override
    public Boolean sendOver(Long id) {
        int lotteryDraw = lotteryDrawMapper.sendOver(id);
        return lotteryDraw > 0;
    }

    @Override
    public Boolean received(Long id) {
        int lotteryDraw = lotteryDrawMapper.received(id);
        return lotteryDraw > 0;
    }

    @Override
    public String setMailNoImport(ImportLotterydraw lotterydraw) {
        LotteryDraw temp =lotteryDrawMapper.selectByPrimaryKey(lotterydraw.getLotteryDrawId());
        if (temp != null && temp.getStatus() >= 2 && temp.getStatus() <= 3) {
            if(temp.getPrizeType() != 0) return ServiceResultEnum.PRIZE_TYPE_ERROR.getResult();
            LotteryDraw temp2 =lotteryDrawMapper.selectByMailNo(lotterydraw.getMailNo());
            if (temp2 != null) {
                return ServiceResultEnum.SAME_MAIL_NO.getResult();
            }
            temp.setUpdateTime(new Date());
            temp.setStatus((byte) 3);
            temp.setMailNo(lotterydraw.getMailNo());
            if (lotteryDrawMapper.updateByPrimaryKeySelective(temp) > 0) {
                String mailresult = mailNoNotice(temp,lotterydraw.getMailNo());//发送通知
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    private String mailNoNotice(LotteryDraw lotterydraw,String mailNo){
        NoticeAddParam addParam = new NoticeAddParam();
        addParam.setTitle("您的奖品已发出");
        addParam.setSender("奖品发放中心");
//        addParam.setNotice("您在"+ lotterydraw.getActivityName() +"活动中获得的 " + lotterydraw.getPrizeName() +
//                " 已发出，邮寄单号: "+mailNo+"，请注意收取。");
        addParam.setNotice("您在 #{notice1} 活动中获得的 #{notice2} 已发出，邮寄单号: #{notice3} ，请注意收取。");
        addParam.setNotice1(lotterydraw.getActivityName());
        addParam.setNotice2(lotterydraw.getPrizeName());
        addParam.setNotice3(mailNo);
        addParam.setNoticeType((byte)0);
        String result = noticeService.saveNotice(addParam,lotterydraw.getUserId());
        return result;
    }



//    @Override
//    public void batchSaveActivity(List<Activity> ActivityList) {
//        if (!CollectionUtils.isEmpty(ActivityList)) {
//            prizeMapper.batchInsert(ActivityList);
//        }
//    }
//
//    @Override
//    public PageResult searchActivity(PageQueryUtil pageUtil) {
//        List<Activity> prizeList = prizeMapper.findActivityListBySearch(pageUtil);
//        int total = prizeMapper.getTotalActivityBySearch(pageUtil);
//        List<ZhongHeMallSearchPrizeVO> zhongHeMallSearchPrizeVOS = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(prizeList)) {
//            zhongHeMallSearchPrizeVOS = BeanUtil.copyList(prizeList, ZhongHeMallSearchPrizeVO.class);
//            for (ZhongHeMallSearchPrizeVO zhongHeMallSearchPrizeVO : zhongHeMallSearchPrizeVOS) {
//                String goodsName = zhongHeMallSearchPrizeVO.getPrizeName();
//                String goodsIntro = zhongHeMallSearchPrizeVO.getPrizeIntro();
//                // 字符串过长导致文字超出的问题
//                if (goodsName.length() > 28) {
//                    goodsName = goodsName.substring(0, 28) + "...";
//                    zhongHeMallSearchPrizeVO.setPrizeName(goodsName);
//                }
//                if (goodsIntro.length() > 30) {
//                    goodsIntro = goodsIntro.substring(0, 30) + "...";
//                    zhongHeMallSearchPrizeVO.setPrizeIntro(goodsIntro);
//                }
//            }
//        }
//        PageResult pageResult = new PageResult(zhongHeMallSearchPrizeVOS, total, pageUtil.getLimit(), pageUtil.getPage());
//        return pageResult;
//    }
}
