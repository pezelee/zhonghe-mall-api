package mall.entity.excel;

import lombok.Data;
import mall.util.ExcelExport;

@Data
public class ExampleOrder {

    @ExcelExport(value = "订单号",example = "18293277322323")
    private String orderNo;

    @ExcelExport(value = "邮寄单号",example = "EC32198323723DDG")
    private String mailNo;
}
