package mall.dao;

import mall.entity.AdminUserToken;
import org.apache.ibatis.annotations.Param;

public interface ZhongHeAdminUserTokenMapper {
    int deleteByPrimaryKey(Long userId);

    int deleteBatch(@Param("ids") Long[] ids);

    int insert(AdminUserToken record);

    int insertSelective(AdminUserToken record);

    AdminUserToken selectByPrimaryKey(Long userId);

    AdminUserToken selectByToken(String token);

    int updateByPrimaryKeySelective(AdminUserToken record);

    int updateByPrimaryKey(AdminUserToken record);
}