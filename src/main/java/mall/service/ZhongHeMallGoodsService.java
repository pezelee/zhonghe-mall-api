 
package mall.service;

import mall.entity.ZhongHeMallGoods;
import mall.util.PageQueryUtil;
import mall.util.PageResult;

import java.util.List;

public interface ZhongHeMallGoodsService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getZhongHeMallGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String saveZhongHeMallGoods(ZhongHeMallGoods goods);

    /**
     * 批量新增商品数据
     *
     * @param zhongHeMallGoodsList
     * @return
     */
    void batchSaveZhongHeMallGoods(List<ZhongHeMallGoods> zhongHeMallGoodsList);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updateZhongHeMallGoods(ZhongHeMallGoods goods);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids, int sellStatus);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    ZhongHeMallGoods getZhongHeMallGoodsById(Long id);

    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResult searchZhongHeMallGoods(PageQueryUtil pageUtil);
}
