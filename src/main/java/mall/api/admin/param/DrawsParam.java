 
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class DrawsParam implements Serializable {

    @ApiModelProperty("用户ID数组")
    @NotNull(message = "用户ID数组不能为空")
    Long[] ids;

    @ApiModelProperty("活动id")
    @NotNull(message = "活动id不能为空")
    Long activityId;

    @ApiModelProperty("分行id")
    @NotNull(message = "分行id不能为空")
    Long organizationId;
}
