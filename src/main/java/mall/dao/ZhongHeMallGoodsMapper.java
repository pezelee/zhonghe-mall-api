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

    ZhongHeMallGoods selectByName(@Param("goodsName") String goodsName);

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