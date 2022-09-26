package mall.entity.excel;

import lombok.Data;
import mall.util.ExcelImport;

@Data
public class ImportError {

    private int rowNum;
    private String rowTips;//错误提示
}
