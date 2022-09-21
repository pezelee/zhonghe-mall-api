/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.service;

import mall.entity.Organization;
import mall.util.PageQueryUtil;
import mall.util.PageResult;

public interface ZhongHeMallOrganizationService {


    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getOrganizationPage(PageQueryUtil pageUtil);

    String saveOrganization(Organization organization);

    String updateOrganization(Organization organization);

    Organization getOrganizationById(Long id);

    String deleteById(Long id);

    Boolean deleteBatch(Long[] ids,Long updateUser);
}
