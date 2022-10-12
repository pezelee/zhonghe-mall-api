package mall.entity.excel;

import lombok.Data;
import mall.util.ExcelExport;

import java.util.Date;

@Data
public class ExportOrder {

    private Long orderId;

    private Long organizationId;

    private Long userId;

    @ExcelExport(value = "订单号")
    private String orderNo;

    @ExcelExport(value = "登录手机号")
    private String loginName;

    @ExcelExport(value = "用户昵称")
    private String nickName;

    @ExcelExport(value = "订单价格(分)")
    private Integer totalPrice;

    @ExcelExport(value = "订单积分")
    private Integer totalPoint;


    @ExcelExport(value = "订单支付状态",kv = "-1:支付失败;0:支付中;1:支付成功")
    private Byte payStatus;
//    private String payStatusString;

    @ExcelExport(value = "订单支付方式",kv = "0:无;1:支付宝;2:微信支付;3:积分支付")
    private Byte payType;
//    private String payTypeString;

    @ExcelExport(value = "订单发送状态",kv = "0:待支付;1:已支付;2:配货完成;3:出库成功;4:交易成功;-1:手动关闭;-2:超时关闭;-3:商家关闭")
    private Byte orderStatus;
//    private String orderStatusString;

    @ExcelExport(value = "订单支付时间")
    private Date payTime;

    @ExcelExport(value = "邮寄单号")
    private String mailNo;

    private Date createTime;

    private Date updateTime;

    @ExcelExport(value = "收件人名称")
    private String userName;

    @ExcelExport(value = "收件人联系方式")
    private String userPhone;

    @ExcelExport(value = "省")
    private String provinceName;

    @ExcelExport(value = "市")
    private String cityName;

    @ExcelExport(value = "区/县")
    private String regionName;

    @ExcelExport(value = "详细地址")
    private String detailAddress;
}
