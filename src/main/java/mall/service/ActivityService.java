/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.service;

import mall.entity.Activity;
import mall.entity.ActivityDraw;
import mall.entity.Model;
import mall.entity.Rule;
import mall.util.PageQueryUtil;
import mall.util.PageResult;

import java.util.List;

public interface ActivityService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getActivityPage(PageQueryUtil pageUtil);

    /**
     * 添加活动
     *
     * @param activity
     * @return
     */
    String saveActivity(Activity activity);



    /**
     * 修改活动信息
     *
     * @param activity
     * @return
     */
    String updateActivity(Activity activity);

    /**
     * 批量修改状态(上架下架)
     *
     * @param ids
     * @param status
     * @return
     */
    Boolean batchUpdateStatus(Long[] ids, int status, Long adminId);

    /**
     * 批量添加抽奖次数
     *
     * @param ids 用户ID组
     * @param activityId 活动ID
     * @param times 抽奖次数
     * @return Boolean
     */
    Boolean batchInsertDraws(Long[] ids, Long activityId, int times, Long adminId);

    /**
     * 活动用户可抽奖次数列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getActivityDrawsPage(PageQueryUtil pageUtil);

    /**
     * 查询活动用户可抽奖次数
     *
     * @param userId
     * @param activityId
     * @return
     */
    ActivityDraw getActivityDraws(Long userId, Long activityId);

    /**
     * 修改活动用户可抽奖次数
     *
     * @param id
     * @param times
     * @return
     */
    int updateActivityDraws(Long id, int times);

    /**
     * 获取活动详情
     *
     * @param id
     * @return
     */
    Activity getActivityById(Long id);

    /**
     * 根据组织获取活动列表
     *
     * @param orgId 组织ID
     * @return
     */
    List<Activity> getActivityList(Long orgId);

    /**
     * 获取规则详情
     *
     * @param activityId
     * @return
     */
    Rule getRuleByActivityId(Long activityId);

    /**
     * 获取模板详情
     *
     * @param activityId
     * @return
     */
    Model getModelByActivityId(Long activityId);

    /**
     * 修改抽奖规则
     *
     * @param rule
     * @return
     */
    String updateRule(Rule rule);

    /**
     * 修改模板规则
     *
     * @param model
     * @return
     */
    String updateModel(Model model);

    /**
     * 保存抽奖规则
     *
     * @param activity
     * @param rule
     * @return
     */
    String saveRule(Activity activity, Rule rule);

    /**
     * 批量新增活动数据
     *
     * @param activities
     * @return
     */
//    void batchSaveActivity(List<Activity> activities);

    /**
     * 活动搜索
     *
     * @param pageUtil
     * @return
     */
//    PageResult searchActivity(PageQueryUtil pageUtil);
}
