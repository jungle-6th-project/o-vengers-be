<div align="center">

# 뽀독뽀독(온라인 독서실) - Back End 

----

![image](https://github.com/depromeet12th/three-days-server/assets/78407939/287741a3-8e02-4eea-a946-f1765018ca7e)
  
정글 6기 오독오독 팀 - [채상엽](https://github.com/saint6839)(BE)


# 📚Tech Stack📚

|                                                                                                                                                                                                  Communications                                                                                                                                                                                                   |                                                                                                                                                                                                           Server                                                                                                                                                                                                           |                                                                                                                                                                                                       Database                                                                                                                                                                                                        |                                                                                                                                                                                                                                                                                                                                             Infrastructure                                                                                                                                                                                                                                                                                                                                             |
|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| <img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white"/> <img src="https://img.shields.io/badge/Jira-0052CC?style=flat-square&logo=Jira&logoColor=white"/> <img src="https://img.shields.io/badge/Slack-4A154B?style=flat-square&logo=Slack&logoColor=white"/> <img src="https://img.shields.io/badge/Notion-000000?style=flat-square&logo=Notion&logoColor=white"/> | <img src="https://img.shields.io/badge/Java-FF9E0F?style=flat-square&logo=&logoColor=white"/> <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white"/> <img src="https://img.shields.io/badge/JUnit5-25A162?style=flat-square&logo=JUnit5&logoColor=white"/> <img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=white"/> | <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white"/> <img src="https://img.shields.io/badge/Adminer-34567C?style=flat-square&logo=adminer&logoColor=white"/> <img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white"/> <img src="https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white"/> | <img src="https://img.shields.io/badge/AWS EC2-FF9900?style=flat-square&logo=amazon ec2&logoColor=white"/> <img src="https://img.shields.io/badge/AWS RDS-527FFF?style=flat-square&logo=amazon rds&logoColor=white"/> <img src="https://img.shields.io/badge/Github Actions-2088FF?style=flat-square&logo=github actions&logoColor=white"/> <img src="https://img.shields.io/badge/AWS S3-569A31?style=flat-square&logo=Amazon S3&logoColor=white"/> <img src="https://img.shields.io/badge/AWS CodeDeploy-FF9900?style=flat-square&logo=Amazon CodeDeploy&logoColor=white"/> <img src="https://img.shields.io/badge/AWS CloudWatch-FF4F8B?style=flat-square&logo=Amazon CloudWatch&logoColor=white"/> |

# 📝Entity Relationship Diagram(ERD)📝

![image](https://github.com/jungle-6th-project/o-vengers-be/assets/78407939/ffeed27f-ec70-46cd-91ea-4e21f0f2c09b)
<div align="left">
JPA에서 제공하는 연관 관계 매핑 어노테이션을 사용하지 않고, 해당 테이블이 참조하고자 하는 테이블의 키를 직접 저장하도록 하였습니다.
이를 통해 3주라는 짧은 시간안에 개발해냈어야 하는 만큼, JPA로 인한 잠재적인 오류를 방지할 수 있었으며 N+1 문제를 방지할 수 있었습니다.
</div>

# 🏗️Infrastructure🏗️

![image](https://github.com/jungle-6th-project/o-vengers-be/assets/78407939/dcc45e28-d3b8-4750-996f-2fb21dd54527)
<div align="left">

- 실시간성이 주요한 화상 채팅 기능은 별도의 ec2 인스턴스로 분리하였습니다. 이에 따라 하나의 도메인으로 요청에 따라 서로 다른 서버로 라우팅 시키기 위해, ALB를 사용하였습니다.
- 서버 인스턴스와 데이터베이스 인스턴스를 별도의 서브넷으로 분리하여 격리 시켜, 데이터베이스 리소스에 대한 접근 안전성을 보장하였습니다.
- 오토 스케일링을 지원하는 RDS Proxy를 사용하여 DB 가용성을 높였습니다.
- CloudWatch를 사용하여 서버 로그를 간편하게 관리하였습니다.

</div>


<div align="center">

### 🛣️ CI/CD process 🛣️

![image](https://github.com/jungle-6th-project/o-vengers-be/assets/78407939/c6591ce0-705e-4943-a462-d2d5d54883e0)

</div>


# 👊백엔드 기술적 챌린지👊

### 🧊Cache Cold Start Issue🧊

![image](https://github.com/depromeet12th/three-days-server/assets/78407939/cfb18ed4-23ee-489c-acf9-ae2dd0fa778d)

### 👥비동기 로직 Thread Pool 최대 크기 초과 문제👥

<img width="467" alt="image" src="https://github.com/jungle-6th-project/o-vengers-be/assets/78407939/0020904e-b830-457c-9b26-b09595ff88da">

</div>
