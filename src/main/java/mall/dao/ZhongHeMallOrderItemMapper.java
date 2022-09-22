package mall.dao;

import mall.entity.ZhongHeMallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhongHeMallOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(ZhongHeMallOrderItem record);

    int insertSelective(ZhongHeMallOrderItem record);

    ZhongHeMallOrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<ZhongHeMallOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<ZhongHeMallOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<ZhongHeMallOrderItem> orderItems);

    int updateByPrimaryKeySelective(ZhongHeMallOrderItem record);

    int updateByPrimaryKey(ZhongHeMallOrderItem record);
}