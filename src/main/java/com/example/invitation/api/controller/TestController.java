package com.example.invitation.api.controller;

import com.example.invitation.api.service.TestService;
import com.example.invitation.api.vo.ESFRSLN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.example.invitation.api.dao.TestDao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
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
    public void queryAndInsert() throws Exception {

//        String directoryPath = "C:\\Users\\wpdud\\OneDrive\\문서\\카카오톡 받은 파일\\db_sync\\index\\WITHUS";
        String directoryPath = "C:\\Users\\wpdud\\OneDrive\\문서\\카카오톡 받은 파일\\db_sync2\\index\\WITHUS";

        // 해당 경로의 File 객체 생성
        File directory = new File(directoryPath);

            // 디렉토리가 존재하는지 확인
        if (directory.exists() && directory.isDirectory()) {

            // 디렉토리 내 파일 목록 가져오기
            File[] subDirectories = directory.listFiles(File::isDirectory);
            if (subDirectories != null) {
                for (File subDir : subDirectories) {

                    if( subDir.getName() .equals("END") ||  subDir.getName() .equals("ERROR")   ) continue;
                    System.out.println("폴더 = 테이블 명 : " + subDir.getName());

                    testService.executeQueryAndInsert(subDir.getPath());
                }

            }

        } else {
            System.out.println("해당 경로가 존재하지 않거나 디렉토리가 아닙니다.");
        }

//            testService.executeQueryAndInsert(directoryPath);

    }
    @GetMapping("updateBatchLog/")
    public void updateBatchLog() throws Exception {


//        String directoryPath = "C:\\Users\\wpdud\\OneDrive\\문서\\카카오톡 받은 파일\\db_sync2\\index\\WITHUS\\END";
        String directoryPath = "C:\\Users\\wpdud\\OneDrive\\문서\\카카오톡 받은 파일\\db_sync2\\index\\WITHUS\\END";

        // 해당 경로의 File 객체 생성
        File directory = new File(directoryPath);

        // 디렉토리가 존재하는지 확인
        if (directory.exists() && directory.isDirectory()) {

            // 디렉토리 내 파일 목록 가져오기
            File[] subDirectories = directory.listFiles(File::isDirectory);
            if (subDirectories != null) {
                for (File subDir : subDirectories) {


                }
            }
        } else {
            System.out.println("해당 경로가 존재하지 않거나 디렉토리가 아닙니다.");
        }

    }



//    public static

}
