 
package mall.service.impl;

import mall.api.mall.param.SaveCartItemParam;
import mall.api.mall.param.UpdateCartItemParam;
import mall.api.mall.vo.ZhongHeMallShoppingCartItemVO;
import mall.common.Constants;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallException;
import mall.dao.ZhongHeMallGoodsMapper;
import mall.dao.ZhongHeMallShoppingCartItemMapper;
import mall.entity.ZhongHeMallGoods;
import mall.entity.ZhongHeMallShoppingCartItem;
import mall.service.ZhongHeMallShoppingCartService;
import mall.util.BeanUtil;
import mall.util.PageQueryUtil;
import mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ZhongHeMallShoppingCartServiceImpl implements ZhongHeMallShoppingCartService {

    @Autowired
    private ZhongHeMallShoppingCartItemMapper zhongHeMallShoppingCartItemMapper;

    @Autowired
    private ZhongHeMallGoodsMapper zhongHeMallGoodsMapper;

    @Override
    public String saveZhongHeMallCartItem(SaveCartItemParam saveCartItemParam, Long userId) {
        ZhongHeMallShoppingCartItem temp = zhongHeMallShoppingCartItemMapper.selectByUserIdAndGoodsId(userId, saveCartItemParam.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            ZhongHeMallException.fail(ServiceResultEnum.SHOPPING_CART_ITEM_EXIST_ERROR.getResult());
        }
        ZhongHeMallGoods zhongHeMallGoods = zhongHeMallGoodsMapper.selectByPrimaryKey(saveCartItemParam.getGoodsId());
        //商品为空
        if (zhongHeMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if (saveCartItemParam.getGoodsCount() < 1) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_NUMBER_ERROR.getResult();
        }
        //超出单个商品的最大数量
        if (saveCartItemParam.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //商品库存不足
        if (zhongHeMallGoods.getStockNum() <= saveCartItemParam.getGoodsCount()) {
            return ServiceResultEnum.GOODS_NUM_LESS.getResult();
        }
        int totalItem = zhongHeMallShoppingCartItemMapper.selectCountByUserId(userId);
        //超出最大数量
        if (totalItem >= Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        ZhongHeMallShoppingCartItem zhongHeMallShoppingCartItem = new ZhongHeMallShoppingCartItem();
        BeanUtil.copyProperties(saveCartItemParam, zhongHeMallShoppingCartItem);
        zhongHeMallShoppingCartItem.setUserId(userId);
        //保存记录
        if (zhongHeMallShoppingCartItemMapper.insertSelective(zhongHeMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateZhongHeMallCartItem(UpdateCartItemParam updateCartItemParam, Long userId) {
        ZhongHeMallShoppingCartItem zhongHeMallShoppingCartItemUpdate = zhongHeMallShoppingCartItemMapper.selectByPrimaryKey(updateCartItemParam.getCartItemId());
        if (zhongHeMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (!zhongHeMallShoppingCartItemUpdate.getUserId().equals(userId)) {
            ZhongHeMallException.fail(ServiceResultEnum.REQUEST_FORBIDEN_ERROR.getResult());
        }
        //超出单个商品的最大数量
        if (updateCartItemParam.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //当前登录账号的userId与待修改的cartItem中userId不同，返回错误
        if (!zhongHeMallShoppingCartItemUpdate.getUserId().equals(userId)) {
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        }
        //数值相同，则不执行数据操作
        if (updateCartItemParam.getGoodsCount().equals(zhongHeMallShoppingCartItemUpdate.getGoodsCount())) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        zhongHeMallShoppingCartItemUpdate.setGoodsCount(updateCartItemParam.getGoodsCount());
        zhongHeMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (zhongHeMallShoppingCartItemMapper.updateByPrimaryKeySelective(zhongHeMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public ZhongHeMallShoppingCartItem getZhongHeMallCartItemById(Long zhongHeMallShoppingCartItemId) {
        ZhongHeMallShoppingCartItem zhongHeMallShoppingCartItem = zhongHeMallShoppingCartItemMapper.selectByPrimaryKey(zhongHeMallShoppingCartItemId);
        if (zhongHeMallShoppingCartItem == null) {
            ZhongHeMallException.fail(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return zhongHeMallShoppingCartItem;
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId, Long userId) {
        ZhongHeMallShoppingCartItem zhongHeMallShoppingCartItem = zhongHeMallShoppingCartItemMapper.selectByPrimaryKey(shoppingCartItemId);
        if (zhongHeMallShoppingCartItem == null) {
            return false;
        }
        //userId不同不能删除
        if (!userId.equals(zhongHeMallShoppingCartItem.getUserId())) {
            return false;
        }
        return zhongHeMallShoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }

    @Override
    public List<ZhongHeMallShoppingCartItemVO> getMyShoppingCartItems(Long zhongHeMallUserId) {
        List<ZhongHeMallShoppingCartItemVO> zhongHeMallShoppingCartItemVOS = new ArrayList<>();
        List<ZhongHeMallShoppingCartItem> zhongHeMallShoppingCartItems = zhongHeMallShoppingCartItemMapper.selectByUserId(zhongHeMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        return getZhongHeMallShoppingCartItemVOS(zhongHeMallShoppingCartItemVOS, zhongHeMallShoppingCartItems);
    }

    @Override
    public List<ZhongHeMallShoppingCartItemVO> getCartItemsForSettle(List<Long> cartItemIds, Long zhongHeMallUserId) {
        List<ZhongHeMallShoppingCartItemVO> zhongHeMallShoppingCartItemVOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(cartItemIds)) {
            ZhongHeMallException.fail("购物项不能为空");
        }
        List<ZhongHeMallShoppingCartItem> zhongHeMallShoppingCartItems = zhongHeMallShoppingCartItemMapper.selectByUserIdAndCartItemIds(zhongHeMallUserId, cartItemIds);
        if (CollectionUtils.isEmpty(zhongHeMallShoppingCartItems)) {
            ZhongHeMallException.fail("购物项不能为空");
        }
        if (zhongHeMallShoppingCartItems.size() != cartItemIds.size()) {
            ZhongHeMallException.fail("参数异常");
        }
        return getZhongHeMallShoppingCartItemVOS(zhongHeMallShoppingCartItemVOS, zhongHeMallShoppingCartItems);
    }

    /**
     * 数据转换
     *
     * @param zhongHeMallShoppingCartItemVOS
     * @param zhongHeMallShoppingCartItems
     * @return
     */
    private List<ZhongHeMallShoppingCartItemVO> getZhongHeMallShoppingCartItemVOS(List<ZhongHeMallShoppingCartItemVO> zhongHeMallShoppingCartItemVOS, List<ZhongHeMallShoppingCartItem> zhongHeMallShoppingCartItems) {
        if (!CollectionUtils.isEmpty(zhongHeMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> zhongHeMallGoodsIds = zhongHeMallShoppingCartItems.stream().map(ZhongHeMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<ZhongHeMallGoods> zhongHeMallGoods = zhongHeMallGoodsMapper.selectByPrimaryKeys(zhongHeMallGoodsIds);
            Map<Long, ZhongHeMallGoods> zhongHeMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(zhongHeMallGoods)) {
                zhongHeMallGoodsMap = zhongHeMallGoods.stream().collect(Collectors.toMap(ZhongHeMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (ZhongHeMallShoppingCartItem zhongHeMallShoppingCartItem : zhongHeMallShoppingCartItems) {
                ZhongHeMallShoppingCartItemVO zhongHeMallShoppingCartItemVO = new ZhongHeMallShoppingCartItemVO();
                BeanUtil.copyProperties(zhongHeMallShoppingCartItem, zhongHeMallShoppingCartItemVO);
                if (zhongHeMallGoodsMap.containsKey(zhongHeMallShoppingCartItem.getGoodsId())) {
                    ZhongHeMallGoods zhongHeMallGoodsTemp = zhongHeMallGoodsMap.get(zhongHeMallShoppingCartItem.getGoodsId());
                    zhongHeMallShoppingCartItemVO.setGoodsCoverImg(zhongHeMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = zhongHeMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    zhongHeMallShoppingCartItemVO.setGoodsName(goodsName);
//                    zhongHeMallShoppingCartItemVO.setSellingPrice(zhongHeMallGoodsTemp.getSellingPrice());
                    zhongHeMallShoppingCartItemVOS.add(zhongHeMallShoppingCartItemVO);
                }
            }
        }
        return zhongHeMallShoppingCartItemVOS;
    }

    @Override
    public PageResult getMyShoppingCartItems(PageQueryUtil pageUtil) {
        List<ZhongHeMallShoppingCartItemVO> zhongHeMallShoppingCartItemVOS = new ArrayList<>();
        List<ZhongHeMallShoppingCartItem> zhongHeMallShoppingCartItems = zhongHeMallShoppingCartItemMapper.findMyZhongHeMallCartItems(pageUtil);
        int total = zhongHeMallShoppingCartItemMapper.getTotalMyZhongHeMallCartItems(pageUtil);
        PageResult pageResult = new PageResult(getZhongHeMallShoppingCartItemVOS(zhongHeMallShoppingCartItemVOS, zhongHeMallShoppingCartItems), total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
