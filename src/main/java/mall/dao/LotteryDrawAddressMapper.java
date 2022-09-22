package mall.dao;

import mall.entity.LotteryDrawAddress;

public interface LotteryDrawAddressMapper {
    int deleteByPrimaryKey(Long lotteryDrawId);

    int insert(LotteryDrawAddress record);

    int insertSelective(LotteryDrawAddress record);

    LotteryDrawAddress selectByPrimaryKey(Long lotteryDrawId);

    int updateByPrimaryKeySelective(LotteryDrawAddress record);

    int updateByPrimaryKey(LotteryDrawAddress record);
}