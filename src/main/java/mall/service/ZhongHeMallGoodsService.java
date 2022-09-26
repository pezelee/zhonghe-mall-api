 
package mall.service;

import mall.entity.AdminUserToken;
import mall.entity.ZhongHeMallGoods;
import mall.entity.excel.ImportGoods;
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
     * @param adminUser
     * @return
     */
    String saveZhongHeMallGoods(ZhongHeMallGoods goods, AdminUserToken adminUser);

    /**
     * 导入时添加商品
     *
     * @param importGoodsgoods
     * @param adminUser
     * @return
     */
    String importGoods(ImportGoods importGoodsgoods, AdminUserToken adminUser);

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
     * @param adminUser
     * @return
     */
    String updateZhongHeMallGoods(ZhongHeMallGoods goods, AdminUserToken adminUser);

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
