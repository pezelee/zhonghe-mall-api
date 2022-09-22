 
package mall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 库存修改所需实体
 */

@Data
@AllArgsConstructor
public class PointDTO implements Comparable<PointDTO>{
    private Date expiretime;

    private Integer point;

    public PointDTO() {

    }

    @Override
    public int compareTo(PointDTO o) {
        long s = o.getExpiretime().getTime()-this.getExpiretime().getTime();
        TimeUnit time = TimeUnit.DAYS;
        long days = time.convert(s, TimeUnit.MILLISECONDS);   //days即相差天数
        return (int) days;
    }
}
