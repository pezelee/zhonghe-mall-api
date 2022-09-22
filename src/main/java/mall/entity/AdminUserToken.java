package mall.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AdminUserToken {
    private Long adminUserId;

    private String token;

    private Long organizationId;

    private Byte role;

    private Date updateTime;

    private Date expireTime;
}