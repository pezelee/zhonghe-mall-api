
package mall.service;

import mall.api.admin.param.LotterydrawMailParam;
import mall.entity.*;
import mall.entity.excel.ExportLotterydraw;
import mall.entity.excel.ImportLotterydraw;
import mall.util.PageQueryUtil;
import mall.util.PageResult;

import java.util.List;

public interface LotterydrawService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getLotteryDrawPage(PageQueryUtil pageUtil);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    List<ExportLotterydraw> getLotteryDrawExport(PageQueryUtil pageUtil);

    /**
     * 积分收入列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getPointList(PageQueryUtil pageUtil);

    /**
     * 添加抽奖记录
     *
     * @param lotteryDraw
     * @return
     */
    String saveLotteryDraw(LotteryDraw lotteryDraw);

    /**
     * 修改抽奖记录
     *
     *
     * @param userId
     * @param prize
     * @return
     */
    LotteryDraw updateDrawResult(Long userId,ZhongHeMallPrize prize,Activity activity);



    /**
     * 修改抽奖记录
     *
     * @param lotteryDraw
     * @return
     */
    String updateLotteryDraw(LotteryDraw lotteryDraw);

    /**
     * 批量修改状态()
     *
     * @param ids
     * @param status
     * @return
     */
    Boolean batchUpdateStatus(Long[] ids, int status, Long adminId);

    /**
     * 获取抽奖记录详情
     *
     * @param id
     * @return
     */
    LotteryDraw getLotteryDrawById(Long id);

    /**
     * 根据用户ID获取抽奖记录列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getLotteryDrawByUserId(PageQueryUtil pageUtil);

    /**
     * 用户领取VIP卡,从已抽奖变成待发送
     *
     * @param lotteryDrawId
     * @return
     */
    String receiveVIP(Long lotteryDrawId);

    /**
     * 填写地址，变成待发送
     *
     * @param id
     * @param address
     * @return
     */
    String saveAddress(Long id, MallUserAddress address);

    /**
     * 查询奖品邮寄地址
     *
     * @param id
     * @return
     */
    LotteryDrawAddress getAddress(Long id);

    /**
     * 变成已发送
     *
     *
     * @param mailParam @return
     * @return
     */
    String sending(LotterydrawMailParam mailParam);

    /**
     * 发送完成
     *
     * @param id
     * @return
     */
    Boolean sendOver(Long id);

    /**
     * 变成已接收
     *
     * @param id
     * @return
     */
    Boolean received(Long id);

    /**
     * 从导入批量添加邮寄单号
     *
     * @param lotterydraw
     * @return
     */
    String setMailNoImport(ImportLotterydraw lotterydraw);



    /**
     * 抽奖记录搜索
     *
     * @param pageUtil
     * @return
     */
//    PageResult searchLotteryDraw(PageQueryUtil pageUtil);
}
