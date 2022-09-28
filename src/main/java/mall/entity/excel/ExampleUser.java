package mall.entity.excel;

import lombok.Data;
import mall.util.ExcelExport;

@Data
public class ExampleUser {

    @ExcelExport(value = "登录名(手机号)",example = "12309877890")
    private String loginUserName;

    @ExcelExport(value = "用户昵称",example = "张珊")
    private String nickName;

    @ExcelExport(value = "用户密码",example = "123456")
    private String loginPassword;
}
