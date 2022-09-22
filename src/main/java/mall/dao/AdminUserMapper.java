package mall.dao;

import mall.entity.AdminUser;
import mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminUserMapper {
    int insert(AdminUser record);

    int insertSelective(AdminUser record);

    /**
     * 登陆方法
     *
     * @param userName
     * @param password
     * @return
     */
    AdminUser login(@Param("userName") String userName, @Param("password") String password);

    AdminUser selectByPrimaryKey(Long adminUserId);

    AdminUser selectByLoginName(String loginName);

    AdminUser selectBySponsor(String sponsor);

    int updateByPrimaryKeySelective(AdminUser record);

    int updateByPrimaryKey(AdminUser record);

    int lockByPrimaryKey(Long userId);

    int lockUserBatch(@Param("ids") Long[] ids, @Param("lockStatus") Byte lockStatus);

    List<AdminUser> findMallAdminList(PageQueryUtil pageUtil);

    int getTotalMallAdmins(PageQueryUtil pageUtil);

    List<AdminUser> findMallAdminListByOrg(PageQueryUtil pageUtil);

    int getTotalMallAdminsByOrg(PageQueryUtil pageUtil);
}