package com.example.invitation.api.controller;

import com.example.invitation.api.service.TestService;
import com.example.invitation.api.vo.ESFRSLN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.example.invitation.api.dao.TestDao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    TestDao testDao ;
    @Autowired
    TestService testService;

    @GetMapping("/selectESFRSLN")
    List<ESFRSLN> selectESFRSLN() {

        List<ESFRSLN> list = testDao.selectESFRSLN();

        return list;
    } ;


    @GetMapping("/mappingFile")
    public void queryAndInsert () {

        String directoryPath = "C:\\Users\\wpdud\\OneDrive\\문서\\카카오톡 받은 파일\\새 폴더\\index\\WITHUS\\ES";
        testService.executeQueryAndInsert(directoryPath);

    }




//    public static

}
