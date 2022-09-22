package mall.dao;

import mall.entity.Activity;
import mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ActivityMapper {
    int closeByPrimaryKey(Long activityId);

    int openByPrimaryKey(Long activityId);

    int insert(Activity record);

    int insertSelective(Activity record);

    Activity selectByPrimaryKey(Long activityId);

    Activity selectByOrganizationAndName(@Param("activityName") String activityName, @Param("organizationId") Long organizationId);

    int updateByPrimaryKeySelective(Activity record);

    int updateByPrimaryKeyWithBLOBs(Activity record);

    int updateByPrimaryKey(Activity record);

    List<Activity> findAllActivityListByOrg(@Param("organizationId") Long organizationId);

    List<Activity> findActivityList(PageQueryUtil pageUtil);

    int getTotalActivity(PageQueryUtil pageUtil);

    List<Activity> findActivityListByOrg(PageQueryUtil pageUtil);

    int getTotalActivityByOrg(PageQueryUtil pageUtil);

    List<Activity> selectByPrimaryKeys(List<Long> activityIds);

    List<Activity> findActivityListBySearch(PageQueryUtil pageUtil);

    int getTotalActivityBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("activityList") List<Activity> activityList);

//    int updateStockNum(@Param("prizeStockNumDTOS") List<PrizeStockNumDTO> prizeStockNumDTOS);

    int batchUpdateStatus(@Param("activityIds")Long[] activityIds,@Param("status") int status,@Param("adminId") Long adminId);

}