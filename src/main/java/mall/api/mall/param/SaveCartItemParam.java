package mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加购物项param
 */
@Data
public class SaveCartItemParam implements Serializable {

    @ApiModelProperty("商品数量")
    private Integer goodsCount;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品预计支付现金")
    private Integer sellingPrice;

    @ApiModelProperty("商品预计支付积分")
    private Integer sellingPoint;
}
