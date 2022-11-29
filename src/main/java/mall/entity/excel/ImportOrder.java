package mall.entity.excel;

import lombok.Data;
import mall.util.ExcelImport;

@Data
public class ImportOrder {

    private int rowNum;

    private Long orderId;

    @ExcelImport(value = "订单号", required = true,unique = true,notE = true)//长数字字符串同phone
    private String orderNo;

    @ExcelImport(value = "邮寄单号" , required = true,notE = true)
    private String mailNo;

    private String rowTips;//错误提示
    private String rowData;//原始数据
}
