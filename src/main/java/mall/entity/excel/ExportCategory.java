package mall.entity.excel;

import lombok.Data;
import mall.util.ExcelExport;

import java.util.Date;

@Data
public class ExportCategory {
    private Long categoryId;


    @ExcelExport(value = "分类级别",kv = "1:一级分类;2:二级分类;3:三级分类")
    private Byte categoryLevel;

    private Long parentId;//父分类id

    private Long rootId;//根分类id

    @ExcelExport(value = "分类名称")
    private String categoryName;

    private Byte isDeleted;
}
