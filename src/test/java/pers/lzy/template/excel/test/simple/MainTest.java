package pers.lzy.template.excel.test.simple;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import pers.lzy.template.excel.core.TemplateExcelFiller;
import pers.lzy.template.excel.core.TemplateExcelFillerFactory;
import pers.lzy.template.excel.utils.ExcelUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/2/28  17:08
 */
public class MainTest {

    private TemplateExcelFiller templateExcelFiller;

    @Before
    public void before() {

        // 构建导出着
        templateExcelFiller = TemplateExcelFillerFactory.defaultTemplateExcelFillerBuilder()
                .expressionCacheSize(500)
                .functions("math", Math.class)
                .functions("StringUtils", StringUtils.class)
                .build();

    }

    @Test
    public void test() throws IOException, InvalidFormatException {
        // 准备数据
        Map<String, Object> data = new HashMap<>();
        data.put("card", new Card("远子哥", "111000111", "zyliu99@foxmail.com", "家里蹲"));
        String jsonStr = JSON.toJSONString(data);
        System.out.println(jsonStr);

        // 准备Excel
        //获取文件的URL
        URL url = this.getClass().getClassLoader().getResource("01MyCard.xlsx");
        assert url != null;
        Workbook workbook = new XSSFWorkbook(new File(url.getPath()));
        Sheet sheet = workbook.getSheetAt(0);


        // 调用我们的框架进行数据的填充
        templateExcelFiller.fillData(() -> sheet, () -> data);

        // 将Excel 写出去，查看结果
        ExcelUtil.writeFile(workbook, new File("C:\\Users\\immortal\\Desktop\\01MyCardFilled.xlsx"));
    }


}
