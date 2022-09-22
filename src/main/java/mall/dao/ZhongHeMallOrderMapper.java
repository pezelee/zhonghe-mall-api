package mall.dao;

import mall.entity.ZhongHeMallOrder;
import mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhongHeMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(ZhongHeMallOrder record);

    int insertSelective(ZhongHeMallOrder record);

    ZhongHeMallOrder selectByPrimaryKey(Long orderId);

    ZhongHeMallOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(ZhongHeMallOrder record);

    int updateByPrimaryKey(ZhongHeMallOrder record);

    List<ZhongHeMallOrder> findZhongHeMallOrderList(PageQueryUtil pageUtil);

    int getTotalZhongHeMallOrders(PageQueryUtil pageUtil);

    List<ZhongHeMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}