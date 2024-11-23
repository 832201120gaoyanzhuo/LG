package com.example.demo.controller;

import com.example.demo.domain.ApiResult;
import com.example.demo.domain.entity.Contacts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.*;

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

        StringBuilder sql = new StringBuilder("SELECT id, name, student_id AS studentId, phone, address, email, sex, top FROM contacts WHERE 1=1");
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
        StringBuilder sql = new StringBuilder("SELECT id, name, student_id AS studentId, phone, address, email, sex, top FROM contacts WHERE 1=1");

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
        Contacts contact = jdbcClient.sql("SELECT id, name, student_id AS studentId, phone, address, email, sex, top FROM contacts WHERE id = :id")
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
}
