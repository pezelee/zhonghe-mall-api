
package mall.service;

import mall.entity.AdminUserToken;

public interface AdminLogService {

    /**
     * 登录
     *  @param adminUserToken @return
     * @param api
     * @param detail
     * @param result
     */
    String addSuccessLog(AdminUserToken adminUserToken, String api, String detail, String result);
}
