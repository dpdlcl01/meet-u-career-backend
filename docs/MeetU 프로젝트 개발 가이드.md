# 🛠 MeetU 프로젝트 개발 가이드

## 📚 목차

- [✅ 프로젝트 개요](#-프로젝트-개요)

- [✅ WIP 커밋 & 브랜치 전략](#-wip-커밋--브랜치-전략)
  - [🔹 하나의 브랜치 = 하나의 기능](#-하나의-브랜치--하나의-기능)
  - [🔹 WIP 커밋은 작업 중인 중간 저장 용도](#-wip-커밋은-작업-중인-중간-저장-용도)

- [✅ 브랜치 목록 및 담당자](#-브랜치-목록-및-담당자)

- [✅ 커밋 메시지 규칙](#-커밋-메시지-규칙)
  - [✅ WIP 커밋 (작업 중)](#-wip-커밋-작업-중)
  - [✅ 기능 완료 커밋](#-기능-완료-커밋)

- [✅ 개발 환경 설정 안내](#-개발-환경-설정-안내)
  - [📁 필수 설정 파일](#-필수-설정-파일)

- [✅ 협업 시 Git 흐름 요약](#-협업-시-git-흐름-요약)

- [✅ Postman 사용 방식](#-postman-사용-방식)

- [✅ 개발 일정 목표 (4월 13일까지)](#-개발-일정-목표-4월-13일까지)
  - [⏰ 1차 목표: **개인회원(Personal) 기능 완성**](#-1차-목표-개인회원personal-기능-완성)
  - [⏰ 2차 목표: **기업회원 기능 추가 구현**](#-2차-목표-기업회원-기능-추가-구현)

- [✅ 백엔드 기능별 패키지 구조 (`meet-u-backend`)](#-백엔드-기능별-패키지-구조-meet-u-backend)

---

## ✅ 프로젝트 개요

- **프로젝트명**: MeetU (커리어 매칭 플랫폼)
- **백엔드 기술스택**: Spring Boot, JPA, MySQL, QueryDSL
- **프론트엔드 기술스택**: React, Zustand
- **배포**: AWS EC2 + Docker + GitHub Actions
- **서버/DB**: Oracle Cloud(MySQL), 백/프 각각 EC2 분리 배포
- **인증 방식**: accessToken 기반
- **레포지토리**:  
  - `meet-u-backend`  
  - `meet-u-frontend`


---

## ✅ WIP 커밋 & 브랜치 전략

### 🔹 하나의 브랜치 = 하나의 기능
- 기능 완료되면 PR 생성 및 병합
- 브랜치명 규칙: `CAR-XX-도메인-기능명`

### 🔹 WIP 커밋은 작업 중 저장 용도
- 하루 작업 마무리 시 커밋 필수
- 커밋 메시지: `WIP: CAR-XX 작업 중 (YYYY-MM-DD)`


---

## ✅ 브랜치 목록 및 담당자

| 브랜치명                               | 기능                | 담당자 |
| ---------------------------------- | ----------------- | --- |
| CAR-24-personal-auth-recovery      | 아이디/비밀번호 찾기       | 문성후 |
| CAR-57-personal-mypage-application | 개인 마이페이지 - 지원 내역  | 문성후 |
| CAR-55-personal-companydetail      | 기업 상세             | 문성후 |
|                                    |                   |     |
| CAR-52-personal-joblist            | 공고 목록             | 서정안 |
| CAR-56-personal-mypage-scrap       | 공고 북마크/관심기업 기능    | 서정안 |
| CAR-72-personal-cs                 | 개인 - 고객센터         | 서정안 |
| CAR-73-**admin**-api-job           | 관리자 공고 관리 API     | 서정안 |
|                                    |                   |     |
| CAR-68-**portal**                  | 메인 페이지 (포털)       | 양화영 |
| CAR-22-personal-login              | 로그인 기능            | 양화영 |
| CAR-23-personal-signup             | 회원가입 기능           | 양화영 |
| CAR-33-personal-mypage-resumeblock | 이력서 블라인드 처리       | 양화영 |
| CAR-53-personal-jobdetail          | 공고 상세             | 양화영 |
| CAR-70-personal-notification       | 알림 기능             | 양화영 |
|                                    |                   |     |
| CAR-60-personal-mypage-dashboard   | 마이페이지 대시보드        | 이경준 |
| CAR-59-personal-mypage-offer       | 개인 마이페이지 - 받은 제안  | 이경준 |
| CAR-54-personal-reviewlist         | 기업 면접 후기          | 이경준 |
| CAR-69-personal-chat               | 채팅 기능             | 이경준 |
|                                    |                   |     |
| CAR-58-personal-mypage-interview   | 면접 후기 작성          | 조연화 |
| CAR-61-personal-community          | 커뮤니티 게시판          | 조연화 |
| CAR-71-personal-privacy            | 개인정보 관리           | 조연화 |
|                                    |                   |     |
| CAR-31-personal-resume             | 이력서 작성/관리         | 홍동화 |
| CAR-32-personal-coverletter        | 자기소개서             | 홍동화 |
| CAR-62-personal-calendar           | 일정 관리             | 홍동화 |
|                                    |                   |     |
|                                    |                   |     |
| business-search                    | 인재 검색 (구직자 찾기)    | 문성후 |
| business-dashboard                 | 기업회원 대시보드         | 서정안 |
| business-applicant                 | 기업 공고별 지원자 관리     | 양화영 |
| business-offer                     | 보낸 제안             | 이경준 |
| business-login                     | 기업회원 로그인          | 조연화 |
| business-signup                    | 기업회원 회원가입         | 조연화 |
| business-job                       | 채용공고 관리           | 홍동화 |
| business-jobpayment                | 채용공고 결제           | 홍동화 |
|                                    |                   |     |
|                                    |                   |     |
| admin-log                          | 시스템 로그 관리         | 문성후 |
| admin-userinfo                     | 사용자 정보 관리 (개인/기업) | 서정안 |
| admin-cs                           | 고객 문의 처리          | 서정안 |
| admin-dashboard                    | 관리자 대시보드          | 양화영 |
| admin-community                    | 커뮤니티 관리           | 양화영 |
| admin-companycheck                 | 기업 조회/검수          | 이경준 |
| admin-account                      | 관리자 계정 관리         | 이경준 |
| admin-jobmanage                    | 공고 관리 (기업별 조회/삭제) | 조연화 |
| admin-login                        | 관리자 로그인           | 홍동화 |
| admin-payment                      | 결제 내역 관리          | 홍동화 |

> 🚨 _각 브랜치 이름은 기능별로 체계적으로 정리되어 있으므로, `CAR-XX-도메인-기능명` 형식을 꼭 지켜주세요._


---

## ✅ 커밋 메시지 규칙

### ✅ WIP 커밋 (작업 중) - WIP: {이슈키} {작업 내용} ({날짜})
형식:  
```
WIP: {이슈키} {작업 내용} ({날짜})
```
예시:  
```
WIP: CAR-71 채용 공고 필터링 작업 중 (2025-03-27)
```

  
### ✅ 기능 완료 커밋 - {작업 종류}: {이슈키} {작업 내용}

| 타입       | 설명        | 예시                         |
| -------- | --------- | -------------------------- |
| feat     | 기능 추가     | `feat: CAR-12 회원가입 API 추가` |
| fix      | 버그 수정     | `fix: CAR-18 포인트 반환 오류 해결` |
| refactor | 리팩토링      | `refactor: 중복 유틸 제거`       |
| docs     | 문서 수정     | `docs: README 업데이트`        |
| style    | 스타일 변경    | `style: 세미콜론 정리`           |
| test     | 테스트 추가    | `test: 회원가입 유닛 테스트`        |
| chore    | 설정, 초기 구성 | `chore: 프로젝트 초기 설정`        |
##### ✅ `feat` (기능 추가)
- **새로운 기능**이 생겼을 때 사용.
- 새로운 API, UI 요소, 로직 등.
##### 🐞 `fix` (버그 수정)
- 기존 기능의 **오작동이나 오류를 고쳤을 때** 사용.
- 사용자나 테스트 중에 **문제가 발생한 부분**을 해결한 것.
##### 🔧 `refactor` (리팩토링)
- 기능은 그대로인데, **코드를 더 깔끔하고 유지보수 가능하게** 고쳤을 때 사용.
- **동작은 안 바뀌지만 구조 개선**한 경우.


---

## ✅ 개발 환경 설정 안내

### 📁 필수 설정 파일

1. `application-secret.yml`  
   - 위치: `src/main/resources/application-secret.yml`  
   - DB 접속 정보, API Key 포함 → **Git에 포함되지 않음**

1. `.env`  
   - 위치: 프로젝트 루트 (`meet-u-backend`)  
   - 환경 변수 저장용 → **수정하지 말고 공유된 그대로 사용**

> 🔐 이 두 파일은 보안상 Git에 커밋하지 않으며, 팀장 또는 공유 채널에서 전달된 파일을 로컬에 직접 복사해 넣으세요.


---

## ✅ 협업 시 Git 흐름 요약

```bash
# 1. 원격 저장소 클론
git clone https://github.com/dpdlcl01/meet-u-career-backend.git

# 2. 본인 기능 브랜치 이동
git fetch origin
git checkout CAR-XX-도메인-기능

# 3. 작업 후 커밋
git add .
git commit -m "feat: CAR-XX 기능 구현"

# 4. 원격 브랜치에 푸시
git push origin CAR-XX-도메인-기능

# 5. PR 생성 (main 또는 develop 브랜치로)

```


---

## ✅ Postman 사용 방식

### 📌 목적
- 백엔드 작업 완료 시 **Postman Collection에 요청 예시 저장**
- 팀원 간 **API 테스트 공유** 및 프론트엔드 연동 편의성 확보

---
  
### ✅ 사용 규칙

|항목|내용|
|---|---|
|Collection 명|기능별로 정리 (예: `InterviewReview`, `Resume`, `JobPosting`)|
|Request 명|API 경로(`/api/~~~`)를 그대로 사용하여 일관성 유지|
|예시 응답|실제 응답 값을 함께 저장|
|Headers|`Content-Type: application/json` / `Authorization: Bearer {accessToken}` 포함|
|Body|JSON 형식으로 요청 내용 구성|

---

### ✅ 예시: 면접 후기 작성

| 항목                | 내용                                                     |
| ----------------- | ------------------------------------------------------ |
| 요청명               | `POST /api/personal/interview-reviews`                 |
| 요청 방식             | `POST`                                                 |
| 요청 URL            | `http://localhost:8080/api/personal/interview-reviews` |
| Body - raw (JSON) |                                                        |

| Body - raw (JSON) |
```json

{
  "companyId": 3,
  "jobCategoryId": 5,
  "careerLevel": 1,
  "interviewYearMonth": "2024-12",
  "rating": 2,
  "difficulty": 4,
  "interviewType": 9,
  "interviewParticipants": 1,
  "hasFrequentQuestions": true,
  "questionsAsked": "자기소개를 해보세요\n지원 동기를 말해주세요",
  "interviewTip": "분위기 편해서 편하게 얘기하면 됩니다.",
  "result": 1,
  "applicationId": 12
}

```

| 응답 예시 |
```json

{
  "count": 1,
  "msg": "success",
  "data": 23
}

```
>해당 요청은 `InterviewReviewPersonalController`의 `/api/personal/interview-reviews` 경로와 매핑됨


---

## ✅ 백엔드 기능별 패키지 구조 (`meet-u-backend`)

```
Application Domain
지원서 작성, 제출, 관리
application/
├── business/
│   ├── controller/
│   ├── dto/
│   └── service/
├── common/
│   ├── entity/
│   │   ├── Application
│   │   └── InterviewReview
│   └── repository/
└── personal/
    ├── controller/
    ├── dto/
    └── service/
  
Auth Domain
인증, 권한 부여 및 관리
auth/
├── admin/
├── business/
├── common/
└── personal/

Calendar Domain
일정 관리 및 스케줄링
calendar/
├── admin/
├── business/
├── common/
│   ├── entity/
│   │   ├── Calendar
│   │   ├── CalendarEvent
│   │   └── CalendarSharing
│   └── repository/
└── personal/

Chat Domain
실시간 채팅 및 메시징
chat/
├── business/
├── common/
│   ├── entity/
│   │   ├── ChatMessage
│   │   └── ChatRoom
│   └── repository/
└── personal/

Community Domain
사용자 간 커뮤니티 활동 및 콘텐츠 공유
community/
├── admin/
├── common/
│   ├── entity/
│   │   ├── CommunityComment
│   │   ├── CommunityLike
│   │   ├── CommunityPost
│   │   └── CommunityTag
│   └── repository/
└── personal/

Company Domain
기업 정보 관리 및 기업 프로필
company/
├── admin/
├── business/
├── common/
│   ├── entity/
│   │   ├── Company
│   │   └── CompanyFollow
│   └── repository/
└── personal/

CoverLetter Domain
자기소개서 작성 및 관리
coverletter/
├── business/
├── common/
│   ├── entity/
│   │   ├── CoverLetter
│   │   └── CoverLetterContent
│   └── repository/
└── personal/

Customer Support (CS) Domain
고객 지원 및 문의 응대
cs/
├── admin/
├── business/
├── common/
│   ├── entity/
│   │   └── CustomerSupport
│   └── repository/
└── personal/

Dashboard Domain
사용자 유형별 대시보드(마이페이지) 및 요약 정보
dashboard/
├── admin/
├── business/
└── personal/

Job Domain
채용 공고 및 구직 활동 관련
job/
├── admin/
├── business/
├── common/
│   ├── entity/
│   │   ├── Bookmark
│   │   ├── JobCategory
│   │   ├── JobPosting
│   │   ├── JobPostingJobCategory
│   │   ├── JobPostingViewLog
│   │   └── Location
│   └── repository/
└── personal/

Notification Domain
알림 및 푸시 메시지
notification/
├── business/
├── common/
│   ├── entity/
│   │   └── Notification
│   └── repository/
└── personal/

Offer Domain
기업의 채용 제안 및 관리
offer/
├── admin/
├── business/
├── common/
│   ├── entity/
│   │   └── Offer
│   └── repository/
└── personal/

Payment Domain
결제 및 과금
payment/
├── admin/
├── business/
├── common/
│   ├── entity/
│   │   ├── Advertisement
│   │   └── Payment
│   └── repository/
└── personal/

Portal Domain
서비스 메인 페이지 및 통합
portal/
├── controller/
├── dto/
└── service/

Resume Domain
이력서 작성 및 관리
resume/
├── admin/
├── business/
├── common/
│   ├── entity/
│   │   ├── Resume
│   │   ├── ResumeContent
│   │   └── ResumeViewLog
│   └── repository/
└── personal/

System Domain
시스템 관리 및 운영
system/
├── admin/
├── common/
│   ├── entity/
│   │   └── SystemLog
│   └── repository/
└── user/

User Domain
사용자 계정 관리 및 프로필
user/
├── admin/
├── business/
├── common/
│   ├── entity/
│   │   ├── Account
│   │   ├── Admin
│   │   └── Profile
│   └── repository/
└── personal/
```

  
---

## ✅ 개발 일정 목표 (4월 13일까지)

### ⏰ 1차 목표: **개인회원(Personal) 기능 완성**

- 회원가입 / 로그인
- 이력서 관리
- 공고 목록 및 상세
- 마이페이지 (스크랩, 지원내역, 제안, 후기)
- 채팅, 알림, 캘린더, 개인정보 관리

✅ **→ 4월 13일까지 최소 개인회원 기능은 완성도 있게 구현 완료할 것**


---

### ⏰ 2차 목표: **기업회원 기능 추가 구현**

- 여유가 될 경우 다음 기능까지 확장 작업
  - 기업회원: 공고 등록/관리, 인재 검색, 제안, 결제
  - 관리자: 사용자/공고 관리, 문의 처리, 로그 확인 등

