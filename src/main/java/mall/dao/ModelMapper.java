
package mall.dao;

import mall.entity.Model;

public interface ModelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Model record);

    int insertSelective(Model record);

    Model selectByPrimaryKey(Long id);

    Model selectByActivityId(Long activityId);

    int updateByPrimaryKeySelective(Model record);

    int updateByPrimaryKey(Model record);
}