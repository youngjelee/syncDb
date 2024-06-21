사용방법


1. test .java .com.example.invitation.alzip  패키지의 Execute 클래스의 unzip 메소드의 directoryPath  변수 바꿔주고 테스트 실행 


2. TestController 의 queryAndInsert 메소드 안에 directoryPath 변수에 위에서 생성된 폴더 풀 패스를 적는다.

3. InvitationApplication.class (메인 클래스 ) 실행


4. http://localhost:8081/selectESFRSLN 를 실행해서 db 접속확인 

5. localhost:8081/mappingFile 실행한다.


6. 실행 후
   home\index\WITHUS\ES
 를 기준으로
성공시
   home\index\WITHUS\ES\END 
실패시 
   home\index\WITHUS\ES\ERROR 
로 폴더가 이동한다. 

7. ERROR 폴더 확인후 수동작업 또는 소스를 수정후 db 테이블 삭제 후 다시 반복한다. 

