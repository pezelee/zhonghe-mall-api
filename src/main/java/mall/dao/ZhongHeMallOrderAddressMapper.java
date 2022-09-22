package mall.dao;

import mall.entity.ZhongHeMallOrderAddress;

public interface ZhongHeMallOrderAddressMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(ZhongHeMallOrderAddress record);

    int insertSelective(ZhongHeMallOrderAddress record);

    ZhongHeMallOrderAddress selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(ZhongHeMallOrderAddress record);

    int updateByPrimaryKey(ZhongHeMallOrderAddress record);
}