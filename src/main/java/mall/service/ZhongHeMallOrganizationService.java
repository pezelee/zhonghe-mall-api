 
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
