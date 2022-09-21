/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.service;

import mall.entity.ZhongHeMallPrize;
import mall.util.PageQueryUtil;
import mall.util.PageResult;

import java.util.List;

public interface ZhongHeMallPrizeService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getZhongHeMallPrizePage(PageQueryUtil pageUtil);

    /**
     * 添加奖品
     *
     * @param Prize
     * @return
     */
    String saveZhongHeMallPrize(ZhongHeMallPrize Prize);

    /**
     * 批量新增商品数据
     *
     * @param zhongHeMallPrizeList
     * @return
     */
    void batchSaveZhongHeMallPrize(List<ZhongHeMallPrize> zhongHeMallPrizeList);

    /**
     * 修改商品信息
     *
     * @param Prize
     * @return
     */
    String updateZhongHeMallPrize(ZhongHeMallPrize Prize);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @param organizationId
     * @param role
     * @return
     */
    Boolean batchUpdateStatus(Long[] ids, int Status, Long adminId, Long organizationId,Byte role);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    ZhongHeMallPrize getZhongHeMallPrizeById(Long id);

    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResult searchZhongHeMallPrize(PageQueryUtil pageUtil);
}
