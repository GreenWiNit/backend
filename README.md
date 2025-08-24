## 🌿 GreenWiNit
GreenWiNit은 웹 기반의 환경 챌린지형 커뮤니티 플랫폼입니다.      
일상 속에서 시민들이 쉽고 자발적으로 환경 보호 활동에 참여하고, 이를 공유하며 서로에게 동기를 부여할 수 있도록 설계된 서비스입니다.    
🔗 https://www.greenwinit.store/

<br>

### 🌍 프로젝트 개요
- 프로젝트명: GreenWiNit
- 팀명: 노크
- 기획 목적:    
    - 누구나 쉽게 환경 보호 활동에 참여할 수 있는 온라인 플랫폼 제공
    - 챌린지 참여와 포인트 리워드 시스템을 통한 지속가능한 참여 유도
    - 오프라인 환경 캠페인과 연계하여 실질적인 사회적 실천 확산
    - 참여 사업명: 2025년 서울 청년 기획봉사단
    - 사업 기간: 2025.04.17 ~ 2025.08.31


<br>

### 📜 프로젝트 구조 및 Coding Convetion 
https://github.com/GreenWiNit/backend/wiki/Coding-Convention    
https://github.com/GreenWiNit/backend/wiki/How-to-Contribute 
```
src/main/java/...
├── domain/
│   ├── challenge/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── repository/
│   │   └── service/
│   ├── common/
│   │   ├── BaseEntity.java
│   │   └── ...
│   ├── product/
│   │   └── ...
│   └── ...
│
├── global/
│   ├── config/
│   │   ├── WebConfig.java
│   │   └── ...
│   ├── error/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ErrorResponse.java
│   │   └── ...
│   └── util/
│       ├── DateUtils.java
│       └── ...
│
└── infra/
    ├── storage/
    │   ├── S3Client.java
    │   ├── S3Config.java
    │   └── ...
    ├── mail/
    │   ├── EmailSender.java
    │   └── ...
    └── security/
        ├── JwtProvider.java
        ├── SecurityConfig.java
        └── ...
```


### ✨ Architecture 
<img width="945" height="295" alt="Image" src="https://github.com/user-attachments/assets/e266ce6f-e2c2-44de-8e92-5a85acc846a7" />



### 🛠 Tech Stack
<p dir="auto"><a target="_blank" rel="noopener noreferrer nofollow" href="https://camo.githubusercontent.com/4a2a76648ac74b1e794e66a65d6c39da2d76c090fdf16b1423a4c99fd9b7956a/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4672616d65776f726b2d3535353535353f7374796c653d666f722d7468652d6261646765"><img src="https://camo.githubusercontent.com/4a2a76648ac74b1e794e66a65d6c39da2d76c090fdf16b1423a4c99fd9b7956a/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4672616d65776f726b2d3535353535353f7374796c653d666f722d7468652d6261646765" data-canonical-src="https://img.shields.io/badge/Framework-555555?style=for-the-badge" style="max-width: 100%;"></a><a target="_blank"><img alt="SpringBoot" src="https://img.shields.io/badge/springboot-%236DB33F.svg?style=for-the-badge&amp;logo=springboot&amp;logoColor=white" style="max-width: 100%;"></a><a target="_blank"><img alt="spring_data_JPA" src="https://img.shields.io/badge/spring_data_JPA-%236DB33F?style=for-the-badge&amp;logo=databricks&amp;logoColor=white" style="max-width: 100%;"></a><a target="_blank"><img alt="SpringSecurity" src="https://img.shields.io/badge/spring_security-%236DB33F.svg?style=for-the-badge&amp;logo=springsecurity&amp;logoColor=white" style="max-width: 100%;"></a> <a target="_blank"><img src="https://img.shields.io/badge/build-555555?style=for-the-badge" style="max-width: 100%;"></a><a target="_blank"><img alt="Gradle" src="https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&amp;logo=Gradle&amp;logoColor=white" style="max-width: 100%;"></a></p>

