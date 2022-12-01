package mall.api.mall;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import mall.api.mall.param.SaveCartItemParam;
import mall.api.mall.param.UpdateCartItemParam;
import mall.api.mall.vo.ZhongHeMallShoppingCartItemVO;
import mall.common.Constants;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallException;
import mall.config.annotation.TokenToMallUser;
import mall.entity.MallUser;
import mall.entity.ZhongHeMallShoppingCartItem;
import mall.service.ZhongHeMallShoppingCartService;
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
@Api(value = "v1", tags = "5.众鹤商城购物车相关接口")
@RequestMapping("/api/v1")
public class ZhongHeMallShoppingCartAPI {

    private static final Logger logger = LoggerFactory.getLogger(ZhongHeMallShoppingCartAPI.class);

    @Resource
    private ZhongHeMallShoppingCartService zhongHeMallShoppingCartService;

    @GetMapping("/shop-cart/page")
    @ApiOperation(value = "购物车列表(每页默认5条)", notes = "传参为页码")
    public Result<PageResult<List<ZhongHeMallShoppingCartItemVO>>> cartItemPageList(Integer pageNumber,
                                                                                    @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户购物车列表接口     MallUser:{}", loginMallUser.toString());
        Map params = new HashMap(8);
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        params.put("userId", loginMallUser.getUserId());
        params.put("page", pageNumber);
        params.put("limit", Constants.SHOPPING_CART_PAGE_LIMIT);
        //封装分页请求参数
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(zhongHeMallShoppingCartService.getMyShoppingCartItems(pageUtil));
    }

    @GetMapping("/shop-cart")
    @ApiOperation(value = "购物车列表(网页移动端不分页)", notes = "")
    public Result<List<ZhongHeMallShoppingCartItemVO>> cartItemList(@TokenToMallUser MallUser loginMallUser) {
        logger.info("用户购物车列表不分页接口     MallUser:{}", loginMallUser.toString());
        return ResultGenerator.genSuccessResult(zhongHeMallShoppingCartService.getMyShoppingCartItems(loginMallUser.getUserId()));
    }

    @PostMapping("/shop-cart")
    @ApiOperation(value = "添加商品到购物车接口", notes = "传参为商品id、数量")
    public Result saveZhongHeMallShoppingCartItem(@RequestBody SaveCartItemParam saveCartItemParam,
                                                 @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户添加商品到购物车接口     MallUser:{},ItemParam:{}", loginMallUser.toString(),saveCartItemParam.toString());
        String saveResult = zhongHeMallShoppingCartService.saveZhongHeMallCartItem(saveCartItemParam, loginMallUser.getUserId());
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ApiOperation(value = "修改购物项数据", notes = "传参为购物项id、数量")
    public Result updateZhongHeMallShoppingCartItem(@RequestBody UpdateCartItemParam updateCartItemParam,
                                                   @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户修改购物项数据接口     MallUser:{},ItemParam:{}", loginMallUser.toString(),updateCartItemParam.toString());
        String updateResult = zhongHeMallShoppingCartService.updateZhongHeMallCartItem(updateCartItemParam, loginMallUser.getUserId());
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{zhongHeMallShoppingCartItemId}")
    @ApiOperation(value = "删除购物项", notes = "传参为购物项id")
    public Result updateZhongHeMallShoppingCartItem(@PathVariable("zhongHeMallShoppingCartItemId") Long zhongHeMallShoppingCartItemId,
                                                   @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户删除购物项数据接口     MallUser:{},ItemParam:{}", loginMallUser.toString(),zhongHeMallShoppingCartItemId.toString());
        ZhongHeMallShoppingCartItem zhongHeMallCartItemById = zhongHeMallShoppingCartService.getZhongHeMallCartItemById(zhongHeMallShoppingCartItemId);
        if (!loginMallUser.getUserId().equals(zhongHeMallCartItemById.getUserId())) {
            return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        Boolean deleteResult = zhongHeMallShoppingCartService.deleteById(zhongHeMallShoppingCartItemId,loginMallUser.getUserId());
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @DeleteMapping("/shop-cart/batchDelete")
    @ApiOperation(value = "批量删除购物项", notes = "传参为购物项ids")
    public Result updateZhongHeMallShoppingCartItems(@RequestBody Long[] zhongHeMallShoppingCartItemIds,
                                                    @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户批量购物项数据接口     MallUser:{},ItemParam:{}", loginMallUser.toString(),zhongHeMallShoppingCartItemIds.toString());
        Boolean deleteResult = true;
        for(Long zhongHeMallShoppingCartItemId : zhongHeMallShoppingCartItemIds){
            ZhongHeMallShoppingCartItem zhongHeMallCartItemById = zhongHeMallShoppingCartService.getZhongHeMallCartItemById(zhongHeMallShoppingCartItemId);
            if (!loginMallUser.getUserId().equals(zhongHeMallCartItemById.getUserId())) {
                return ResultGenerator.genFailResult(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
            }
            deleteResult = zhongHeMallShoppingCartService.deleteById(zhongHeMallShoppingCartItemId,loginMallUser.getUserId());
        }
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    @ApiOperation(value = "根据购物项id数组查询购物项明细", notes = "确认订单页面使用")
    public Result<List<ZhongHeMallShoppingCartItemVO>> toSettle(Long[] cartItemIds, @TokenToMallUser MallUser loginMallUser) {
        logger.info("用户根据购物项id数组查询购物项明细接口     MallUser:{},ItemParam:{}", loginMallUser.toString(), Arrays.toString(cartItemIds));
        if (cartItemIds.length < 1) {
            ZhongHeMallException.fail("参数异常");
        }
        int priceTotal = 0;
        List<ZhongHeMallShoppingCartItemVO> itemsForSettle = zhongHeMallShoppingCartService.getCartItemsForSettle(Arrays.asList(cartItemIds), loginMallUser.getUserId());
        if (CollectionUtils.isEmpty(itemsForSettle)) {
            //无数据则抛出异常
            ZhongHeMallException.fail("参数异常");
        } else {
            //总价
            for (ZhongHeMallShoppingCartItemVO zhongHeMallShoppingCartItemVO : itemsForSettle) {
                priceTotal += zhongHeMallShoppingCartItemVO.getGoodsCount() * zhongHeMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 0) {
                ZhongHeMallException.fail("价格异常");
            }
        }
        return ResultGenerator.genSuccessResult(itemsForSettle);
    }
}
