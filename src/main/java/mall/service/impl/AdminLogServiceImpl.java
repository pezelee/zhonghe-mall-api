
package mall.service.impl;

import mall.common.ServiceResultEnum;
import mall.dao.AdminUserMapper;
import mall.dao.ZhongHeAdminLogMapper;
import mall.entity.AdminUserToken;
import mall.entity.Log;
import mall.service.AdminLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class AdminLogServiceImpl implements AdminLogService {

    @Resource
    private AdminUserMapper adminUserMapper;

    @Resource
    private ZhongHeAdminLogMapper zhongHeAdminLogMapper;

    @Override
    public String addSuccessLog(AdminUserToken adminUserToken, String api, String detail, String result) {

        Log log = new Log();
        log.setAdminUserId(adminUserToken.getAdminUserId());
        log.setLoginUserName("");
        log.setOrganizationId(adminUserToken.getOrganizationId());
        log.setRole(adminUserToken.getRole());
        log.setUpdateTime(new Date());
        log.setApi(api);
        log.setDetail(detail);
        log.setResult(result);

        if (zhongHeAdminLogMapper.insertSelective(log) > 0) {
            //新增成功后返回
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.ERROR.getResult();
    }
}
