 
package mall.service;

import mall.api.admin.param.OrderMailParam;
import mall.api.mall.vo.ZhongHeMallOrderDetailVO;
import mall.api.mall.vo.ZhongHeMallOrderItemVO;
import mall.api.mall.vo.ZhongHeMallShoppingCartItemVO;
import mall.api.mall.vo.ZhongHeMallUserAddressVO;
import mall.entity.MallUser;
import mall.entity.MallUserAddress;
import mall.entity.ZhongHeMallOrder;
import mall.entity.excel.ExportOrder;
import mall.entity.excel.ImportOrder;
import mall.util.PageQueryUtil;
import mall.util.PageResult;

import java.util.List;

public interface ZhongHeMallOrderService {
    /**
     * 获取订单详情
     *
     * @param orderId
     * @return
     */
    ZhongHeMallOrderDetailVO getOrderDetailByOrderId(Long orderId);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    ZhongHeMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 获取订单地址
     *
     * @param orderId
     * @return
     */
    ZhongHeMallUserAddressVO getAddressByOrderId(Long orderId);

    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * 手动取消订单
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * 确认收货
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Long userId);

    String paySuccess(String orderNo, int payType);

    String saveOrder(MallUser loginMallUser, MallUserAddress address, List<ZhongHeMallShoppingCartItemVO> itemsForSave);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getZhongHeMallOrdersPage(PageQueryUtil pageUtil);

    /**
     * 获得导出的订单数据
     *
     * @param pageUtil
     * @return
     */
    List<ExportOrder> getZhongHeMallOrdersExport(PageQueryUtil pageUtil);

    /**
     * 订单信息修改
     *
     * @param zhongHeMallOrder
     * @return
     */
    String updateOrderInfo(ZhongHeMallOrder zhongHeMallOrder);

    /**
     * 配货
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 出库
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 订单添加邮寄单号
     *
     * @param orderMailParam
     * @return
     */
    String setMailNo(OrderMailParam orderMailParam);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    List<ZhongHeMallOrderItemVO> getOrderItems(Long orderId);

    /**
     * 从导入批量添加邮寄单号
     *
     * @param order
     * @return
     */
    String setMailNoImport(ImportOrder order);
}
