package pers.lzy.template.excel.test.arr;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author immort-liuzyj(zyliu)
 * @since 2022/3/2  18:15
 */
public class MainTest {

    private TemplateExcelFiller templateExcelFiller;

    @Before
    public void before() {

        // 构建导出者
        templateExcelFiller = TemplateExcelFillerFactory.defaultTemplateExcelFillerBuilder()
                .expressionCacheSize(500)
                .functions("math", Math.class)
                .functions("StringUtils", StringUtils.class)
                .build();

    }

    @Test
    public void test() throws IOException, InvalidFormatException {

        Map<String, Object> param = initParam();
        //获取文件的URL
        URL url = this.getClass().getClassLoader().getResource("02MyScore.xlsx");
        assert url != null;

        Workbook workbook = new XSSFWorkbook(new File(url.getPath()));
        Sheet sheet = workbook.getSheetAt(0);


        // 调用我们的框架进行数据的填充
        templateExcelFiller.fillData(() -> sheet, () -> param);

        // 将Excel 写出去，查看结果
        ExcelUtil.writeFile(workbook, new File("C:\\Users\\immortal\\Desktop\\02MyScore1111.xlsx"));
    }

    private Map<String, Object> initParam() {


        Map<String, Object> res = new HashMap<>();
        ClassScore classScore = new ClassScore();
        res.put("classScore", classScore);

        classScore.setName("清华xx附属小学");
        classScore.setLevel("五年级一班");
        classScore.setPhone("15032000000");

        List<ClassScore.Score> scoreList = new ArrayList<>();
        scoreList.add(new ClassScore.Score("张三", 80D, 98D, 30D));
        scoreList.add(new ClassScore.Score("李四", 70D, 88D, 88D));
        scoreList.add(new ClassScore.Score("王五", 90D, 61D, 90D));
        scoreList.add(new ClassScore.Score("赵六", 86D, 78D, 78D));

        classScore.setScore(scoreList);
        return res;


    }
}
