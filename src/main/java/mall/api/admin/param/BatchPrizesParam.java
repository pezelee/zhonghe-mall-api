 
package mall.api.admin.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BatchPrizesParam implements Serializable {

    @ApiModelProperty("待修改活动id")
    Long activityId;

//    @ApiModelProperty("奖品id数组")
//    String prizeIds;

    @ApiModelProperty("奖品id数组")
    Long[] prizeIds;

//    Long[] removeIds;
}