<p dir="auto"><a target="_blank" ><img src="https://img.shields.io/badge/Database-555555?style=for-the-badge" style="max-width: 100%;"></a><a target="_blank"><img alt="MySQL" src="https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&amp;logo=mysql&amp;logoColor=white" style="max-width: 100%;"></a></p>

<p dir="auto"><a target="_blank"><img src="https://img.shields.io/badge/Infrastructure-555555?style=for-the-badge" style="max-width: 100%;"></a><a target="_blank" ><img alt="Amazon Ec2" src="https://img.shields.io/badge/amazon_ec2-FF9900.svg?style=for-the-badge&amp;logo=amazonec2&amp;logoColor=white" style="max-width: 100%;"></a><a target="_blank"><img alt="Amazon S3" src="https://img.shields.io/badge/AWS_S3-569A31.svg?style=for-the-badge&amp;logo=amazons3&amp;logoColor=white" style="max-width: 100%;"></a><a target="_blank"><img  alt="Amazon RDS" src="https://img.shields.io/badge/amazon_RDS-527FFF.svg?style=for-the-badge&amp;logo=amazonrds&amp;logoColor=white" style="max-width: 100%;"></a><a target="_blank"></p>


<p dir="auto"><a target="_blank"><img src="https://img.shields.io/badge/CICD-555555?style=for-the-badge" style="max-width: 100%;"><img alt="GitHub Actions" src="https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&amp;logo=githubactions&amp;logoColor=white" style="max-width: 100%;"></a><a target="_blank"></p>

<br>


### ✨ 주요 기능
- 환경 챌린지 참여 (개인/팀 단위)
- 챌린지 인증 및 후기 등록
- 포인트 적립 및 리워드 교환 상점
- 환경 정보 공유 및 커뮤니티 기능
- 관리자 기능 (챌린지/회원/포인트 관리 등)
- SNS 및 오프라인 활동과 연계한 참여 유도

<br>

### 🏁 기능 요약
#### 기능	설명
- 챌린지:	플로깅, 분리배출, 텀블러 사용 등 다양한 환경 행동 참여
- 포인트:	챌린지 인증 시 포인트 적립, 굿즈 교환 가능
- 정보공유:	정보 공유 탭을 통해 환경 관련 콘텐츠를 자유롭게 등록 및 열람 가능
- 마이페이지:	챌린지 참여 내역, 포인트 내역, 리워드 신청 등 개인 활동 관리


<div align="center">
  <img
    src="https://github.com/user-attachments/assets/d1f48a33-de44-4f8f-9516-7bd8cd37c38c"
    alt="홈 화면"
    width="250"
  />
  <img
    src="https://github.com/user-attachments/assets/9c7fe4ab-1bcd-478d-8574-18ab2bb9c3e7"
    alt="챌린지 참여"
    width="250"
  />
  <img
    src="https://github.com/user-attachments/assets/e3175ecb-0238-4ff6-9163-26170ce4ede9"
    alt="인증 작성"
    width="250"
  />
</div>

<br />

<div align="center">
  <img
    src="https://github.com/user-attachments/assets/f0734e21-7b97-422c-a4c5-2ed3450e36f1"
    alt="포인트/리워드"
    width="250"
  />
  <img
    src="https://github.com/user-attachments/assets/ce6f5b72-21b1-450f-b076-ebb010181525"
    alt="마이페이지"
    width="250"
  />
</div>

<br />

<div align="center">
  <img
    src="https://github.com/user-attachments/assets/33563c9b-8d71-49fc-803f-cf694130164d"
    alt="커뮤니티(정보공유)"
    width="250"
  />
  <img
    src="https://github.com/user-attachments/assets/39d7f951-76f5-41ff-9484-73043b1ed769"
    alt="관리자 기능"
    width="250"
  />
</div>

<br /><br />



### 👩‍💻 Backend Contributors
| 김지환                                | 최윤정                           | 김지호                                 | 
| ------------------------------------- | ---------------------------------- | -------------------------------------- | 
| [Github](https://github.com/jihwankim128) | [Github](https://github.com/yunjeooong) | [Github](https://github.com/JEEEEEEHO) |


