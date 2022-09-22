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