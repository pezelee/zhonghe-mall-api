package mall.dao;

import mall.entity.Log;
import org.apache.ibatis.annotations.Param;

public interface ZhongHeAdminLogMapper {
    int deleteByPrimaryKey(Long id);

    int deleteBatch(@Param("ids") Long[] ids);

    int insert(Log record);

    int insertSelective(Log record);

    Log selectByPrimaryKey(Long id);

    Log selectByOrganizationId(Long organizationId);

    int updateByPrimaryKeySelective(Log record);

    int updateByPrimaryKey(Log record);
}