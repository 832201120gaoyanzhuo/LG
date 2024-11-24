package com.example.demo.controller;

import com.example.demo.domain.ApiResult;
import com.example.demo.domain.entity.Contacts;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.example.demo.domain.excel.ContactsExcel;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contacts")
public class ContactsController {

    @Autowired
    private JdbcClient jdbcClient;

    // 分页查询
    @GetMapping("/page")
    public ApiResult page(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @ModelAttribute Contacts contacts) {

        StringBuilder sql = new StringBuilder("SELECT id, name, student_id AS studentId, phone, address, email, sex, top, favorite FROM contacts WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM contacts WHERE 1=1");

        // 构建模糊查询条件
        if (contacts.getName() != null && !contacts.getName().isEmpty()) {
            sql.append(" AND name LIKE concat('%',:name,'%')");
            countSql.append(" AND name LIKE concat('%',:name,'%')");
        }
        if (contacts.getStudentId() != null && !contacts.getStudentId().isEmpty()) {
            sql.append(" AND student_id LIKE concat('%',:studentId,'%')");
            countSql.append(" AND student_id LIKE concat('%',:studentId,'%')");
        }
        if (contacts.getPhone() != null && !contacts.getPhone().isEmpty()) {
            sql.append(" AND phone LIKE concat('%',:phone,'%')");
            countSql.append(" AND phone LIKE concat('%',:phone,'%')");
        }
        sql.append(" ORDER BY top DESC, id DESC");


        // 查询总数
        long total = jdbcClient.sql(countSql.toString())
                .param("name", contacts.getName())
                .param("studentId", contacts.getStudentId())
                .param("phone", contacts.getPhone())
                .query(Long.class)
                .single();

        // 分页查询
        sql.append(" LIMIT ").append(size).append(" OFFSET ").append((page - 1) * size);
        List<Contacts> records = jdbcClient.sql(sql.toString())
                .param("name", contacts.getName())
                .param("studentId", contacts.getStudentId())
                .param("phone", contacts.getPhone())
                .query(Contacts.class)
                .list();

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);

