package mall.entity.excel;

import lombok.Data;
import mall.util.ExcelImport;

@Data
public class ImportLotterydraw {

    private int rowNum;

    @ExcelImport(value = "抽奖编号", required = true,unique = true,notE = true)//长数字字符串同phone
    private Long lotteryDrawId;

    @ExcelImport(value = "邮寄单号" , required = true,notE = true)
    private String mailNo;

    private String rowTips;//错误提示
    private String rowData;//原始数据
}
