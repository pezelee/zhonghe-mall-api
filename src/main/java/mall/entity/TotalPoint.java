 
package mall.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 库存修改所需实体
 */

@Data
public class TotalPoint {

    private Integer totalPoint;////总积分

    private Integer expirePoint;//过期积分

    private Date nextExpiretime;//

    private Integer nextPoint;//

    private List<PointDTO> PointDTOs;//
}
