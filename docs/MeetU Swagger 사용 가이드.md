# MeetU Swagger 사용 가이드

본 문서는 MeetU 프로젝트에서 Swagger를 활용하여 API 인증/인가 테스트를 수행하는 방법을 안내합니다. 각 팀원은 자신의 역할에 따라 Swagger를 통해 기능 테스트를 진행해주세요.

🔗 Swagger 접속 경로:
```
http://localhost:8080/swagger-ui.html
```

---
# MeetU 테스트 로그인 계정

아래 계정으로 Swagger / Postman 테스트를 진행할 수 있습니다.

| 구분     | 아이디              | 비밀번호   |
| ------ | ---------------- | ------ |
| 개인회원   | `testp`          | `1234` |
| 기업회원   | `testb`          | `1234` |
| 슈퍼 관리자 | `super@test.com` | `1234` |
| 일반 관리자 | `admin@test.com` | `1234` |

---

## 1. 로그인 API 호출

본인의 역할(개인회원 / 기업회원 / 관리자)에 맞는 로그인 API를 사용해 accessToken을 발급받습니다.

|역할|API 경로|설명|
|---|---|---|
|개인회원|`POST /api/personal/auth/login`|개인 사용자 로그인|
|기업회원|`POST /api/business/auth/login`|기업 사용자 로그인|
|관리자|`POST /api/admin/auth/login`|관리자 로그인|

요청 예시 (Request Body):

```
{
  "email": "user@example.com",
  "password": "your_password"
}
```

응답 예시 (Response Body):

```
{
  "data": {
    "accessToken": "Bearer eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "..."
  }
}
```

---

## 2. Swagger UI에서 토큰 등록

1. Swagger 접속 → 우측 상단 `Authorize 🔒` 버튼 클릭
    
2. accessToken을 입력:
    ```
    eyJhbGciOiJIUzI1NiIsInR...
    ```

3. "Authorize" 버튼 클릭 후 창 닫기
    
---

## ✅ 3. 기능 API 호출 및 테스트

- Authorize 후, 본인이 담당한 API를 호출하여 동작을 확인합니다.
- 토큰이 적용된 상태로 요청이 자동 전송되므로 별도로 헤더를 입력할 필요는 없습니다.

예:
- 개인회원 → `/api/personal/resume/*` 호출 가능
- 기업회원 → `/api/business/jobs/*` 호출 가능
- 관리자 → `/api/admin/user/*` 호출 가능

---

## ➡️ 추가 안내: **로그인 처리 추가 필요성 관련 안내**

대부분 기능 API들이 아직 로그인(Authentication) 처리가 안 되어 있을 수 있기 때문에,  
Swagger 테스트 이전에 **반드시 다음 사항도 함께 점검해야 합니다**.

---

## 🚨 기능별 로그인 처리 체크리스트

-  Controller에서는  
    ❗ **PathVariable이나 RequestParam으로 accountId, profileId, adminId를 직접 받지 않는다.**  
    ➡️ 대신 **반드시 `SecurityUtil` 메서드를 이용해 가져올 것.**
    
    - `SecurityUtil.getAccountId()`       
    - `SecurityUtil.getProfileId()`
    - `SecurityUtil.getAdminId()`
        
- Service 단 진입 전에,  
    ❗ **현재 로그인한 사용자의 accountId/profileId/companyId/adminId를 가져오는 코드를 삽입한다.**
    
    ➡️ 이후 로직은 해당 값을 기준으로 동작하도록 작성.


---

## ❌ 인가 오류 확인

- 로그인하지 않고 호출 → `401 Unauthorized`
- 다른 역할로 호출 → `403 Forbidden`

예:
- 개인회원이 `/api/admin/user/list` 호출 → `403`이면 정상 처리
- 토큰 없이 호출 → `401`이면 인증 필터 정상 동작
    
