/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪抽奖记录，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class OrderMailParam {

    @ApiModelProperty("待修改订单id")
    @NotNull(message = "订单id不能为空")
    @Min(value = 1, message = "订单id不能为空")
    private Long orderId;

    @ApiModelProperty("待修改邮寄单号")
    @NotEmpty(message = "邮寄单号不能为空")
    @Length(max = 128,message = "抽奖记录名称内容过长")
    private String mailNo;

//    @ApiModelProperty("待修改抽奖记录状态:0.待抽奖 1.已抽奖 2.待发送 3:发送中 4.发送完成")
//    private Byte status;
}