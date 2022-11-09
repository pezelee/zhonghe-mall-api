package mall.api.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mall.api.mall.param.SaveOrderParam;
import mall.api.mall.vo.ZhongHeMallOrderDetailVO;
import mall.api.mall.vo.ZhongHeMallOrderListVO;
import mall.api.mall.vo.ZhongHeMallShoppingCartItemVO;
import mall.api.mall.vo.ZhongHeMallUserAddressVO;
import mall.common.Constants;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallException;
import mall.config.annotation.TokenToMallUser;
import mall.entity.MallUser;
import mall.entity.MallUserAddress;
import mall.service.ZhongHeMallOrderService;
import mall.service.ZhongHeMallShoppingCartService;
import mall.service.ZhongHeMallUserAddressService;
import mall.util.PageQueryUtil;
import mall.util.PageResult;
import mall.util.Result;
import mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(value = "v1", tags = "7.众鹤商城订单操作相关接口")
@RequestMapping("/api/v1")
public class ZhongHeMallOrderAPI {
    private static final Logger logger = LoggerFactory.getLogger(ZhongHeMallOrderAPI.class);

    @Resource
    private ZhongHeMallShoppingCartService zhongHeMallShoppingCartService;
    @Resource
    private ZhongHeMallOrderService zhongHeMallOrderService;
    @Resource
    private ZhongHeMallUserAddressService zhongHeMallUserAddressService;


