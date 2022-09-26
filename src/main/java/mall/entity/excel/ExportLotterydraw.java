package mall.entity.excel;

import lombok.Data;
import mall.util.ExcelExport;

import java.util.Date;

@Data
public class ExportLotterydraw {

    private Long lotteryDrawId;

    private Long userId;

    @ExcelExport(value = "登录手机号")
    private String loginName;

    @ExcelExport(value = "用户昵称")
    private String nickName;

    private String sponsor;//客户经理号

    private Long organizationId;

    @ExcelExport(value = "承办分行")
    private String orgName;//所属组织名称

    private Long activityId;

    @ExcelExport(value = "活动名称")
    private String activityName;

    @ExcelExport(value = "抽奖时间")
    private Date drawTime;//抽奖时间

    @ExcelExport(value = "过期时间")
    private Date expireTime;//过期时间

    @ExcelExport(value = "订单支付状态",kv = "0:待抽奖;1:已抽奖;2:待发送;3:发送中;4:发送完成;5:已接收;-1:抽奖失败;-2:用户关闭;-3:商家关闭")
    private Byte status;

    private Long prizeId;//抽到奖品

    @ExcelExport(value = "奖品名称")
    private String prizeName;

    @ExcelExport(value = "积分值")
    private Integer prizeValue;

    @ExcelExport(value = "发放方式",kv = "0:实物邮寄;1:实物现场;2:积分;3:会员卡")
    private Byte prizeType;

    @ExcelExport(value = "邮寄单号")
    private String mailNo;

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
