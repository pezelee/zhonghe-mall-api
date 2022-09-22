package mall.dao;

import mall.entity.*;
import mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LotteryDrawMapper {

    int batchInsert(@Param("LotteryDrawList") List<LotteryDraw> LotteryDrawList);

    LotteryDraw selectByPrimaryKey(Long lotteryDrawId);

    int batchUpdateStatus(@Param("lotteryDrawIds")Long[] lotteryDrawIds,@Param("status") int status);

    int deleteByPrimaryKey(Long lotteryDrawId);

    int insert(LotteryDraw record);

    int insertSelective(LotteryDraw record);

    int updateByPrimaryKeySelective(LotteryDraw record);

    int updateByPrimaryKey(LotteryDraw record);

    List<LotteryDraw> getPointList(PageQueryUtil pageUtil);

    int getTotalPointList(PageQueryUtil pageUtil);

    List<LotteryDraw> findLotteryDrawList(PageQueryUtil pageUtil);

    int getTotalLotteryDraw(PageQueryUtil pageUtil);

    List<LotteryDraw> findLotteryDrawListByOrg(PageQueryUtil pageUtil);

    int getTotalLotteryDrawByOrg(PageQueryUtil pageUtil);

    List<LotteryDraw> findLotteryDrawListByUserId(PageQueryUtil pageUtil);

    int getTotalLotteryDrawByUserId(PageQueryUtil pageUtil);

    int updateStockNum(@Param("stockNumDTO") PrizeStockNumDTO stockNumDTO);

    int toBeSent(@Param("lotteryDrawId") Long lotteryDrawId);

    int sending(LotteryDrawMail mail);//从待发送变成发送中 填写运单号

    int sendOver(@Param("lotteryDrawId") Long lotteryDrawId);

    int received(@Param("lotteryDrawId") Long lotteryDrawId);
}