        return ApiResult.ok(result);
    }


    // 列表查询
    @GetMapping("/list")
    public ApiResult list(@ModelAttribute Contacts contacts) {
        StringBuilder sql = new StringBuilder("SELECT id, name, student_id AS studentId, phone, address, email, sex, top, favorite FROM contacts WHERE 1=1");

        if (contacts.getName() != null && !contacts.getName().isEmpty()) {
            sql.append(" AND name LIKE '%' || :name || '%'");
        }
        if (contacts.getStudentId() != null && !contacts.getStudentId().isEmpty()) {
            sql.append(" AND student_id LIKE '%' || :studentId || '%'");
        }
        if (contacts.getPhone() != null && !contacts.getPhone().isEmpty()) {
            sql.append(" AND phone LIKE '%' || :phone || '%'");
        }
        sql.append(" ORDER BY top DESC, id DESC");

        List<Contacts> list = jdbcClient.sql(sql.toString())
                .param("name", contacts.getName())
                .param("studentId", contacts.getStudentId())
                .param("phone", contacts.getPhone())
                .query(Contacts.class)
                .list();

        return ApiResult.ok(list);
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public ApiResult getById(@PathVariable Integer id) {
        Contacts contact = jdbcClient.sql("SELECT id, name, student_id AS studentId, phone, address, email, sex, top, favorite FROM contacts WHERE id = :id")
                .param("id", id)
                .query(Contacts.class)
                .single();

        return ApiResult.ok(contact);
    }

    // 新增
    @PostMapping
    public ApiResult add(@RequestBody Contacts contacts) {
        int result = jdbcClient.sql("INSERT INTO contacts (name, student_id, phone, address, email, sex) " +
                        "VALUES (:name, :studentId, :phone, :address, :email, :sex)")
                .param("name", contacts.getName())
                .param("studentId", contacts.getStudentId())
                .param("phone", contacts.getPhone())
                .param("address", contacts.getAddress())
                .param("email", contacts.getEmail())
                .param("sex", contacts.getSex())
                .update();

        return ApiResult.result(result);
    }

    // 修改
    @PutMapping
    public ApiResult update(@RequestBody Contacts contacts) {
        String sql = "UPDATE contacts SET name = :name, student_id = :studentId, phone = :phone, " +
                "address = :address, email = :email, sex = :sex";
        if (contacts.getTop() != null && !contacts.getTop().isEmpty()) {
            sql += ", top = :top";
        }
        sql += " WHERE id = :id";
        JdbcClient.StatementSpec spec = jdbcClient.sql(sql)
                .param("name", contacts.getName())
                .param("studentId", contacts.getStudentId())
                .param("phone", contacts.getPhone())
                .param("address", contacts.getAddress())
                .param("email", contacts.getEmail())
                .param("sex", contacts.getSex())
                .param("id", contacts.getId());
        if (contacts.getTop() != null && !contacts.getTop().isEmpty()) {
            spec.param("top", contacts.getTop());
        }
        int result = spec.update();
        return ApiResult.result(result);
    }

    // 删除
    @DeleteMapping("/{id}")
    public ApiResult delete(@PathVariable Integer id) {
        int result = jdbcClient.sql("DELETE FROM contacts WHERE id = :id")
                .param("id", id)
                .update();

        return ApiResult.result(result);
    }

    /**
     * 更新收藏状态
     */
    @PutMapping("/favorite/{id}")
    public ApiResult<Boolean> updateFavorite(@PathVariable Integer id, @RequestParam String favorite) {
        String sql = "UPDATE contacts SET favorite = :favorite WHERE id = :id";
        int rows = jdbcClient.sql(sql)
                .param("favorite", favorite)
                .param("id", id)
                .update();
        
        return ApiResult.result(rows);
    }

    /**
     * 获取收藏列表
     */
    @GetMapping("/favorites")
    public ApiResult<List<Contacts>> getFavorites() {
        String sql = "SELECT id, name, student_id AS studentId, phone, address, email, sex, top, favorite " +
                    "FROM contacts WHERE favorite = '1' ORDER BY id DESC";
        List<Contacts> favorites = jdbcClient.sql(sql)
                .query(Contacts.class)
                .list();
        
        return ApiResult.ok(favorites);
    }

    /**
     * 收藏列表分页查询
     */
    @GetMapping("/favorites/page")
    public ApiResult favoritesPage(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @ModelAttribute Contacts contacts) {

        StringBuilder sql = new StringBuilder("SELECT id, name, student_id AS studentId, phone, address, email, sex, top, favorite FROM contacts WHERE favorite = '1'");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM contacts WHERE favorite = '1'");

        // 构建模糊查询条件
        if (contacts.getName() != null && !contacts.getName().isEmpty()) {
            sql.append(" AND name LIKE concat('%',:name,'%')");
            countSql.append(" AND name LIKE concat('%',:name,'%')");
        }
        if (contacts.getStudentId() != null && !contacts.getStudentId().isEmpty()) {
            sql.append(" AND student_id LIKE concat('%',:studentId,'%')");
            countSql.append(" AND student_id LIKE concat('%',:studentId,'%')");
        }
        if (contacts.getPhone() != null && !contacts.getPhone().isEmpty()) {
            sql.append(" AND phone LIKE concat('%',:phone,'%')");
            countSql.append(" AND phone LIKE concat('%',:phone,'%')");
        }
        sql.append(" ORDER BY id DESC");

        // 查询总数
        long total = jdbcClient.sql(countSql.toString())
                .param("name", contacts.getName())
                .param("studentId", contacts.getStudentId())
                .param("phone", contacts.getPhone())
                .query(Long.class)
                .single();

        // 分页查询
        sql.append(" LIMIT ").append(size).append(" OFFSET ").append((page - 1) * size);
        List<Contacts> records = jdbcClient.sql(sql.toString())
                .param("name", contacts.getName())
                .param("studentId", contacts.getStudentId())
                .param("phone", contacts.getPhone())
                .query(Contacts.class)
                .list();

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);

        return ApiResult.ok(result);
    }

    /**
     * 导出Excel
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        // 查询所有数据
        List<Contacts> list = jdbcClient.sql("SELECT * FROM contacts ORDER BY id DESC")
                .query(Contacts.class)
                .list();

        // 转换为Excel对象
        List<ContactsExcel> excelList = list.stream().map(contact -> {
            ContactsExcel excel = new ContactsExcel();
            excel.setName(contact.getName());
            excel.setStudentId(contact.getStudentId());
            excel.setPhone(contact.getPhone());
            excel.setAddress(contact.getAddress());
            excel.setEmail(contact.getEmail());
            excel.setSex(contact.getSex());
            return excel;
        }).toList();

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("通讯录", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 导出
        EasyExcel.write(response.getOutputStream(), ContactsExcel.class)
                .sheet("通讯录")
                .doWrite(excelList);
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("通讯录导入模板", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 创建示例数据
        List<ContactsExcel> list = new ArrayList<>();
        ContactsExcel example = new ContactsExcel();
        example.setName("张三");
        example.setStudentId("2021001");
        example.setPhone("13800138000");
        example.setAddress("北京市朝阳区");
        example.setEmail("zhangsan@example.com");
        example.setSex("男");
        list.add(example);

        // 导出模板
        EasyExcel.write(response.getOutputStream(), ContactsExcel.class)
                .sheet("通讯录")
                .doWrite(list);
    }

    /**
     * 导入Excel
     */
    @PostMapping("/import")
    public ApiResult importExcel(@RequestPart("file") MultipartFile file) throws IOException {
        List<ContactsExcel> list = EasyExcel.read(file.getInputStream())
                .head(ContactsExcel.class)
                .sheet()
                .doReadSync();

        // 批量插入数据
        for (ContactsExcel excel : list) {
            jdbcClient.sql("INSERT INTO contacts (name, student_id, phone, address, email, sex) " +
                            "VALUES (:name, :studentId, :phone, :address, :email, :sex)")
                    .param("name", excel.getName())
                    .param("studentId", excel.getStudentId())
                    .param("phone", excel.getPhone())
                    .param("address", excel.getAddress())
                    .param("email", excel.getEmail())
                    .param("sex", excel.getSex())
                    .update();
        }

        return ApiResult.ok();
    }
}
