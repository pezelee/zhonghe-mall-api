 
package mall.service.impl;

import mall.common.ServiceResultEnum;
import mall.dao.OrganizationMapper;
import mall.entity.Organization;
import mall.service.ZhongHeMallOrganizationService;
import mall.util.PageQueryUtil;
import mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ZhongHeMallOrganizationServiceImpl implements ZhongHeMallOrganizationService {

    @Autowired
    private OrganizationMapper organizationMapper;


    @Override
    public PageResult getOrganizationPage(PageQueryUtil pageUtil) {
        List<Organization> organizations = organizationMapper.findOrganizationList(pageUtil);
        int total = organizationMapper.getTotalOrganizations(pageUtil);
        PageResult pageResult = new PageResult(organizations, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveOrganization(Organization organization) {
        organization.setCreateTime(new Date());
        organization.setUpdateTime(new Date());
        Organization temp = organizationMapper.selectByName(organization.getOrgName());
        if (temp != null) {
            return ServiceResultEnum.SAME_ORG_NAME_EXIST.getResult();
        }
        if (organizationMapper.insertSelective(organization) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateOrganization(Organization organization) {
        Organization temp = organizationMapper.selectByPrimaryKey(organization.getOrganizationId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        Organization temp2 = organizationMapper.selectByName(organization.getOrgName());
        if (temp2 != null && !organization.getOrganizationId().equals(temp2.getOrganizationId())) {
            return ServiceResultEnum.SAME_ORG_NAME_EXIST.getResult();
        }
        temp.setOrgName(organization.getOrgName());
        temp.setDescription(organization.getDescription());
        temp.setUpdateTime(new Date());
        if (organizationMapper.updateByPrimaryKeySelective(temp) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Organization getOrganizationById(Long id) {
        return organizationMapper.selectByPrimaryKey(id);
    }

    @Override
    public String deleteById(Long id) {
        Organization temp = organizationMapper.selectByPrimaryKey(id);
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setIsDeleted((byte) 1);
        temp.setUpdateTime(new Date());
        if (organizationMapper.deleteByPrimaryKey(id) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Boolean deleteBatch(Long[] ids,Long updateUser) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return organizationMapper.deleteBatch(ids,updateUser) > 0;
    }

//
}
