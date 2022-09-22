package mall.entity;

import lombok.Data;

@Data
public class LotteryDrawAddress {
    private Long lotteryDrawId;

    private String userName;

    private String userPhone;

    private String provinceName;

    private String cityName;

    private String regionName;

    private String detailAddress;
}