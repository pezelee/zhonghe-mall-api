 
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class NoticeAddParam implements Serializable {

    @ApiModelProperty("标题")
    @NotEmpty(message = "登录名不能为空")
    private String title;

    @ApiModelProperty("通知内容")
    @NotEmpty(message = "密码不能为空")
    private String sender;

    @ApiModelProperty("通知内容")
    @NotEmpty(message = "密码不能为空")
    private String notice;

    @ApiModelProperty("通知种类")
    @NotNull(message = "种类(0-抽奖通知 1-订单通知 2-个人信息通知)")
    private Byte noticeType;
}
