/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.dao;

import mall.entity.ActivityDraw;
import mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ActivityDrawMapper {

    int batchInsert(@Param("ActivityDrawList") List<ActivityDraw> ActivityDrawList);

    int batchInsertDraws(@Param("ActivityDrawList") List<ActivityDraw> ActivityDrawList);

    ActivityDraw selectByPrimaryKey(Long lotteryDrawId);

    ActivityDraw selectByKeyIds(@Param("activityId") Long activityId, @Param("userId") Long userId);
//
    int updateDrawsByPrimaryKey(@Param("id") Long id, @Param("draws") int draws);

    int batchUpdateStatus(@Param("lotteryDrawIds")Long[] lotteryDrawIds,@Param("status") int status);

    int deleteByPrimaryKey(Long lotteryDrawId);

    int insert(ActivityDraw record);

    int insertSelective(ActivityDraw record);

    int updateByPrimaryKeySelective(ActivityDraw record);

    int updateDrawsByPrimaryKey(ActivityDraw record);

    List<ActivityDraw> findActivityDrawList(PageQueryUtil pageUtil);

    int getTotalActivityDraw(PageQueryUtil pageUtil);

    List<ActivityDraw> findActivityDrawListByOrg(PageQueryUtil pageUtil);

    int getTotalActivityDrawByOrg(PageQueryUtil pageUtil);

    List<ActivityDraw> findActivityDrawListByUserId(PageQueryUtil pageUtil);

    int getTotalActivityDrawByUserId(PageQueryUtil pageUtil);

    int drawOnce( ActivityDraw record);
}