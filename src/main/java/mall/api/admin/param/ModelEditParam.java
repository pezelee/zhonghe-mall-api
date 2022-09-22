 
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ModelEditParam {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("待修改活动id")
    @NotNull(message = "活动id不能为空")
    private Long activityId;

    @ApiModelProperty("模板id")
    @NotNull(message = "模板id不能为空")
    private Long modelId;

    @ApiModelProperty("模板名称")
    @NotEmpty(message = "模板名称不能为空")
    @Length(max = 128,message = "模板名称内容过长")
    private String modelName;
}