package mall.entity.excel;

import mall.util.ExcelImport;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ImportUser {

    private int rowNum;

    @ExcelImport(value = "登录名",maxLength = 11, required = true,unique = true,phone = true)
    private String loginUserName;

    @ExcelImport(value = "用户昵称" ,required = true,unique = true)
    private String nickName;

    @ExcelImport(value = "用户密码" ,required = true)
    private String loginPassword;

    private String rowTips;//错误提示
    private String rowData;//原始数据
}
