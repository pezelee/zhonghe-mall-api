 
package mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 13
 * @qq交流群 796794009
 * @email 2449207463@qq.com
 */
@MapperScan("mall.dao")
@SpringBootApplication
public class ZhongHeMallAPIApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhongHeMallAPIApplication.class, args);
    }

}
