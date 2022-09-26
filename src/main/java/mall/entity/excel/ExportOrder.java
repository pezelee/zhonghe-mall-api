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

    @ExcelExport(value = "订单价格")
    private Integer totalPrice;

    @ExcelExport(value = "订单积分")
    private Integer totalPoint;


    private Byte payStatus;
    @ExcelExport(value = "订单支付状态")
    private String payStatusString;

    private Byte payType;
    @ExcelExport(value = "订单支付方式")
    private String payTypeString;

    private Byte orderStatus;
    @ExcelExport(value = "订单发送状态")
    private String orderStatusString;

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
