package pers.lzy.template.excel.anno;

import java.lang.annotation.*;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/23  17:15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CellOperateHandler {

    /**
     * The cell that identifies which tag this processor is used to process
     *
     * @return cell tag name
     */
    String tagName();
}
