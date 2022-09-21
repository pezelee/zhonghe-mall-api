/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
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
