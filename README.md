# Company-Service

---

## 1. 프로젝트 설명

- Company Service
- 가상의 IT 기업의 업무 내용을 전산화하여 데이터를 저장하는 사이트입니다.
- 주요 흐름은 다음과 같습니다.

```less
[사용자]
   |
   ▼
[프론트엔드 (HTML, CSS, JS, Thymeleaf, Bootstrap)]
   |
   ▼
[백엔드 (Spring Boot, JPA, Security)]
   |
   ├── [계정 관리 기능]
   ├── [부서별 업무 기능]
   └── [부서별 게시판 기능]
   |
   ▼
[데이터베이스 (MySQL 또는 Oracle)]
```

---

## 2.주요 기능

1. 직원 계정 관리(생성, 직원 정보 수정, 삭제 등)
2. 부서별 업무 실행
   - 기본값 : 재무회계, 인사, 영업, IT(백엔드), 디자인(프론트엔드)
3. 부서별 게시판 사용

---

## 3. 기술 스택

### FrontEnd

1. HTML5
2. CSS
3. JavaScript
4. Thymeleaf
5. Bootstrap

### BackEnd

1. Java 17
2. Spring Boot 3.1.7
3. Spring Data JPA (ORM을 사용한 데이터베이스 연동)
4. Spring Security (사용자 인증 및 권한 관리)
5. MySQL (사용 중) / Oracle DB (선택적)
6. Gradle (빌드 및 의존성 관리)
7. Lombok (코드 간소화를 위한 라이브러리)

### 기타

1. Git (버전 관리 시스템)
2. JUnit 5 (테스팅 도구)

---

## 4. 개발 정보 (Development Note)

- 이 프로젝트는 학습용이자 개인 포트폴리오 용으로 제작되었습니다.
- 전체 기능은 본인이 직접 기획하고 구현하였으며, 외부 라이브러리는 필수적인 개발 도구와 프레임워크만 사용하였으며, 불필요한 의존성은 최대한 배제하였습니다.
- 모든 핵심 로직과 UI는 직접 설계하고 구현했습니다.

---

## 5. 참여자

- **이름**: 장성환
- **GitHub**: [https://github.com/java-backend-starter](https://github.com/java-backend-starter)
- **이메일**:
    - Naver: jsksh9865@naver.com
    - Gmail: jsksh9865@gmail.com