/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
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
