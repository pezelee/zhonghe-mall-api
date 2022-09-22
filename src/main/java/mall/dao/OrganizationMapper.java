package mall.dao;

import mall.entity.Organization;
import mall.util.PageQueryUtil;

import java.util.List;

public interface OrganizationMapper {
    int deleteByPrimaryKey(Long organizationId);

    int insert(Organization record);

    int insertSelective(Organization record);

    Organization selectByName(String orgName);

    Organization selectByPrimaryKey(Long organizationId);

    int updateByPrimaryKeySelective(Organization record);

    int updateByPrimaryKey(Organization record);

    List<Organization> findOrganizationList(PageQueryUtil pageUtil);

    int getTotalOrganizations(PageQueryUtil pageUtil);

    int deleteBatch(Long[] ids,Long updateUser);
}