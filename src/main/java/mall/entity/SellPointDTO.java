 
package mall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 库存修改所需实体
 */

@Data
@AllArgsConstructor
public class SellPointDTO {
    private Integer price;

    private Integer point;


    public SellPointDTO() {

    }
}
