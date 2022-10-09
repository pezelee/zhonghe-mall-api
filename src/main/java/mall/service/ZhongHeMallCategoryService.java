 
package mall.service;

import mall.api.mall.vo.ZhongHeMallIndexCategoryVO;
import mall.entity.GoodsCategory;
import mall.entity.excel.ExportCategory;
import mall.entity.excel.ExportOrder;
import mall.util.PageQueryUtil;
import mall.util.PageResult;

import java.util.List;

public interface ZhongHeMallCategoryService {

    String saveCategory(GoodsCategory goodsCategory);

    String updateGoodsCategory(GoodsCategory goodsCategory);

    GoodsCategory getGoodsCategoryById(Long id);

    Boolean deleteBatch(Long[] ids);

    /**
     * 返回分类数据(首页调用)
     *
     * @return
     */
    List<ZhongHeMallIndexCategoryVO> getCategoriesForIndex();

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getCategorisPage(PageQueryUtil pageUtil);

    /**
     * 后台导出
     *
     * @return
     */
    List<ExportCategory> getCategorisExport();

    /**
     * 根据parentId和level获取分类列表
     *
     * @param parentIds
     * @param categoryLevel
     * @return
     */
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);
}
