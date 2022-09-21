/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.dao;

import mall.entity.StockNumDTO;
import mall.entity.ZhongHeMallGoods;
import mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhongHeMallGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(ZhongHeMallGoods record);

    int insertSelective(ZhongHeMallGoods record);

    ZhongHeMallGoods selectByPrimaryKey(Long goodsId);

    ZhongHeMallGoods selectByCategoryIdAndName(@Param("goodsName") String goodsName, @Param("goodsCategoryId") Long goodsCategoryId);

    int updateByPrimaryKeySelective(ZhongHeMallGoods record);

    int updateByPrimaryKeyWithBLOBs(ZhongHeMallGoods record);

    int updateByPrimaryKey(ZhongHeMallGoods record);

    List<ZhongHeMallGoods> findZhongHeMallGoodsList(PageQueryUtil pageUtil);

    int getTotalZhongHeMallGoods(PageQueryUtil pageUtil);

    List<ZhongHeMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<ZhongHeMallGoods> findZhongHeMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalZhongHeMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("zhongHeMallGoodsList") List<ZhongHeMallGoods> zhongHeMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int cancelStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}