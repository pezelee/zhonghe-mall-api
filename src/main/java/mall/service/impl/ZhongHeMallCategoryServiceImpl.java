 
package mall.service.impl;

import mall.api.mall.vo.SecondLevelCategoryVO;
import mall.api.mall.vo.ThirdLevelCategoryVO;
import mall.api.mall.vo.ZhongHeMallIndexCategoryVO;
import mall.common.Constants;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallCategoryLevelEnum;
import mall.dao.GoodsCategoryMapper;
import mall.entity.GoodsCategory;
import mall.entity.excel.ExportCategory;
import mall.service.ZhongHeMallCategoryService;
import mall.util.BeanUtil;
import mall.util.PageQueryUtil;
import mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class ZhongHeMallCategoryServiceImpl implements ZhongHeMallCategoryService {

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public String saveCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp != null) {
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        if (goodsCategoryMapper.insertSelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateGoodsCategory(GoodsCategory goodsCategory) {
        GoodsCategory temp = goodsCategoryMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsCategory temp2 = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp2 != null && !temp2.getCategoryId().equals(goodsCategory.getCategoryId())) {
            //同名且不同id 不能继续修改
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        goodsCategory.setUpdateTime(new Date());
        if (goodsCategoryMapper.updateByPrimaryKeySelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public GoodsCategory getGoodsCategoryById(Long id) {
        return goodsCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除分类数据
        return goodsCategoryMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<ZhongHeMallIndexCategoryVO> getCategoriesForIndex() {
        List<ZhongHeMallIndexCategoryVO> zhongHeMallIndexCategoryVOS = new ArrayList<>();
        //获取一级分类的固定数量的数据
        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), ZhongHeMallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            List<Long> firstLevelCategoryIds = firstLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            //获取二级分类的数据
            List<GoodsCategory> secondLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(firstLevelCategoryIds, ZhongHeMallCategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                List<Long> secondLevelCategoryIds = secondLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                //获取三级分类的数据
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(secondLevelCategoryIds, ZhongHeMallCategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                    //根据 parentId 将 thirdLevelCategories 分组
                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream().collect(groupingBy(GoodsCategory::getParentId));
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    //处理二级分类
                    for (GoodsCategory secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                        //如果该二级分类下有数据则放入 secondLevelCategoryVOS 对象中
                        if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())) {
                            //根据二级分类的id取出thirdLevelCategoryMap分组中的三级分类list
                            List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.get(secondLevelCategory.getCategoryId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS((BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryVO.class)));
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }
                    //处理一级分类
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                        //根据 parentId 将 thirdLevelCategories 分组
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for (GoodsCategory firstCategory : firstLevelCategories) {
                            ZhongHeMallIndexCategoryVO zhongHeMallIndexCategoryVO = new ZhongHeMallIndexCategoryVO();
                            BeanUtil.copyProperties(firstCategory, zhongHeMallIndexCategoryVO);
                            //如果该一级分类下有数据则放入 zhongHeMallIndexCategoryVOS 对象中
                            if (secondLevelCategoryVOMap.containsKey(firstCategory.getCategoryId())) {
                                //根据一级分类的id取出secondLevelCategoryVOMap分组中的二级级分类list
                                List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.get(firstCategory.getCategoryId());
                                zhongHeMallIndexCategoryVO.setSecondLevelCategoryVOS(tempGoodsCategories);
                                zhongHeMallIndexCategoryVOS.add(zhongHeMallIndexCategoryVO);
                            }
                        }
                    }
                }
            }
            return zhongHeMallIndexCategoryVOS;
        } else {
            return null;
        }
    }

    @Override
    public PageResult getCategorisPage(PageQueryUtil pageUtil) {
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.findGoodsCategoryList(pageUtil);
        int total = goodsCategoryMapper.getTotalGoodsCategories(pageUtil);
        PageResult pageResult = new PageResult(goodsCategories, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public List<ExportCategory> getCategorisExport() {
        List<ExportCategory> lv1Categories = goodsCategoryMapper.findGoodsCategoryExport((byte) 1);//一级分类
        List<ExportCategory> lv2Categories = goodsCategoryMapper.findGoodsCategoryExport((byte) 2);//二级分类
        List<ExportCategory> lv3Categories = goodsCategoryMapper.findGoodsCategoryExport((byte) 3);//三级分类
        List<ExportCategory> result = new ArrayList<>();
        for(ExportCategory category1:lv1Categories){
            category1.setRootId((long) 0);
            result.add(category1);
            Long rootId = category1.getCategoryId();
            for(ExportCategory category2:lv2Categories){
                if(category2.getParentId().equals(rootId)){
                    category2.setRootId(rootId);
                    result.add(category2);
                    Long parentId = category2.getCategoryId();
                    for(ExportCategory category3 : lv3Categories){
                        if(category3.getParentId().equals(parentId)){
                            category3.setRootId(rootId);
                            result.add(category3);
//                            lv3Categories.remove(category3);
                        }
                    }
//                    lv2Categories.remove(category2);
                }
            }
        }
        return result;
    }
    private Long getRootId(ExportCategory category,List<ExportCategory> lv1Categories,List<ExportCategory> lv2Categories){
        if(category.getCategoryLevel() == 1){
            return category.getCategoryId();
        }
        return (long) 0;
    }

    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        return goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(parentIds, categoryLevel, 0);//0代表查询所有
    }
}
