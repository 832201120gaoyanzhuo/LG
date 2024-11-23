package com.example.demo.controller;

import cn.hutool.core.util.ObjectUtil;
import com.example.demo.domain.entity.Contacts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class RouterController {

    @Autowired
    private JdbcClient jdbcClient;

    @GetMapping({"/", "/index"})
    public ModelAndView index() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/index");
        return view;
    }

    @GetMapping("/contacts/contactsList")
    public ModelAndView contactsList() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/contacts/index");
        return view;
    }

    @GetMapping("/contacts/dialog")
    public ModelAndView contactsDialog(Integer id) {
        ModelAndView view = new ModelAndView();
        if (ObjectUtil.isNotNull(id)) {
            Contacts contacts = jdbcClient.sql("SELECT * FROM contacts where id = ?").param(id).query(Contacts.class).single();
            view.addObject("contacts", contacts);
        }
        view.setViewName("/contacts/dialog");
        return view;
    }


}