    @PostMapping("/saveOrder")
    @ApiOperation(value = "生成订单接口", notes = "传参为地址id和待结算的购物项id数组")
    public Result<String> saveOrder(@ApiParam(value = "订单参数") @RequestBody SaveOrderParam saveOrderParam, @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户生成订单接口   saveOrderParam:{},loginMallUser:{}", saveOrderParam.toString(), loginMallUser.toString());
        int priceTotal = 0;
        int pointTotal = 0;
        if (saveOrderParam == null || saveOrderParam.getCartItemIds() == null || saveOrderParam.getAddressId() == null) {
            ZhongHeMallException.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        if (saveOrderParam.getCartItemIds().length < 1) {
            ZhongHeMallException.fail(ServiceResultEnum.PARAM_ERROR.getResult());
        }
        List<ZhongHeMallShoppingCartItemVO> itemsForSave = zhongHeMallShoppingCartService.getCartItemsForSettle(Arrays.asList(saveOrderParam.getCartItemIds()), loginMallUser.getUserId());
        if (CollectionUtils.isEmpty(itemsForSave)) {
            //无数据
            ZhongHeMallException.fail("参数异常");
        } else {
            //总价
            for (ZhongHeMallShoppingCartItemVO zhongHeMallShoppingCartItemVO : itemsForSave) {
                priceTotal += zhongHeMallShoppingCartItemVO.getGoodsCount() * zhongHeMallShoppingCartItemVO.getSellingPrice();
                pointTotal += zhongHeMallShoppingCartItemVO.getGoodsCount() * zhongHeMallShoppingCartItemVO.getSellingPoint();
            }
            if (priceTotal < 1 && pointTotal < 1) {
                ZhongHeMallException.fail("价格异常");
            }
            MallUserAddress address = zhongHeMallUserAddressService.getMallUserAddressById(saveOrderParam.getAddressId());
            if (!loginMallUser.getUserId().equals(address.getUserId())) {
                return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
            }
            //保存订单并返回订单号
            String saveOrderResult = zhongHeMallOrderService.saveOrder(loginMallUser, address, itemsForSave);
            Result result = ResultGenerator.genSuccessResult();
            result.setData(saveOrderResult);
            return result;
        }
        return ResultGenerator.genFailResult("生成订单失败");
    }

    @GetMapping("/order/{orderNo}")
    @ApiOperation(value = "订单详情接口", notes = "传参为订单号")
    public Result orderDetailPage(@ApiParam(value = "订单号") @PathVariable("orderNo") String orderNo, @TokenToMallUser MallUser loginMallUser) {

        ZhongHeMallOrderDetailVO detailVO = zhongHeMallOrderService.getOrderDetailByOrderNo(orderNo, loginMallUser.getUserId());
        ZhongHeMallUserAddressVO addressVO = zhongHeMallOrderService.getAddressByOrderId(detailVO.getOrderId());
        Map info = new HashMap(8);
        info.put("orderDetail", detailVO);
        info.put("address", addressVO);
        logger.info("用户订单详情接口  MallUser:{},orderNo:{}", loginMallUser.toString(), orderNo);
        return ResultGenerator.genSuccessResult(info);
    }

    @GetMapping("/order")
    @ApiOperation(value = "订单列表接口", notes = "传参为页码")
    public Result<PageResult<List<ZhongHeMallOrderListVO>>> orderList(@ApiParam(value = "页码") @RequestParam(required = false) Integer pageNumber,
                                                                      @ApiParam(value = "订单状态:0.待支付 1.待确认 2.待发货 3:已发货 4.交易成功") @RequestParam(required = false) Integer orderStatus,
                                                                      @ApiParam(value = "支付状态:0.未支付,1.支付成功,-1:支付失败") @RequestParam(required = false) Integer payStatus,
                                                                      @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户订单列表接口  MallUser:{}", loginMallUser.toString());
        if (pageNumber < 1 ) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        logger.info("列表参数 pageNumber:{}", pageNumber.toString());
        if (orderStatus != null) {
            logger.info("订单状态  orderStatus:{}", orderStatus.toString());
        }
        if (payStatus != null) {
            logger.info("支付状态  payStatus:{}", payStatus.toString());
        }
        Map params = new HashMap(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("userId", loginMallUser.getUserId());
        params.put("orderStatus", orderStatus);
        params.put("payStatus", payStatus);
        params.put("page", pageNumber);
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装分页请求参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(zhongHeMallOrderService.getMyOrders(pageUtil));
    }

    @PutMapping("/order/{orderNo}/cancel")
    @ApiOperation(value = "订单取消接口", notes = "传参为订单号")
    public Result cancelOrder(@ApiParam(value = "订单号") @PathVariable("orderNo") String orderNo, @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户订单取消接口  MallUser:{},orderNo:{}", loginMallUser.toString(), orderNo);
        String cancelOrderResult = zhongHeMallOrderService.cancelOrder(orderNo, loginMallUser.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @PutMapping("/order/{orderNo}/finish")
    @ApiOperation(value = "确认收货接口", notes = "传参为订单号")
    public Result finishOrder(@ApiParam(value = "订单号") @PathVariable("orderNo") String orderNo,
                              @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户确认收货接口  MallUser:{},orderNo:{}", loginMallUser.toString(), orderNo);
        String finishOrderResult = zhongHeMallOrderService.finishOrder(orderNo, loginMallUser.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    @GetMapping("/paySuccess")
    @ApiOperation(value = "支付成功回调的接口", notes = "传参为订单号和支付方式")
    public Result paySuccess(@ApiParam(value = "订单号") @RequestParam("orderNo") String orderNo,
                             @ApiParam(value = "支付方式 0.无 1.支付宝支付 2.微信支付，3 积分支付") @RequestParam("payType") int payType,
                             @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户支付订单接口  loginMallUser:{}  payType:{},orderNo:{}", loginMallUser.toString(), payType, orderNo);
        //payType : 0.无 1.支付宝支付 2.微信支付，3 积分支付
//        if (payType == 3) {
//            //积分支付
//            logger.info("积分支付：MallUser:{},point:{}", loginMallUser.toString(),point);
//            String Result = zhongHeMallUserService.payPoint(loginMallUser.getUserId(),point);
//            if (!Result.equals("success")) {
//                return ResultGenerator.genFailResult(Result);
//            }
//            return ResultGenerator.genSuccessResult(Result);
//        }
        if (payType != 3) {
            return ResultGenerator.genFailResult("暂不支持积分支付以外的其他支付");
        }
        String payResult = zhongHeMallOrderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

}
