 
package mall.service.impl;

import mall.api.mall.vo.ZhongHeMallSearchPrizeVO;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallCategoryLevelEnum;
import mall.common.ZhongHeMallException;
import mall.dao.GoodsCategoryMapper;
import mall.dao.ZhongHeMallPrizeMapper;
import mall.entity.GoodsCategory;
import mall.entity.ZhongHeMallPrize;
import mall.service.ZhongHeMallPrizeService;
import mall.util.BeanUtil;
import mall.util.PageQueryUtil;
import mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ZhongHeMallPrizeServiceImpl implements ZhongHeMallPrizeService {

    @Autowired
    private ZhongHeMallPrizeMapper prizeMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getZhongHeMallPrizePage(PageQueryUtil pageUtil) {

        List<ZhongHeMallPrize> prizeList;
        int total;
        byte role = (byte) pageUtil.get("role");
        if (role == 0) {
            //总管理员
            prizeList = prizeMapper.findZhongHeMallPrizeList(pageUtil);
            total = prizeMapper.getTotalZhongHeMallPrize(pageUtil);
        }else {
            //分行人员
            prizeList = prizeMapper.findZhongHeMallPrizeListByOrg(pageUtil);
            total = prizeMapper.getTotalZhongHeMallPrizeByOrg(pageUtil);
        }

//        List<ZhongHeMallPrize> prizeList = prizeMapper.findZhongHeMallPrizeList(pageUtil);
//        int total = prizeMapper.getTotalZhongHeMallPrize(pageUtil);
        PageResult pageResult = new PageResult(prizeList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveZhongHeMallPrize(ZhongHeMallPrize prize) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(prize.getPrizeCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != ZhongHeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        if (prizeMapper.selectByCategoryIdAndName(prize.getPrizeName(), prize.getPrizeCategoryId(),prize.getOrganizationId()) != null) {
            return ServiceResultEnum.SAME_PRIZE_EXIST.getResult();
        }
        prize.setUpdateTime(new Date());
        prize.setCreateTime(new Date());
        if (prizeMapper.insertSelective(prize) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveZhongHeMallPrize(List<ZhongHeMallPrize> zhongHeMallPrizeList) {
        if (!CollectionUtils.isEmpty(zhongHeMallPrizeList)) {
            prizeMapper.batchInsert(zhongHeMallPrizeList);
        }
    }

    @Override
    public String updateZhongHeMallPrize(ZhongHeMallPrize prize) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(prize.getPrizeCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != ZhongHeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        ZhongHeMallPrize temp = prizeMapper.selectByPrimaryKey(prize.getPrizeId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        ZhongHeMallPrize temp2 = prizeMapper.selectByCategoryIdAndName(prize.getPrizeName(), prize.getPrizeCategoryId(),prize.getOrganizationId());
        if (temp2 != null && !temp2.getPrizeId().equals(prize.getPrizeId())) {
            //name和分类id相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_PRIZE_EXIST.getResult();
        }
        prize.setUpdateTime(new Date());
        if (prizeMapper.updateByPrimaryKeySelective(prize) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public ZhongHeMallPrize getZhongHeMallPrizeById(Long id) {
        ZhongHeMallPrize prize = prizeMapper.selectByPrimaryKey(id);
        if (prize == null) {
            ZhongHeMallException.fail(ServiceResultEnum.PRIZE_NOT_EXIST.getResult());
        }
        return prize;
    }

    @Override
    public Boolean batchUpdateStatus(Long[] ids, int sellStatus, Long adminId, Long organizationId, Byte role) {

        return prizeMapper.batchUpdateStatus(ids, sellStatus, adminId,organizationId) > 0;
    }

    @Override
    public PageResult searchZhongHeMallPrize(PageQueryUtil pageUtil) {
        List<ZhongHeMallPrize> prizeList = prizeMapper.findZhongHeMallPrizeListBySearch(pageUtil);
        int total = prizeMapper.getTotalZhongHeMallPrizeBySearch(pageUtil);
        List<ZhongHeMallSearchPrizeVO> zhongHeMallSearchPrizeVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(prizeList)) {
            zhongHeMallSearchPrizeVOS = BeanUtil.copyList(prizeList, ZhongHeMallSearchPrizeVO.class);
            for (ZhongHeMallSearchPrizeVO zhongHeMallSearchPrizeVO : zhongHeMallSearchPrizeVOS) {
                String goodsName = zhongHeMallSearchPrizeVO.getPrizeName();
                String goodsIntro = zhongHeMallSearchPrizeVO.getPrizeIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    zhongHeMallSearchPrizeVO.setPrizeName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    zhongHeMallSearchPrizeVO.setPrizeIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(zhongHeMallSearchPrizeVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
