package com.example.demo.domain.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import lombok.Data;

import static com.alibaba.excel.enums.poi.HorizontalAlignmentEnum.CENTER;

@Data
@HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
@ContentStyle(horizontalAlignment = CENTER)
@ColumnWidth(20)
@HeadRowHeight(25)
@ContentRowHeight(20)
public class ContactsExcel {
    @ExcelProperty(value = "姓名")
    private String name;

    @ExcelProperty(value = "学号")
    private String studentId;

    @ExcelProperty(value = "手机号码")
    @ColumnWidth(25)
    private String phone;

    @ExcelProperty(value = "地址")
    @ColumnWidth(40)
    private String address;

    @ExcelProperty(value = "邮箱")
    @ColumnWidth(30)
    private String email;

    @ExcelProperty(value = "性别")
    @ColumnWidth(15)
    private String sex;
} 