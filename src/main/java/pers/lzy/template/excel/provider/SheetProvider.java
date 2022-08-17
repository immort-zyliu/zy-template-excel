package pers.lzy.template.excel.provider;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/23  18:10
 */
@FunctionalInterface
public interface SheetProvider {

    /**
     * sheet提供
     * @return 要操作的sheet
     */
    Sheet getSheet();
}
