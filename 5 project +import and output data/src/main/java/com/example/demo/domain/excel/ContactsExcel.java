package com.example.demo.domain.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ContactsExcel {
    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("学号")
    private String studentId;

    @ExcelProperty("手机号码")
    private String phone;

    @ExcelProperty("地址")
    private String address;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("性别")
    private String sex;
} 