
package mall.service.impl;

import mall.api.admin.param.BatchPrizesParam;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallException;
import mall.dao.*;
import mall.entity.*;
import mall.service.ActivityService;
import mall.util.CheckUtils;
import mall.util.PageQueryUtil;
import mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private ActivityDrawMapper activityDrawMapper;
    @Autowired
    private ZhongHeMallPrizeMapper prizeMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PageResult getActivityPage(PageQueryUtil pageUtil) {
        List<Activity> activityList;
        int total;
        byte role = (byte) pageUtil.get("role");
        if (role == 0) {
            //总管理员
            activityList = activityMapper.findActivityList(pageUtil);
            total = activityMapper.getTotalActivity(pageUtil);
        }else {
            //分行人员
            activityList = activityMapper.findActivityListByOrg(pageUtil);
            total = activityMapper.getTotalActivityByOrg(pageUtil);
        }
        PageResult pageResult = new PageResult(activityList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveActivity(Activity activity) {

        if (activityMapper.selectByOrganizationAndName(activity.getActivityName(), activity.getOrganizationId()) != null) {
            return ServiceResultEnum.SAME_ACTIVITY_EXIST.getResult();
        }
        activity.setUpdateTime(new Date());
        activity.setCreateTime(new Date());
        if (activityMapper.insertSelective(activity) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateActivity(Activity activity) {

        Activity temp = activityMapper.selectByPrimaryKey(activity.getActivityId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        Activity temp2 = activityMapper.selectByOrganizationAndName(activity.getActivityName(), activity.getOrganizationId());
        if (temp2 != null && !temp2.getActivityId().equals(activity.getActivityId())) {
            //name和分行id相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_ACTIVITY_EXIST.getResult();
        }

        activity.setUpdateTime(new Date());
        if (activityMapper.updateByPrimaryKeySelective(activity) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updatePrizes(Activity activity, BatchPrizesParam activityEditParam) {

        Long[] newPrizeIds = activityEditParam.getPrizeIds();
        StringBuilder newPrizes = new StringBuilder();
        //检验新配置奖池，生成新奖池字段
        if (newPrizeIds.length != 0 ) {
            for (Long newPrizeId : newPrizeIds) {
                ZhongHeMallPrize temp = prizeMapper.selectByPrimaryKey(newPrizeId);
                if (temp == null) {//奖品不存在
                    return ServiceResultEnum.PRIZE_NOT_EXIST.getResult();
                }
                if (temp.getPrizeSellStatus() == 1) {//奖品已下架
                    return ServiceResultEnum.PRIZE_PUT_DOWN.getResult();
                }
                if (temp.getActivityId() != 0 && !temp.getActivityId().equals(activity.getActivityId())) {//奖品已使用
                    return ServiceResultEnum.PRIZE_USED.getResult();
                }
                if (!temp.getOrganizationId().equals(activity.getOrganizationId())) {//奖品属于其他组织
                    return ServiceResultEnum.PRIZE_OTHER_ORG.getResult();
                }
                if (!newPrizes.toString().equals("")) {
                    newPrizes.append(",");
                }
                newPrizes.append(newPrizeId);
            }
        }else newPrizes.append("");
//        }else return ServiceResultEnum.PARAM_ERROR.getResult();
        //清理旧奖池
        String oldPrizes=activity.getPrizes();
        if (!("".equals(oldPrizes) || oldPrizes ==null)) {
            String[] oldPrizeIdList;
            oldPrizeIdList = oldPrizes.split(",");
            Long[] temp = new Long[oldPrizeIdList.length];
            for (int i = 0; i < oldPrizeIdList.length; i++) {
                temp[i]= Long.valueOf(oldPrizeIdList[i]);
            }
            //旧奖池清空活动ID
//            prizeMapper.batchUpdateActivityId(temp, (long) 0,activity.getUpdateUser());
            if (!(prizeMapper.batchUpdateActivityId(temp, (long) 0,activity.getUpdateUser())>0)){
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        //设置新奖池奖品
        if (newPrizeIds.length  != 0) {
            if (!(prizeMapper.batchUpdateActivityId(newPrizeIds, activity.getActivityId(),activity.getUpdateUser())>0)){
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        //修改活动奖池字段
        activity.setPrizes(newPrizes.toString());
        activity.setUpdateTime(new Date());
        if (activityMapper.updateByPrimaryKeySelective(activity) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Activity getActivityById(Long id) {
        Activity activity = activityMapper.selectByPrimaryKey(id);
        if (activity == null) {
            ZhongHeMallException.fail(ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult());
        }
        return activity;
    }

    @Override
    public Boolean batchUpdateStatus(Long[] ids, int status, Long adminId) {
        return activityMapper.batchUpdateStatus(ids, status, adminId) > 0;
    }

    @Override
    public Boolean batchInsertDraws(Long[] ids, Long activityId, int times, Long adminId) {
        List<ActivityDraw> ActivityDrawList = new ArrayList<>();
        Activity activity = activityMapper.selectByPrimaryKey(activityId);
        if (activity == null) {
            ZhongHeMallException.fail(ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult());
        }
        for(Long userId : ids){
            ActivityDraw activityDraw = activityDrawMapper.selectByKeyIds(activityId,userId);
            if (activityDraw == null) {//新增人员抽奖
                ActivityDraw temp = new ActivityDraw();
                temp.setUserId(userId);
                temp.setActivityId(activityId);
                temp.setOrganizationId(activity.getOrganizationId());
                temp.setDraws(times);
                temp.setCreateTime(new Date());
                temp.setCreateUser(adminId);
                temp.setUpdateTime(new Date());
                temp.setUpdateUser(adminId);
                ActivityDrawList.add(temp);
            }else {     //修改旧抽奖次数
                if(updateActivityDraws(activityDraw.getId(),times)<=0){
                    ZhongHeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
                }
            }
        }
        if(ActivityDrawList.size()>0){
            return activityDrawMapper.batchInsertDraws(ActivityDrawList) > 0;
        }else {
            return true;
        }
    }

    @Override
    public PageResult getActivityDrawsPage(PageQueryUtil pageUtil) {

//        Long activityId = (Long) pageUtil.get("activityId");
//        Long organizationId = (Long) pageUtil.get("organizationId");
//        Activity activity = activityMapper.selectByPrimaryKey(activityId);
        List<ActivityDraw> ActivityDrawList;
        int total;
        ActivityDrawList = activityDrawMapper.findActivityDrawList(pageUtil);
        total = activityDrawMapper.getTotalActivityDraw(pageUtil);
        PageResult pageResult = new PageResult(ActivityDrawList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public ActivityDraw getActivityDraws(Long userId, Long activityId){
        ActivityDraw activityDraw = activityDrawMapper.selectByKeyIds(activityId,userId);
        return activityDraw;
    }

    @Override
    public List<Activity> getActivityList(Long orgId){
        List<Activity> activityList = activityMapper.findAllActivityListByOrg(orgId);
        return activityList;
    }
//
    @Override
    public int updateActivityDraws(Long id, int times){
        int a = activityDrawMapper.updateDrawsByPrimaryKey(id,times);
        return a;
    }

    @Override
    public Rule getRuleByActivityId(Long activityId) {
        Activity activity = activityMapper.selectByPrimaryKey(activityId);
        Rule rule = ruleMapper.selectByActivityId(activityId);
        if (rule == null) {
            ZhongHeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        getPeriod(rule, activity);
        return rule;
    }

    @Override
    public Model getModelByActivityId(Long activityId) {
//        Activity activity = activityMapper.selectByPrimaryKey(activityId);
        Model model = modelMapper.selectByActivityId(activityId);
        if (model == null) {
            ZhongHeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return model;
    }

    @Override
    public String updateModel(Model model) {
        Activity activity = activityMapper.selectByPrimaryKey(model.getActivityId());
        if (activity == null) {
            return ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult();
        }
        Model temp = modelMapper.selectByActivityId(model.getActivityId());
        model.setActivityName(activity.getActivityName());
        model.setUpdateTime(new Date());
        if (temp == null) {//初次保存
            model.setCreateTime(new Date());
            model.setCreateUser(model.getUpdateUser());
            if (modelMapper.insertSelective(model)>0) {
                activity.setTemplate(model.getId());
                activityMapper.updateByPrimaryKeySelective(activity);
                return ServiceResultEnum.SUCCESS.getResult();
            }
        }else {
            if (temp.getId().equals(model.getId())) {
                //传参的ID和数据库里的不同
                model.setId(temp.getId());
            }
            if (modelMapper.updateByPrimaryKeySelective(model) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
        }
        return ServiceResultEnum.SAVE_RULE_ERROR.getResult();
    }

    @Override
    public String updateRule(Rule rule) {
        Activity activity = activityMapper.selectByPrimaryKey(rule.getActivityId());
        if (activity == null) {
            return ServiceResultEnum.ACTIVITY_NOT_EXIST.getResult();
        }
        //第一阶段和第二阶段
        rule.setPeriod1st(period(rule.getPeriod1stDate(),rule.getPeriod1stNum1(),rule.getPeriod1stNum2(),rule.getPeriod1stNum3()));
        rule.setPeriod2nd(period(rule.getPeriod2ndDate(),rule.getPeriod2ndNum1(),rule.getPeriod2ndNum2(),rule.getPeriod2ndNum3()));
        //检测规则是否规范
        String check = CheckUtils.isRule(rule,activity);
        if (!check.equals("success")) {
            return check;
        }
        rule.setActivityName(activity.getActivityName());
        rule.setUpdateTime(new Date());
        Rule temp = ruleMapper.selectByActivityId(rule.getActivityId());
        if (temp == null) {//初次保存规则
            rule.setCreateTime(new Date());
            rule.setCreateUser(rule.getUpdateUser());
            if (ruleMapper.insertSelective(rule)>0) {
                activity.setRule(rule.getId());
                activityMapper.updateByPrimaryKeySelective(activity);
                return ServiceResultEnum.SUCCESS.getResult();
            }
        }else {
            if (temp.getId().equals(rule.getId())) {
                //传参的ID和数据库里的不同
                rule.setId(temp.getId());
            }
            if (ruleMapper.updateByPrimaryKeySelective(rule) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
        }
        return ServiceResultEnum.SAVE_RULE_ERROR.getResult();
    }

    private String period (Date date,Integer num1,Integer num2,Integer num3){
        String period ="";
        if (date != null) {
            period = period +date.toString();
        }
        if (num1 != null) {
            period = period + "," + num1.toString();
        }
        if (num2 != null) {
            period = period + "," + num2.toString();
        }
        if (num3 != null) {
            period = period + "," + num3.toString();
        }
        return period;
    }

    private void getPeriod (Rule rule, Activity activity){
        String[] period1List;
        if (rule.getPeriod1st() == null) {
            return;//第一阶段为空，直接返回
        }else {
            period1List = rule.getPeriod1st().split(",");
        }
        String[] period2List = new String[0];
        if (rule.getPeriod2nd() != null) {
            period2List = rule.getPeriod2nd().split(",");
        }
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Date periodtime1;
        Date periodtime2;
        try {
            periodtime1 = format.parse(period1List[0]);
            if (periodtime1.after(activity.getEndtime()) || periodtime1.before(activity.getStarttime())) {
                return;//第一阶段日期错误，直接返回
            }
            rule.setPeriod1stDate(periodtime1);
            if (period1List[1] != null && !period1List[1].matches("^[0-9]+$")) {
                rule.setPeriod1stNum1(Integer.valueOf(period1List[1]));
            }
            if (period1List[2] != null && !period1List[2].matches("^[0-9]+$")) {
                rule.setPeriod1stNum2(Integer.valueOf(period1List[2]));
            }
            if (period1List[3] != null && !period1List[3].matches("^[0-9]+$")) {
                rule.setPeriod1stNum3(Integer.valueOf(period1List[3]));
            }
            if (period2List.length>0) {
                periodtime2 = format.parse(period2List[0]);
                if (periodtime2.after(activity.getEndtime()) || periodtime2.before(periodtime1)) {
                    return;//第二阶段日期错误，直接返回
                }
                rule.setPeriod2ndDate(periodtime2);
                if (period2List[1] != null && !period2List[1].matches("^[0-9]+$")) {
                    rule.setPeriod2ndNum1(Integer.valueOf(period2List[1]));
                }
                if (period2List[2] != null && !period2List[2].matches("^[0-9]+$")) {
                    rule.setPeriod2ndNum2(Integer.valueOf(period2List[2]));
                }
                if (period2List[3] != null && !period2List[3].matches("^[0-9]+$")) {
                    rule.setPeriod2ndNum3(Integer.valueOf(period2List[3]));
                }
            }

        } catch (ParseException ignored) {
        }
    }

    @Override
    public String saveRule(Activity activity, Rule rule) {
        //第一阶段和第二阶段
        rule.setPeriod1st(period(rule.getPeriod1stDate(),rule.getPeriod1stNum1(),rule.getPeriod1stNum2(),rule.getPeriod1stNum3()));
        rule.setPeriod2nd(period(rule.getPeriod2ndDate(),rule.getPeriod2ndNum1(),rule.getPeriod2ndNum2(),rule.getPeriod2ndNum3()));
        //检测规则是否规范
        String check = CheckUtils.isRule(rule,activity);
        if (!check.equals("success")) {
            return check;
        }
        rule.setActivityName(activity.getActivityName());
        rule.setCreateTime(new Date());
        rule.setUpdateTime(new Date());
        if (ruleMapper.insertSelective(rule)>0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.SAVE_RULE_ERROR.getResult();
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
