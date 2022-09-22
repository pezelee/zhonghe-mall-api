package mall.dao;

import mall.entity.PrizeStockNumDTO;
import mall.entity.ZhongHeMallPrize;
import mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhongHeMallPrizeMapper {
    int deleteByPrimaryKey(Long prizeId);

    int insert(ZhongHeMallPrize record);

    int insertSelective(ZhongHeMallPrize record);

    ZhongHeMallPrize selectByPrimaryKey(Long prizeId);

    ZhongHeMallPrize selectByCategoryIdAndName(@Param("prizeName") String prizeName, @Param("prizeCategoryId") Long prizeCategoryId, @Param("organizationId") Long organizationId);

    int updateByPrimaryKeySelective(ZhongHeMallPrize record);

    int updateByPrimaryKeyWithBLOBs(ZhongHeMallPrize record);

    int updateByPrimaryKey(ZhongHeMallPrize record);

    List<ZhongHeMallPrize> findZhongHeMallPrizeList(PageQueryUtil pageUtil);

    List<ZhongHeMallPrize> findZhongHeMallPrizeListByOrg(PageQueryUtil pageUtil);

    int getTotalZhongHeMallPrize(PageQueryUtil pageUtil);

    int getTotalZhongHeMallPrizeByOrg(PageQueryUtil pageUtil);

    List<ZhongHeMallPrize> selectByPrimaryKeys(List<Long> prizeIds);

    List<ZhongHeMallPrize> findZhongHeMallPrizeListBySearch(PageQueryUtil pageUtil);

    int getTotalZhongHeMallPrizeBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("zhongHeMallPrizeList") List<ZhongHeMallPrize> zhongHeMallPrizeList);

//    boolean updateStockNum(@Param("stockNumDTO") PrizeStockNumDTO stockNumDTO);

    boolean updateStockNum( PrizeStockNumDTO stockNumDTO);

    int batchUpdateStatus(@Param("prizeIds")Long[] prizeIds,@Param("sellStatus") int sellStatus,@Param("adminId")  Long adminId, @Param("organizationId")  Long organizationId);

}