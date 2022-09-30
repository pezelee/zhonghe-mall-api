 
package mall.service.impl;

import mall.api.admin.param.OrderMailParam;
import mall.api.mall.vo.*;
import mall.common.*;
import mall.dao.*;
import mall.entity.*;
import mall.entity.excel.ExportOrder;
import mall.entity.excel.ImportOrder;
import mall.service.ZhongHeMallOrderService;
import mall.service.ZhongHeMallUserService;
import mall.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class ZhongHeMallOrderServiceImpl implements ZhongHeMallOrderService {


    private static final Logger logger = LoggerFactory.getLogger(ZhongHeMallOrderServiceImpl.class);

    @Autowired
    private ZhongHeMallOrderMapper zhongHeMallOrderMapper;
    @Autowired
    private ZhongHeMallOrderItemMapper zhongHeMallOrderItemMapper;
    @Autowired
    private ZhongHeMallShoppingCartItemMapper zhongHeMallShoppingCartItemMapper;
    @Autowired
    private ZhongHeMallGoodsMapper zhongHeMallGoodsMapper;
    @Autowired
    private ZhongHeMallOrderAddressMapper zhongHeMallOrderAddressMapper;
    @Autowired
    private MallUserMapper mallUserMapper;
    @Autowired
    private ZhongHeMallUserService zhongHeMallUserService;


    @Override
    public ZhongHeMallOrderDetailVO getOrderDetailByOrderId(Long orderId) {
        ZhongHeMallOrder zhongHeMallOrder = zhongHeMallOrderMapper.selectByPrimaryKey(orderId);
        if (zhongHeMallOrder == null) {
            ZhongHeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        List<ZhongHeMallOrderItem> orderItems = zhongHeMallOrderItemMapper.selectByOrderId(zhongHeMallOrder.getOrderId());
        //获取订单项数据
        if (!CollectionUtils.isEmpty(orderItems)) {
            List<ZhongHeMallOrderItemVO> zhongHeMallOrderItemVOS = BeanUtil.copyList(orderItems, ZhongHeMallOrderItemVO.class);
            ZhongHeMallOrderDetailVO zhongHeMallOrderDetailVO = new ZhongHeMallOrderDetailVO();
            BeanUtil.copyProperties(zhongHeMallOrder, zhongHeMallOrderDetailVO);
            zhongHeMallOrderDetailVO.setOrderStatusString(ZhongHeMallOrderStatusEnum.getZhongHeMallOrderStatusEnumByStatus(zhongHeMallOrderDetailVO.getOrderStatus()).getName());
            zhongHeMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(zhongHeMallOrderDetailVO.getPayType()).getName());
            zhongHeMallOrderDetailVO.setZhongHeMallOrderItemVOS(zhongHeMallOrderItemVOS);
            return zhongHeMallOrderDetailVO;
        } else {
            ZhongHeMallException.fail(ServiceResultEnum.ORDER_ITEM_NULL_ERROR.getResult());
            return null;
        }
    }

    @Override
    public ZhongHeMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        ZhongHeMallOrder zhongHeMallOrder = zhongHeMallOrderMapper.selectByOrderNo(orderNo);
//        MallUser user = mallUserMapper.selectByPrimaryKey(userId);
        if (zhongHeMallOrder == null ) {
            ZhongHeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        if (!userId.equals(zhongHeMallOrder.getUserId())) {
            ZhongHeMallException.fail(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        List<ZhongHeMallOrderItem> orderItems = zhongHeMallOrderItemMapper.selectByOrderId(zhongHeMallOrder.getOrderId());
        //获取订单项数据
        if (CollectionUtils.isEmpty(orderItems)) {
            ZhongHeMallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        List<ZhongHeMallOrderItemVO> zhongHeMallOrderItemVOS = BeanUtil.copyList(orderItems, ZhongHeMallOrderItemVO.class);
        ZhongHeMallOrderDetailVO zhongHeMallOrderDetailVO = new ZhongHeMallOrderDetailVO();
        BeanUtil.copyProperties(zhongHeMallOrder, zhongHeMallOrderDetailVO);
        zhongHeMallOrderDetailVO.setOrderStatusString(ZhongHeMallOrderStatusEnum.getZhongHeMallOrderStatusEnumByStatus(zhongHeMallOrderDetailVO.getOrderStatus()).getName());
        zhongHeMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(zhongHeMallOrderDetailVO.getPayType()).getName());
        zhongHeMallOrderDetailVO.setZhongHeMallOrderItemVOS(zhongHeMallOrderItemVOS);
        return zhongHeMallOrderDetailVO;
    }

    @Override
    public ZhongHeMallUserAddressVO getAddressByOrderId(Long orderId){
        ZhongHeMallUserAddressVO addressVO = new ZhongHeMallUserAddressVO();
        ZhongHeMallOrderAddress address = zhongHeMallOrderAddressMapper.selectByPrimaryKey(orderId);
        if (address == null) {
            ZhongHeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        BeanUtil.copyProperties(address, addressVO);
        return addressVO;
    }


    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = zhongHeMallOrderMapper.getTotalZhongHeMallOrders(pageUtil);
        List<ZhongHeMallOrder> zhongHeMallOrders = zhongHeMallOrderMapper.findZhongHeMallOrderList(pageUtil);
        List<ZhongHeMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(zhongHeMallOrders, ZhongHeMallOrderListVO.class);
            //设置订单状态中文显示值
            for (ZhongHeMallOrderListVO zhongHeMallOrderListVO : orderListVOS) {
                zhongHeMallOrderListVO.setOrderStatusString(ZhongHeMallOrderStatusEnum.getZhongHeMallOrderStatusEnumByStatus(zhongHeMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = zhongHeMallOrders.stream().map(ZhongHeMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<ZhongHeMallOrderItem> orderItems = zhongHeMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<ZhongHeMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(ZhongHeMallOrderItem::getOrderId));
                for (ZhongHeMallOrderListVO zhongHeMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(zhongHeMallOrderListVO.getOrderId())) {
                        List<ZhongHeMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(zhongHeMallOrderListVO.getOrderId());
                        //将ZhongHeMallOrderItem对象列表转换成ZhongHeMallOrderItemVO对象列表
                        List<ZhongHeMallOrderItemVO> zhongHeMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, ZhongHeMallOrderItemVO.class);
                        zhongHeMallOrderListVO.setZhongHeMallOrderItemVOS(zhongHeMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        ZhongHeMallOrder zhongHeMallOrder = zhongHeMallOrderMapper.selectByOrderNo(orderNo);
        if (zhongHeMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(zhongHeMallOrder.getUserId())) {
                ZhongHeMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            //订单状态判断
            if (zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_PAID.getOrderStatus()
                    || zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_PACKAGED.getOrderStatus()
                    || zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()
                    || zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                    || zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                    || zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            //恢复库存和销量  获取订单项数据
            List<ZhongHeMallOrderItem> orderItems = zhongHeMallOrderItemMapper.selectByOrderId(zhongHeMallOrder.getOrderId());
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(orderItems, StockNumDTO.class);
                int updateStockNumResult = zhongHeMallGoodsMapper.cancelStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    ZhongHeMallException.fail(ServiceResultEnum.CANCEL_GOODS_COUNT_ERROR.getResult());
                }
            }
            if (zhongHeMallOrderMapper.closeOrder(Collections.singletonList(zhongHeMallOrder.getOrderId()), ZhongHeMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        ZhongHeMallOrder zhongHeMallOrder = zhongHeMallOrderMapper.selectByOrderNo(orderNo);
        if (zhongHeMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(zhongHeMallOrder.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //订单状态判断 非出库状态下不进行修改操作
            if (zhongHeMallOrder.getOrderStatus().intValue() != ZhongHeMallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            zhongHeMallOrder.setOrderStatus((byte) ZhongHeMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            zhongHeMallOrder.setUpdateTime(new Date());
            if (zhongHeMallOrderMapper.updateByPrimaryKeySelective(zhongHeMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        ZhongHeMallOrder zhongHeMallOrder = zhongHeMallOrderMapper.selectByOrderNo(orderNo);
        if (zhongHeMallOrder != null) {
            Long userId = zhongHeMallOrder.getUserId();
            Integer totalPrice = zhongHeMallOrder.getTotalPrice();
            Integer totalPoint = zhongHeMallOrder.getTotalPoint();
            //订单状态判断 非待支付状态下不进行修改操作
            if (zhongHeMallOrder.getOrderStatus().intValue() != ZhongHeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            if (payType == 3) {
                //积分支付
                logger.info("积分支付：userId:{},point:{}", userId.toString(),totalPoint.toString());
                String Result = zhongHeMallUserService.payPoint(userId,totalPoint);
                if (!Result.equals("success")) {
                    return Result;
                }
            }
            zhongHeMallOrder.setOrderStatus((byte) ZhongHeMallOrderStatusEnum.ORDER_PAID.getOrderStatus());
            zhongHeMallOrder.setPayType((byte) payType);
            zhongHeMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            zhongHeMallOrder.setPayTime(new Date());
            zhongHeMallOrder.setUpdateTime(new Date());
            if (zhongHeMallOrderMapper.updateByPrimaryKeySelective(zhongHeMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(MallUser loginMallUser, MallUserAddress address, List<ZhongHeMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(ZhongHeMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(ZhongHeMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<ZhongHeMallGoods> zhongHeMallGoods = zhongHeMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<ZhongHeMallGoods> goodsListNotSelling = zhongHeMallGoods.stream()
                .filter(zhongHeMallGoodsTemp -> zhongHeMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            ZhongHeMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, ZhongHeMallGoods> zhongHeMallGoodsMap = zhongHeMallGoods.stream().collect(Collectors.toMap(ZhongHeMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (ZhongHeMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!zhongHeMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                ZhongHeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > zhongHeMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                ZhongHeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(zhongHeMallGoods)) {
            if (zhongHeMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = zhongHeMallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    ZhongHeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                int pointTotal = 0;
                //保存订单
                ZhongHeMallOrder zhongHeMallOrder = new ZhongHeMallOrder();
                zhongHeMallOrder.setOrderNo(orderNo);
                zhongHeMallOrder.setUserId(loginMallUser.getUserId());
                zhongHeMallOrder.setLoginName(loginMallUser.getLoginName());
                zhongHeMallOrder.setNickName(loginMallUser.getNickName());
                zhongHeMallOrder.setOrganizationId(loginMallUser.getOrganizationId());
                //总价
                for (ZhongHeMallShoppingCartItemVO zhongHeMallShoppingCartItemVO : myShoppingCartItems) {
                    pointTotal += zhongHeMallShoppingCartItemVO.getGoodsCount() * zhongHeMallShoppingCartItemVO.getSellingPoint();
                    priceTotal += zhongHeMallShoppingCartItemVO.getGoodsCount() * zhongHeMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1 && pointTotal < 1) {
                    ZhongHeMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                zhongHeMallOrder.setTotalPrice(priceTotal);
                zhongHeMallOrder.setTotalPoint(pointTotal);
                String extraInfo = "";
                zhongHeMallOrder.setExtraInfo(extraInfo);
                zhongHeMallOrder.setCreateTime(new Date());
                zhongHeMallOrder.setUpdateTime(new Date());
                //生成订单项并保存订单项纪录
                if (zhongHeMallOrderMapper.insertSelective(zhongHeMallOrder) > 0) {
                    //生成订单收货地址快照，并保存至数据库
                    ZhongHeMallOrderAddress zhongHeMallOrderAddress = new ZhongHeMallOrderAddress();
                    BeanUtil.copyProperties(address, zhongHeMallOrderAddress);
                    zhongHeMallOrderAddress.setOrderId(zhongHeMallOrder.getOrderId());
                    //生成所有的订单项快照，并保存至数据库
                    List<ZhongHeMallOrderItem> zhongHeMallOrderItems = new ArrayList<>();
                    for (ZhongHeMallShoppingCartItemVO zhongHeMallShoppingCartItemVO : myShoppingCartItems) {
                        ZhongHeMallOrderItem zhongHeMallOrderItem = new ZhongHeMallOrderItem();
                        //使用BeanUtil工具类将ZhongHeMallShoppingCartItemVO中的属性复制到ZhongHeMallOrderItem对象中
                        BeanUtil.copyProperties(zhongHeMallShoppingCartItemVO, zhongHeMallOrderItem);
                        //ZhongHeMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        zhongHeMallOrderItem.setOrderId(zhongHeMallOrder.getOrderId());
                        zhongHeMallOrderItem.setCreateTime(new Date());
                        zhongHeMallOrderItems.add(zhongHeMallOrderItem);
                    }
                    //保存至数据库
                    if (zhongHeMallOrderItemMapper.insertBatch(zhongHeMallOrderItems) > 0 && zhongHeMallOrderAddressMapper.insertSelective(zhongHeMallOrderAddress) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    ZhongHeMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                ZhongHeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            ZhongHeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        ZhongHeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }


    @Override
    public PageResult getZhongHeMallOrdersPage(PageQueryUtil pageUtil) {
        List<ZhongHeMallOrder> zhongHeMallOrders = zhongHeMallOrderMapper.findZhongHeMallOrderList(pageUtil);
        int total = zhongHeMallOrderMapper.getTotalZhongHeMallOrders(pageUtil);
        PageResult pageResult = new PageResult(zhongHeMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public List<ExportOrder> getZhongHeMallOrdersExport(PageQueryUtil pageUtil) {
        List<ExportOrder> zhongHeMallOrders = zhongHeMallOrderMapper.findZhongHeMallOrderExport(pageUtil);
        return zhongHeMallOrders;
    }

    @Override
    @Transactional
    public String updateOrderInfo(ZhongHeMallOrder zhongHeMallOrder) {
        ZhongHeMallOrder temp = zhongHeMallOrderMapper.selectByPrimaryKey(zhongHeMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(zhongHeMallOrder.getTotalPrice());
            temp.setUpdateTime(new Date());
            if (zhongHeMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<ZhongHeMallOrder> orders = zhongHeMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (ZhongHeMallOrder zhongHeMallOrder : orders) {
                if (zhongHeMallOrder.getIsDeleted() == 1 || zhongHeMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += zhongHeMallOrder.getOrderNo() + " ";
                    break;
                }
//                if (zhongHeMallOrder.getOrderStatus() != 1) {
//                    errorOrderNos += zhongHeMallOrder.getOrderNo() + " ";
//                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (zhongHeMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                return errorOrderNos + "订单的状态不是支付成功无法执行配货操作";
//                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
//                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
//                } else {
//                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
//                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<ZhongHeMallOrder> orders = zhongHeMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (ZhongHeMallOrder zhongHeMallOrder : orders) {
                if (zhongHeMallOrder.getIsDeleted() == 1
                        || (zhongHeMallOrder.getOrderStatus() != 2 && zhongHeMallOrder.getOrderStatus() != 3)) {
                    errorOrderNos += zhongHeMallOrder.getOrderNo() + " ";
                    break;
                }
//                if (zhongHeMallOrder.getOrderStatus() != 1 && zhongHeMallOrder.getOrderStatus() != 2) {
//                    errorOrderNos += zhongHeMallOrder.getOrderNo() + " ";
//                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (zhongHeMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                return errorOrderNos + "订单的状态不是配货完成或出库成功无法执行出库操作";
//                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
//                    return errorOrderNos + "订单的状态不是配货完成或出库成功无法执行出库操作";
//                } else {
//                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
//                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String setMailNo(OrderMailParam orderMailParam) {
        String result = checkOut(orderMailParam.getOrderId(),orderMailParam.getMailNo());
        return result;
    }


    @Override
    public String setMailNoImport(ImportOrder order) {
        String result = checkOut(order.getOrderId(),order.getMailNo());
        return result;
    }

    private String checkOut(Long orderId,String mailNo){
        ZhongHeMallOrder temp = zhongHeMallOrderMapper.selectByPrimaryKey(orderId);
        //不为空且orderStatus>=0且状态为完成之前可以修改部分信息
        if (temp != null && temp.getIsDeleted() == 0 ) {
            if (temp.getOrderStatus() >= 2 && temp.getOrderStatus() <= 3) {
                temp.setUpdateTime(new Date());
                temp.setOrderStatus((byte) 3);
                temp.setMailNo(mailNo);
                if (zhongHeMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                }
                return ServiceResultEnum.DB_ERROR.getResult();
            }else {
                return "订单状态不是配货完成或出库成功无法执行出库操作";
            }
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<ZhongHeMallOrder> orders = zhongHeMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (ZhongHeMallOrder zhongHeMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (zhongHeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += zhongHeMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (zhongHeMallOrder.getOrderStatus() == 4 || zhongHeMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += zhongHeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (zhongHeMallOrderMapper.closeOrder(Arrays.asList(ids), ZhongHeMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public List<ZhongHeMallOrderItemVO> getOrderItems(Long orderId) {
        ZhongHeMallOrder zhongHeMallOrder = zhongHeMallOrderMapper.selectByPrimaryKey(orderId);
        if (zhongHeMallOrder != null) {
            List<ZhongHeMallOrderItem> orderItems = zhongHeMallOrderItemMapper.selectByOrderId(zhongHeMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<ZhongHeMallOrderItemVO> zhongHeMallOrderItemVOS = BeanUtil.copyList(orderItems, ZhongHeMallOrderItemVO.class);
                return zhongHeMallOrderItemVOS;
            }
        }
        return null;
    }

}
