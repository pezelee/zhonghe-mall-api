 
package mall.service.impl;

import mall.api.mall.vo.ZhongHeMallSearchGoodsVO;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallCategoryLevelEnum;
import mall.common.ZhongHeMallException;
import mall.dao.GoodsCategoryMapper;
import mall.dao.ZhongHeMallGoodsMapper;
import mall.entity.*;
import mall.service.ZhongHeMallGoodsService;
import mall.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ZhongHeMallGoodsServiceImpl implements ZhongHeMallGoodsService {

    @Autowired
    private ZhongHeMallGoodsMapper goodsMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getZhongHeMallGoodsPage(PageQueryUtil pageUtil) {
        List<ZhongHeMallGoods> goodsList = goodsMapper.findZhongHeMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalZhongHeMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveZhongHeMallGoods(ZhongHeMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != ZhongHeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        if (goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId()) != null) {
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        //最小兑换积分
        List<SellPointDTO> sellPointDTOS = GoodsUtils.toDTOList(goods.getSellingPoint());
        int minPoint = GoodsUtils.minPoint(sellPointDTOS);
        goods.setMinPoint(minPoint);
        goods.setVolume(0);
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveZhongHeMallGoods(List<ZhongHeMallGoods> zhongHeMallGoodsList) {
        if (!CollectionUtils.isEmpty(zhongHeMallGoodsList)) {
            goodsMapper.batchInsert(zhongHeMallGoodsList);
        }
    }

    @Override
    public String updateZhongHeMallGoods(ZhongHeMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != ZhongHeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        ZhongHeMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        ZhongHeMallGoods temp2 = goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId());
        if (temp2 != null && !temp2.getGoodsId().equals(goods.getGoodsId())) {
            //name和分类id相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        //修改兑换积分时，同时修改最小兑换积分
        String sellPoint = goods.getSellingPoint();
        List<SellPointDTO> sellPointDTOS;
        if (sellPoint != null) {
            sellPointDTOS = GoodsUtils.toDTOList(sellPoint);
        }else {
            sellPointDTOS= GoodsUtils.toDTOList(temp.getSellingPoint());
        }
        int minPoint = GoodsUtils.minPoint(sellPointDTOS);
        goods.setMinPoint(minPoint);
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public ZhongHeMallGoods getZhongHeMallGoodsById(Long id) {
        ZhongHeMallGoods zhongHeMallGoods = goodsMapper.selectByPrimaryKey(id);
        if (zhongHeMallGoods == null) {
            ZhongHeMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        return zhongHeMallGoods;
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchZhongHeMallGoods(PageQueryUtil pageUtil) {

        List<ZhongHeMallGoods> goodsList = goodsMapper.findZhongHeMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalZhongHeMallGoodsBySearch(pageUtil);
        List<ZhongHeMallSearchGoodsVO> zhongHeMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            zhongHeMallSearchGoodsVOS = BeanUtil.copyList(goodsList, ZhongHeMallSearchGoodsVO.class);
            for (ZhongHeMallSearchGoodsVO zhongHeMallSearchGoodsVO : zhongHeMallSearchGoodsVOS) {
                String goodsName = zhongHeMallSearchGoodsVO.getGoodsName();
                String goodsIntro = zhongHeMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    zhongHeMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    zhongHeMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(zhongHeMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
