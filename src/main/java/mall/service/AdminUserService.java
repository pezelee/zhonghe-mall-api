
package mall.service;

import mall.entity.AdminUser;
import mall.util.PageQueryUtil;
import mall.util.PageResult;

public interface AdminUserService {

    /**
     * 登录
     * @param userName
     * @param password
     * @return
     */
    String login(String userName, String password);

    /**
     * 获取用户信息
     *
     * @param loginUserId
     * @return
     */
    AdminUser getUserDetailById(Long loginUserId);

    /**
     * 修改当前登录用户的密码
     *
     * @param loginUserId
     * @param originalPassword
     * @param newPassword
     * @return
     */
    Boolean updatePassword(Long loginUserId, String originalPassword, String newPassword);

    /**
     * 修改当前登录用户的名称信息
     *
     * @param loginUserId
     * @param loginUserName
     * @param nickName
     * @return
     */
    String updateName(Long loginUserId, String loginUserName, String nickName);

    /**
     * 登出接口
     * @param adminUserId
     * @return
     */
    Boolean logout(Long adminUserId);


    /**
     * 锁定接口
     * @param adminUserId
     * @return
     */
    String deleteById(Long adminUserId);

    /**
     * 批量锁定接口
     * @param ids
     * @param lockStatus
     * @return
     */
    Boolean deleteBatch(Long[] ids, Byte lockStatus);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getZhongHeMallAdminsPage(PageQueryUtil pageUtil);

    /**
     * 新增后台管理员
     *
     * @param adminUser
     * @return
     */
    String saveAdmin(AdminUser adminUser);
}
