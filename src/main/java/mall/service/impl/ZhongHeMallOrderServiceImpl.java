
package mall.service.impl;

import mall.api.admin.param.NoticeAddParam;
import mall.api.admin.param.OrderMailParam;
import mall.api.mall.vo.*;
import mall.common.*;
import mall.dao.*;
import mall.entity.*;
import mall.entity.excel.ExportOrder;
import mall.entity.excel.ImportOrder;
import mall.service.NoticeService;
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

import javax.annotation.Resource;
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
    @Resource
    private NoticeService noticeService;


    @Override
    public ZhongHeMallOrderDetailVO getOrderDetailByOrderId(Long orderId) {
        ZhongHeMallOrder zhongHeMallOrder = zhongHeMallOrderMapper.selectByPrimaryKey(orderId);
        if (zhongHeMallOrder == null) {
            ZhongHeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        List<ZhongHeMallOrderItem> orderItems = zhongHeMallOrderItemMapper.selectByOrderId(zhongHeMallOrder.getOrderId());
        //?????????????????????
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
        //?????????????????????
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
            //???????????? ??????????????????vo
            orderListVOS = BeanUtil.copyList(zhongHeMallOrders, ZhongHeMallOrderListVO.class);
            //?????????????????????????????????
            for (ZhongHeMallOrderListVO zhongHeMallOrderListVO : orderListVOS) {
                zhongHeMallOrderListVO.setOrderStatusString(ZhongHeMallOrderStatusEnum.getZhongHeMallOrderStatusEnumByStatus(zhongHeMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = zhongHeMallOrders.stream().map(ZhongHeMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<ZhongHeMallOrderItem> orderItems = zhongHeMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<ZhongHeMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(ZhongHeMallOrderItem::getOrderId));
                for (ZhongHeMallOrderListVO zhongHeMallOrderListVO : orderListVOS) {
                    //????????????????????????????????????????????????
                    if (itemByOrderIdMap.containsKey(zhongHeMallOrderListVO.getOrderId())) {
                        List<ZhongHeMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(zhongHeMallOrderListVO.getOrderId());
                        //???ZhongHeMallOrderItem?????????????????????ZhongHeMallOrderItemVO????????????
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
            //?????????????????????userId???????????????????????????
            if (!userId.equals(zhongHeMallOrder.getUserId())) {
                ZhongHeMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            //??????????????????
            if (zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_PAID.getOrderStatus()
                    || zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_PACKAGED.getOrderStatus()
                    || zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()
                    || zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                    || zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                    || zhongHeMallOrder.getOrderStatus().intValue() == ZhongHeMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            //?????????????????????  ?????????????????????
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
            //?????????????????????userId???????????????????????????
            if (!userId.equals(zhongHeMallOrder.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //?????????????????? ???????????????????????????????????????
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
            //?????????????????? ??????????????????????????????????????????
            if (zhongHeMallOrder.getOrderStatus().intValue() != ZhongHeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            if (payType == 3) {
                //????????????
                logger.info("???????????????userId:{},point:{}", userId.toString(),totalPoint.toString());
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
        //?????????????????????????????????
        List<ZhongHeMallGoods> goodsListNotSelling = zhongHeMallGoods.stream()
                .filter(zhongHeMallGoodsTemp -> zhongHeMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling ????????????????????????????????????
            ZhongHeMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "??????????????????????????????");
        }
        Map<Long, ZhongHeMallGoods> zhongHeMallGoodsMap = zhongHeMallGoods.stream().collect(Collectors.toMap(ZhongHeMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //??????????????????
        for (ZhongHeMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //?????????????????????????????????????????????????????????????????????????????????????????????
            if (!zhongHeMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                ZhongHeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //????????????????????????????????????????????????????????????
            if (shoppingCartItemVO.getGoodsCount() > zhongHeMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                ZhongHeMallException.fail(shoppingCartItemVO.getGoodsName() + ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //???????????????
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(zhongHeMallGoods)) {
            if (zhongHeMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = zhongHeMallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    ZhongHeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //???????????????
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                int pointTotal = 0;
                //????????????
                ZhongHeMallOrder zhongHeMallOrder = new ZhongHeMallOrder();
                zhongHeMallOrder.setOrderNo(orderNo);
                zhongHeMallOrder.setUserId(loginMallUser.getUserId());
                zhongHeMallOrder.setLoginName(loginMallUser.getLoginName());
                zhongHeMallOrder.setNickName(loginMallUser.getNickName());
                zhongHeMallOrder.setOrganizationId(loginMallUser.getOrganizationId());
                //??????
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
                //???????????????????????????????????????
                if (zhongHeMallOrderMapper.insertSelective(zhongHeMallOrder) > 0) {
                    //??????????????????????????????????????????????????????
                    ZhongHeMallOrderAddress zhongHeMallOrderAddress = new ZhongHeMallOrderAddress();
                    BeanUtil.copyProperties(address, zhongHeMallOrderAddress);
                    zhongHeMallOrderAddress.setOrderId(zhongHeMallOrder.getOrderId());
                    //??????????????????????????????????????????????????????
                    List<ZhongHeMallOrderItem> zhongHeMallOrderItems = new ArrayList<>();
                    for (ZhongHeMallShoppingCartItemVO zhongHeMallShoppingCartItemVO : myShoppingCartItems) {
                        ZhongHeMallOrderItem zhongHeMallOrderItem = new ZhongHeMallOrderItem();
                        //??????BeanUtil????????????ZhongHeMallShoppingCartItemVO?????????????????????ZhongHeMallOrderItem?????????
                        BeanUtil.copyProperties(zhongHeMallShoppingCartItemVO, zhongHeMallOrderItem);
                        //ZhongHeMallOrderMapper??????insert()??????????????????useGeneratedKeys??????orderId???????????????
                        zhongHeMallOrderItem.setOrderId(zhongHeMallOrder.getOrderId());
                        zhongHeMallOrderItem.setCreateTime(new Date());
                        zhongHeMallOrderItems.add(zhongHeMallOrderItem);
                    }
                    //??????????????????
                    if (zhongHeMallOrderItemMapper.insertBatch(zhongHeMallOrderItems) > 0 && zhongHeMallOrderAddressMapper.insertSelective(zhongHeMallOrderAddress) > 0) {
                        //???????????????????????????????????????????????????Controller???????????????????????????
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
        //????????????orderStatus>=0????????????????????????????????????????????????
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
        //????????????????????? ???????????? ???????????????????????????
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
                //?????????????????? ?????????????????????????????? ?????????????????????????????????
                if (zhongHeMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //????????????????????????????????????
                return errorOrderNos + "?????????????????????????????????????????????????????????";
//                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
//                    return errorOrderNos + "?????????????????????????????????????????????????????????";
//                } else {
//                    return "????????????????????????????????????????????????????????????????????????????????????";
//                }
            }
        }
        //?????????????????? ??????????????????
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //????????????????????? ???????????? ???????????????????????????
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
                //?????????????????? ???????????????????????? ?????????????????????????????????
                if (zhongHeMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //????????????????????????????????????
                return errorOrderNos + "????????????????????????????????????????????????????????????????????????";
//                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
//                    return errorOrderNos + "????????????????????????????????????????????????????????????????????????";
//                } else {
//                    return "?????????????????????????????????????????????????????????????????????????????????????????????";
//                }
            }
        }
        //?????????????????? ??????????????????
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String setMailNo(OrderMailParam orderMailParam) {
        ZhongHeMallOrder temp = zhongHeMallOrderMapper.selectByPrimaryKey(orderMailParam.getOrderId());
        if (temp == null) {
            return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
        }
        String result = checkOut(temp.getOrderNo(),orderMailParam.getMailNo());
        return result;
    }


    @Override
    public String setMailNoImport(ImportOrder order) {
        String result = checkOut(order.getOrderNo(),order.getMailNo());
        return result;
    }

    //????????????
    private String checkOut(String orderNo,String mailNo){
        ZhongHeMallOrder temp = zhongHeMallOrderMapper.selectByOrderNo(orderNo);
        //????????????orderStatus>=0????????????????????????????????????????????????
        if (temp != null && temp.getIsDeleted() == 0 ) {
            if (temp.getOrderStatus() >= 2 && temp.getOrderStatus() <= 3) {
                ZhongHeMallOrder temp2 =zhongHeMallOrderMapper.selectByMailNo(mailNo);
                if (temp2 != null) {
                    return ServiceResultEnum.SAME_MAIL_NO.getResult();
                }
                temp.setUpdateTime(new Date());
                temp.setOrderStatus((byte) 3);
                temp.setMailNo(mailNo);
                if (zhongHeMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                    String mailresult = mailNoNotice(temp,mailNo);
                    return ServiceResultEnum.SUCCESS.getResult();
                }
                return ServiceResultEnum.DB_ERROR.getResult();
            }else {
                return "?????????????????????????????????????????????????????????????????????";
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    private String mailNoNotice(ZhongHeMallOrder order,String mailNo){
        NoticeAddParam addParam = new NoticeAddParam();
        addParam.setTitle("???????????????????????????");
        addParam.setSender("??????????????????");
        addParam.setNotice("?????????????????? " + order.getOrderNo() +
                " ?????????????????????????????????: "+mailNo+"?????????????????????");
        addParam.setNoticeType((byte)0);
        String result = noticeService.saveNotice(addParam,order.getUserId());
        return result;
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //????????????????????? ???????????? ???????????????????????????
        List<ZhongHeMallOrder> orders = zhongHeMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (ZhongHeMallOrder zhongHeMallOrder : orders) {
                // isDeleted=1 ????????????????????????
                if (zhongHeMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += zhongHeMallOrder.getOrderNo() + " ";
                    continue;
                }
                //??????????????????????????????????????????
                if (zhongHeMallOrder.getOrderStatus() == 4 || zhongHeMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += zhongHeMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //?????????????????? ???????????????????????? ?????????????????????????????????
                if (zhongHeMallOrderMapper.closeOrder(Arrays.asList(ids), ZhongHeMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //????????????????????????????????????
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "??????????????????????????????";
                } else {
                    return "??????????????????????????????????????????";
                }
            }
        }
        //?????????????????? ??????????????????
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public List<ZhongHeMallOrderItemVO> getOrderItems(Long orderId) {
        ZhongHeMallOrder zhongHeMallOrder = zhongHeMallOrderMapper.selectByPrimaryKey(orderId);
        if (zhongHeMallOrder != null) {
            List<ZhongHeMallOrderItem> orderItems = zhongHeMallOrderItemMapper.selectByOrderId(zhongHeMallOrder.getOrderId());
            //?????????????????????
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<ZhongHeMallOrderItemVO> zhongHeMallOrderItemVOS = BeanUtil.copyList(orderItems, ZhongHeMallOrderItemVO.class);
                return zhongHeMallOrderItemVOS;
            }
        }
        return null;
    }

}
