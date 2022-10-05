package mall.api.mall.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ZhongHeMallNoticeVO implements Serializable {

    @ApiModelProperty("用户昵称")
    private String nickName;

    @ApiModelProperty("用户登录名")
    private String loginName;

    @ApiModelProperty("组织ID")
    private Long organizationId;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("发送者")
    private String sender;

    @ApiModelProperty("通知内容")
    private String notice;

    @ApiModelProperty("通知类型")
    private Byte noticeType;

    @ApiModelProperty("发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("阅读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date readTime;
}
