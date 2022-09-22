package mall.dao;

import mall.entity.ZhongHeMallShoppingCartItem;
import mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhongHeMallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(ZhongHeMallShoppingCartItem record);

    int insertSelective(ZhongHeMallShoppingCartItem record);

    ZhongHeMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    ZhongHeMallShoppingCartItem selectByUserIdAndGoodsId(@Param("zhongHeMallUserId") Long zhongHeMallUserId, @Param("goodsId") Long goodsId);

    List<ZhongHeMallShoppingCartItem> selectByUserId(@Param("zhongHeMallUserId") Long zhongHeMallUserId, @Param("number") int number);

    List<ZhongHeMallShoppingCartItem> selectByUserIdAndCartItemIds(@Param("zhongHeMallUserId") Long zhongHeMallUserId, @Param("cartItemIds") List<Long> cartItemIds);

    int selectCountByUserId(Long zhongHeMallUserId);

    int updateByPrimaryKeySelective(ZhongHeMallShoppingCartItem record);

    int updateByPrimaryKey(ZhongHeMallShoppingCartItem record);

    int deleteBatch(List<Long> ids);

    List<ZhongHeMallShoppingCartItem> findMyZhongHeMallCartItems(PageQueryUtil pageUtil);

    int getTotalMyZhongHeMallCartItems(PageQueryUtil pageUtil);
}