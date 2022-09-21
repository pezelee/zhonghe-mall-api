/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
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