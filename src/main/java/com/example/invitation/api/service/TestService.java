package com.example.invitation.api.service;

import com.example.invitation.api.dao.TestDao;
import com.example.invitation.api.vo.BatchMetaData;
import com.example.invitation.api.vo.ColumnMetadata;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.sap.db.jdbc.exceptions.JDBCDriverException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Service
public class TestService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDao testDao;

    final String  SCHEMA = "WITHUS";
    //    public void test
    public void executeQueryAndInsert(String directoryPath ) throws Exception {


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

                    String tableName = subDir.getName() ;


                    // 각 폴더 내 파일 목록 가져오기
                    File[] files = subDir.listFiles();
                    if (files != null) {

                        try{

                            for (File file : files) {
                                if (file.getName().equalsIgnoreCase("create.sql")) {
                                    // ddl 실행
                                    String ddlSql = this.readFileToString( file );


                                    this.executeDDL(ddlSql); // 오류시 보통 테이블 중복에러  UncategorizedSQLException


                                } else if (file.getName().equalsIgnoreCase("data.csv")) {
                                    // insert 실행
                                    this.insertFromCsv( file.getPath()  , SCHEMA , tableName   );

                                }
                            }
                            // 완료시 상위 폴더 END (없을시 생성) 폴더안으로 이동
                            this.moveToEndFolder(subDir , "END" , tableName , null );

                        }catch(UncategorizedSQLException usqe ) {
                            // 테이블 중복 에러
                            if( usqe.getSQLException().getErrorCode() == 288 ){

                                final String dataInfoFileName  = "data.info";


                                // insert 해야할 total Count 와 db 에 있는 row count 비교 후 다르면 삭제 후 재 insert
                                int cnt = testDao.getRowCountBySchemaTblName(SCHEMA+"."+tableName);
                                File data_info = Arrays.stream(files).filter( _file -> _file.getName().equalsIgnoreCase(dataInfoFileName)).findFirst().orElse(null);

                                if (data_info != null) {
                                    int dataInfoRecordCount = getRecordCounter(data_info) ;

                                    // 다를 시 테이블 초기화
                                    if(cnt != dataInfoRecordCount ) {
                                        testDao.truncateTableBySchemaTblName( SCHEMA+"."+tableName  );

                                        // insert 실행
                                        final String csvInfo = "data.csv";
                                        File csvData = Arrays.stream(files).filter( _file -> _file.getName().equalsIgnoreCase(csvInfo)).findFirst().orElse(null);
                                        this.insertFromCsv( csvData.getPath()  , SCHEMA , tableName);

                                        // 완료시 상위 폴더 END (없을시 생성) 폴더안으로 이동
                                        this.moveToEndFolder(subDir , "END" , tableName , null );

                                    }else {
                                        System.out.println("성공적으로 처리된 테이블입니다. ") ;
                                        // 완료시 상위 폴더 END (없을시 생성) 폴더안으로 이동
                                        this.moveToEndFolder(subDir , "END" , tableName , null );

                                    }
                                } else {
                                    System.err.println("data.info 파일을 찾을 수 없습니다.");
                                    throw usqe;
                                }
                            }else {
                                throw usqe;
                            }



                        }catch(Exception e ) {
                            e.printStackTrace();
                            // 에러발생시  상위 폴더 ERROR 로 옮긴다 .
                            this.moveToEndFolder(subDir , "ERROR" ,tableName , e );
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

    private int getRecordCounter(File file ) throws Exception {

        final String recordCountKey  = "RECORD_COUNT";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 파일에서 읽은 각 줄을 분석
                if (line.startsWith(recordCountKey)) {
                    // RECORD_COUNT=3358에서 '=' 기준으로 자른 후 값만 추출
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String recordCountValue = parts[1].trim();
                        // recordCountValue에 원하는 RECORD_COUNT 값이 저장됩니다.
                        int recordCount = Integer.parseInt(recordCountValue);
                        System.out.println("RECORD_COUNT: " + recordCount);
                        return recordCount;
//                        break; // 원하는 값이 발견되면 종료
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        throw new Exception(file.getPath() +"data.info 파일 에 RECORD_COUNT 가 없음 " )  ;
    }




    private void moveToEndFolder(File subDir , String folderName , String tblName , Exception exception ) {
        // END 폴더의 경로 설정
        File endFolder = new File(  ((File)subDir.getParentFile()).getParent() , folderName);

        // END 폴더가 존재하지 않으면 생성
        if (!endFolder.exists()) {
            try {
                Files.createDirectory(endFolder.toPath());
                System.out.println("END 폴더를 생성했습니다: " + endFolder.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("END 폴더 생성 중 오류 발생: " + e.getMessage());
                return;
            }
        }

        // subDir의 경로와 이름을 사용하여 이동할 경로 설정
        Path sourcePath = subDir.toPath(); // 원본 폴더의 경로
        Path destinationPath = Paths.get(endFolder.getAbsolutePath(), subDir.getName()); // 이동할 폴더의 경로

        // 폴더 이동 시도
        try {
            Files.move(sourcePath, destinationPath);
            System.out.println("폴더를 END 폴더로 이동했습니다: " + subDir.getAbsolutePath() + " -> " + destinationPath);
        } catch (IOException e) {
            System.err.println("폴더 이동 중 오류 발생: " + e.getMessage());
        }


        // 로그성 데이터
        BatchMetaData batchMetaData = new BatchMetaData();
        batchMetaData.setTable_name(tblName);
        batchMetaData.setResult_txt(folderName);
        if(exception != null )   batchMetaData.setFail_reason(exception.getMessage());

        testDao.insertBatchLog( batchMetaData );
    }



    public static String readFileToString(File file ) {
        StringBuilder sb = new StringBuilder();

        // 파일 존재 여부 확인
        if (file.exists() && file.isFile()) {
            // BufferedReader를 사용하여 파일 읽기
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n"); // 각 줄을 StringBuilder에 추가
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("해당 파일이 존재하지 않거나 파일이 아닙니다: " + file);
        }

        return sb.toString();
    }


    private void executeDDL(String ddlStatment) throws UncategorizedSQLException , Exception  {
        // 예시: CREATE TABLE 구문을 실행하는 DDL
        try {
            String [] ddlStatments = ddlStatment.split(";");

            for (String ddl : ddlStatments) {
                jdbcTemplate.execute(ddl);
                System.out.println("DDL executed successfully.");
            }

        }
        // SAP DB EXCEPTION CODE
        // https://help.sap.com/docs/HANA_SERVICE_CF/7c78579ce9b14a669c1f3295b0d8ca16/20a78d3275191014b41bae7c4a46d835.html

        catch(UncategorizedSQLException usqe ) {
            usqe.printStackTrace();
            throw usqe;
//            usqe.getSQLException().getMessage();
//            usqe.getSQLException().getErrorCode(); // 288 이면 테이블 중복 에러
        }catch (Exception e) {
            System.err.println("Error executing DDL: " + e.getMessage());
            throw e;
        }
    }


//    public void insertFromCsvPatch(String csvFilePath, String tableName) {
//        // Construct the LOAD DATA SQL statement
//        String sql = "LOAD DATA INFILE '" + csvFilePath.replace("\\", "/") + "' " +
//                "INTO TABLE " + tableName + " " +
//                "FIELDS TERMINATED BY ',' " +
//                "ENCLOSED BY '\"' " +
//                "LINES TERMINATED BY '\\n' " +
//                "IGNORE 0 LINES";
//
//        // Execute the SQL statement
//        try {
//            jdbcTemplate.execute(sql);
//            System.out.println("CSV data loaded into table: " + tableName);
//        } catch (Exception e) {
//            System.err.println("Error loading CSV data into table: " + e.getMessage());
//        }
//    }

    public void insertFromCsv(String csvFilePath, final String SCHEMA , final String TABLE  ) throws Exception {


        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {

            List<String[]> lines = reader.readAll();

            if (lines.isEmpty()) {
                System.err.println("파일 내용 비었음 Empty CSV file: " + csvFilePath);
                return;
            }

            // Assuming the first line contains headers
            String[] headers = lines.get(0);
            int numberOfColumns = headers.length;

            // Prepare SQL insert statement
            String sql = generateInsertStatement(SCHEMA + "."+ TABLE, numberOfColumns);
            List<ColumnMetadata> getColumnNameDataType = testDao.getColumnNameDataType(SCHEMA , TABLE) ;

            if(getColumnNameDataType.isEmpty()) {
//                System.out.print("");
                throw new Exception("테이블 없습니다. 조회한 "+ SCHEMA +  TABLE  + "에 메타데이터 없음 ") ;
            }


            // Process each data line (skip the header line)
//            int batchSize = 1000; // Adjust batch size as needed
            int batchSize = 1000; // Adjust batch size as needed
            List<Object[]> batchArgs = new ArrayList<>();

            for (int i = 0; i < lines.size(); i++) { // Start from index 0 to skip header line
                String[] data = lines.get(i);

                if (data.length == numberOfColumns) {
                    Object[] transeData = new Object[data.length];
                    for (int j = 0; j < data.length; j++) {

                        transeData[j] = convertToTimestamp(data[j] ,  getColumnNameDataType.get(j) );
                    }

                    batchArgs.add(transeData);

                    // Execute batch update when batch size is reached
                    if (batchArgs.size() >= batchSize) {
                        jdbcTemplate.batchUpdate(sql, batchArgs);
                        batchArgs.clear();
                    }
                } else {
                    System.err.println("Skipping line due to column mismatch: " + data   );
                }
            }

            // Insert remaining rows
            if (!batchArgs.isEmpty()) {
                jdbcTemplate.batchUpdate(sql, batchArgs);
            }

            System.out.println("Data inserted successfully from CSV to table: " + TABLE );




        } catch(Exception e  ) {
            throw e;
        }

    }
    //테이블에 넣어야하는 row  insert 된 row update
    public void updateBatchLogExecute(String directoryPath ) throws Exception {


        // 해당 경로의 File 객체 생성
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            // 디렉토리 내 파일 목록 가져오기
            File[] subDirectories = directory.listFiles(File::isDirectory);
            if (subDirectories != null) {
                for (File subDir : subDirectories) {

                    String tableName = subDir.getName() ;

                    // 각 폴더 내 파일 목록 가져오기
                    File[] files = subDir.listFiles();
                    if (files != null) {
                        final String dataInfoFileName  = "data.info";


                        // insert 해야할 total Count 와 db 에 있는 row count 비교 후 다르면 삭제 후 재 insert
                        int tblRowCnt = testDao.getRowCountBySchemaTblName(SCHEMA+"."+tableName);

                        File data_info = Arrays.stream(files).filter( _file -> _file.getName().equalsIgnoreCase(dataInfoFileName)).findFirst().orElse(null);


                        BatchMetaData batchMetaData = new BatchMetaData();
                        batchMetaData.setTable_name(tableName);
                        batchMetaData.setTbl_row_cnt(tblRowCnt);

                        if (data_info != null) {
                            int dataInfoRecordCount = getRecordCounter(data_info) ;
                            batchMetaData.setCsv_row_cnt(dataInfoRecordCount);
                        }

                        testDao.updateBatchLog(batchMetaData);


                    }



                }
            }

        }

    }

        private String generateInsertStatement(String tableName, int numberOfColumns) {
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(tableName).append(" VALUES (");
        for (int i = 0; i < numberOfColumns; i++) {
            if (i > 0) {
                sqlBuilder.append(",");
            }
            sqlBuilder.append("?");
        }
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }


    private Object convertToTimestamp(String paramStr , ColumnMetadata columnMetadata ) {
        if(paramStr== null || paramStr.equals("")) {
            switch ( columnMetadata.getData_type_name() )  {
                case "NUMBER": // 예시로 NUMBER 타입일 때
                case "INT": // 예시로 INT 타입일 때
                case "DECIMAL": // 예시로 DECIMAL 타입일 때
                    return null; // 숫자 타입이면 null 반환
                default:
                    return ""; // 그 외의 경우는 빈 문자열("") 반환
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date parsedDate;
        try {
            String tmpParamStr = paramStr.replaceAll("\"", "") ;
            parsedDate = dateFormat.parse(tmpParamStr);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            return timestamp; // Convert to string representation for SQL insertion
        } catch (ParseException e) {
            return paramStr; // Handle conversion failure appropriately
        }
    }
}
