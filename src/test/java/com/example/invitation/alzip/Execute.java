package com.example.invitation.alzip;

import org.junit.jupiter.api.Test;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
public class Execute {


    // directoryPath 안에 폴더 압축푼다.
    @Test
    public void unzip () {


        // 특정 디렉토리 경로
        String directoryPath = "C:\\Users\\wpdud\\OneDrive\\문서\\카카오톡 받은 파일\\새 폴더";

        try {
            // 디렉토리 아래의 모든 .tar.gz 파일을 찾기
            Files.list(Paths.get(directoryPath))
                    .filter(path -> path.toString().endsWith(".tar.gz"))
                    .forEach(path -> {
                        try {
                            unTarGz(path.toString(), directoryPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void executeQueryAndInsert(){
        // 경로 설정
        String directoryPath = "C:\\Users\\wpdud\\OneDrive\\문서\\카카오톡 받은 파일\\새 폴더\\index\\WITHUS\\ES";

        // 해당 경로의 File 객체 생성
        File directory = new File(directoryPath);

        // 디렉토리가 존재하는지 확인
        if (directory.exists() && directory.isDirectory()) {
            // 디렉토리 내 파일 목록 가져오기
            File[] subDirectories = directory.listFiles(File::isDirectory);
            if (subDirectories != null) {
                for (File subDir : subDirectories) {
                    System.out.println("폴더: " + subDir.getName());

                    // 각 폴더 내 파일 목록 가져오기
                    File[] files = subDir.listFiles();
                    if (files != null) {
                        boolean foundCreateSQL = false;
                        boolean foundDataCSV = false;

                        for (File file : files) {
                            if (file.getName().equalsIgnoreCase("create.sql")) {
                                foundCreateSQL = true;
                            } else if (file.getName().equalsIgnoreCase("data.csv")) {
                                foundDataCSV = true;
                            }
                        }

                        // 검색 결과 출력
                        if (foundCreateSQL) {
                            System.out.println("\t ddl 실행 ");
                            // ddl

                        } else {
                            System.out.println("\tcreate.sql 파일이 존재하지 않습니다.");
                        }

                        if (foundDataCSV) {
                            System.out.println("\t insert 쿼리실행 ");

                        } else {
                            System.out.println("\tdata.csv 파일이 존재하지 않습니다.");
                        }
                    } else {
                        System.out.println("\t해당 폴더가 비어 있습니다.");
                    }
                    System.out.println(); // 개행
                }
            } else {
                System.out.println("해당 경로에 폴더가 없습니다.");
            }
        } else {
            System.out.println("해당 경로가 존재하지 않거나 디렉토리가 아닙니다.");
        }


    }










    // .tar.gz 파일 압축 풀기 메서드
    public static void unTarGz(String tarGzPath, String outputDir) throws IOException {
        try (FileInputStream fis = new FileInputStream(tarGzPath);
             GzipCompressorInputStream gis = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tis = new TarArchiveInputStream(gis)) {

            TarArchiveEntry entry;
            while ((entry = tis.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File outputFile = new File(outputDir, entry.getName());
                outputFile.getParentFile().mkdirs();

                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = tis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        }
    }
}
