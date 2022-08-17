package pers.lzy.template.excel.core;

import org.apache.poi.ss.usermodel.Sheet;
import pers.lzy.template.excel.provider.FillDataProvider;
import pers.lzy.template.excel.provider.SheetProvider;

import java.util.Map;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/24  10:03
 */
public interface TemplateExcelFiller {

    /**
     * 给sheet填充对应的数据，根据模板
     *
     * @param sheetProvider sheet 提供者
     * @param dataProvider  数据提供着
     */
    void fillData(SheetProvider sheetProvider, FillDataProvider dataProvider);


    /**
     * 给sheet填充对应的数据，根据模板
     *
     * @param sheet     被填充的sheet
     * @param paramData 数据
     */
    default void fillData(Sheet sheet, Map<String, Object> paramData) {
        fillData(() -> sheet, () -> paramData);
    }
}
