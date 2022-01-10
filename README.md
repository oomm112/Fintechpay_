# FintechPay (제작 기간 / 11/28 ~ )
만약 내가 핀테크 페이를 설계한다면 이라는 생각으로 클론코딩을 해봤습니다.


# 목표
RestAPI방식을 사용하며, JPA와 JWT를 사용하여 보안까지 갖추어 최대한, 
생각한대로 구현해보려고 합니다.
실무에서의 경험이 없어 만약. 내가 한다면 이라는 생각으로 만들어 보았습니다.


# UseSkills
1.SpringBoot ( Gradle ver.11) 이용
2.MariaDB
3.JPA
4.JWT
5.스프링 시큐리티
6.Resttemplate 방식 오픈 api의 기능 구현
7.AWS(EC2)를 사용하여 WAR파일로 배포


# UseIDE  
Eclipse

# Swagger URI
http://52.78.62.73:8080/swagger-ui.html#/

# UseDependencies
  1.Lombok
  2.Gson
  3.MariaDB
  4.spring security
  5.jwt
  6.jackson
  7.Swagger
  
  
# 패키지 구조
  1.Controller
  
  2.Service
    Service-Impl
  
  3.Data
    3-1.DTO

    3-2.DAO
      DAO-Impl

    3-3.Repository <JPA 이용>

    3-4.Entity
  
  4.Config <Swagger2라이브러리 configuration>
   4-1 config-jwt

# 기능 목록 (jwt와 스프링시큐리티 이용)
  1. 회원가입
  3. 로그인
  4. 내정보확인 / 관리자용 조회
  5. 회원탈퇴
  6. 내 정보 업데이트 / 관리자용 업데이트
  
# 오픈뱅킹 API기능
  1.사용자 인증 코드 등록
  
  2.토큰발급
  
  3.사용자조회(핀테크번호사용)
  
  4.잔액조회
  
  5.거래내역조회
  
  6.등록계좌조회
  
  7.사용자탈퇴
  
  8.등록계좌해지
  
# 관리자용
  
  1.계좌실명조회
  
  2.계좌정보변경
