package mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 保存订单param
 */
@Data
public class SaveLotteryDrawParam implements Serializable {

    @ApiModelProperty("抽奖记录id数组")
    private Long lotteryDrawId;

    @ApiModelProperty("地址id")
    private Long addressId;
}
