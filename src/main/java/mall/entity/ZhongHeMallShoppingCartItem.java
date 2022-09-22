 
package mall.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ZhongHeMallShoppingCartItem {
    private Long cartItemId;

    private Long userId;

    private Long goodsId;

    private Integer goodsCount;

    private Integer sellingPrice;

    private Integer sellingPoint;

    private Byte isDeleted;

    private Date createTime;

    private Date updateTime;
}