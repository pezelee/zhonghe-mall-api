/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
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