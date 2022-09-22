package mall.dao;

import mall.entity.Rule;

public interface RuleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Rule record);

    int insertSelective(Rule record);

    Rule selectByPrimaryKey(Long id);

    Rule selectByActivityId(Long activityId);

    int updateByPrimaryKeySelective(Rule record);

    int updateByPrimaryKey(Rule record);
}