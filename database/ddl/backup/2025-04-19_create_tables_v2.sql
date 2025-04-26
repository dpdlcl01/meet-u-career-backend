-- DROP DATABASE IF EXISTS meetu_db;
-- CREATE DATABASE meetu_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE meetu_db;

-- GRANT ALL PRIVILEGES ON meetu_db.* TO 'test_admin'@'%';


--------------------------------------------------
-- 1. company (기업 테이블)
--------------------------------------------------
CREATE TABLE company (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 기업 고유 ID (자동 증가)
    name VARCHAR(255) NOT NULL,  -- 회사명
    businessNumber VARCHAR(20) NOT NULL UNIQUE,  -- 사업자등록번호 (고유값)
    representativeName VARCHAR(100) NULL,  -- 대표자명
    industry VARCHAR(255) NOT NULL,  -- 업종 (예: IT, 금융 등)
    foundedDate DATE NULL,  -- 설립일
    numEmployees INT NULL,  -- 직원 수
    revenue BIGINT NULL,  -- 매출액
    website VARCHAR(500) NULL,  -- 회사 웹사이트 URL
    logoKey VARCHAR(500) NULL,  -- S3 객체 키 (기업 로고) (NULL 가능)
    address VARCHAR(500) NULL,  -- 회사 주소
    companyType VARCHAR(50) NULL COMMENT '기업 형태 (대기업 등)',
    corpcode VARCHAR(50) NULL COMMENT 'API 연동 기업 코드',
    operatingProfit BIGINT NULL COMMENT '최근 연도 영업이익',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 기업 정보 등록일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 기업 정보 수정일
    status INT NOT NULL DEFAULT 0  -- 기업 상태 (0: 활성, 1: 비활성)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 2. location (지역 테이블)
--------------------------------------------------
CREATE TABLE location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 지역 고유 ID (자동 증가)
    locationCode VARCHAR(50) NOT NULL UNIQUE,  -- 지역 코드 (API 연동 가능)
    province VARCHAR(100) NOT NULL,  -- 광역시/도 (예: 서울특별시, 경기도 등)
    city VARCHAR(100) NULL,  -- 시/군/구 (예: 성남시 분당구, 강남구 등)
    fullLocation VARCHAR(255) NOT NULL,  -- 전체 지역명 (예: "경기도 성남시 분당구")
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 지역 데이터 생성일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 지역 데이터 수정일
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 3. jobCategory (직무 코드 테이블)
-- 사람인 API 기준:
-- 상위 직무 코드 (job-mid-code)와 하위 직무 코드 (job-code)를 모두 저장
-- parentCode를 통해 상하위 관계를 구성
--------------------------------------------------
CREATE TABLE jobCategory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  
        -- 직무 코드 고유 ID (자동 증가)

    jobCode VARCHAR(50) NOT NULL UNIQUE,  
        -- 사람인 API의 직무 코드 값
        -- 상위 직무 코드: job.position.job-mid-code.code
        -- 하위 직무 코드: job.position.job-code.code (복수 가능, 관계 테이블 통해 연결)

    jobName VARCHAR(255) NOT NULL,  
        -- 사람인 API의 직무명
        -- 상위 직무명: job.position.job-mid-code.name
        -- 하위 직무명: job.position.job-code.name

    parentCode VARCHAR(50) DEFAULT NULL,  
        -- 상위 직무 코드와의 연결 관계를 위한 필드
        -- 상위 직무일 경우 NULL, 하위 직무일 경우 상위 jobCode 참조
        -- 예: 게임개발(2072)의 parentCode = '22' (IT개발·데이터)

    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  
        -- 직무 코드 데이터 생성일
	FOREIGN KEY (parentCode) REFERENCES jobCategory(jobCode) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 4. account (계정 테이블) - 로그인 ID 및 이메일 정책 반영
--------------------------------------------------
CREATE TABLE account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 회원 계정 고유 ID (자동 증가)
    
    userId VARCHAR(50) UNIQUE NULL,  -- 로그인용 ID (일반 로그인 시 필수, OAuth는 NULL 가능)
    email VARCHAR(255) NULL,  -- 이메일 (중복 가능, 인증용 등으로 사용)
    password VARCHAR(255) NULL,  -- 비밀번호 (일반 로그인 시 사용, OAuth 로그인 시 NULL)
    
    name VARCHAR(50) NOT NULL,  -- 이름
    phone VARCHAR(20) NOT NULL,  -- 연락처
    birthday DATE NOT NULL,  -- 생년월일 (필수값)
    accountType INT NOT NULL,  -- 회원 계정 유형 (0: 개인 계정, 1: 기업 계정)
    position VARCHAR(100) NULL,  -- 기업 계정인 경우 담당 직책 (예: HR Manager, CTO 등)
    
    oauthProvider INT DEFAULT NULL,  -- OAuth 제공자 (1: GOOGLE, 2: KAKAO, 3: NAVER)
    oauthId VARCHAR(255) UNIQUE NULL,  -- OAuth 사용자 고유 ID (OAuth 로그인 시 사용)
    
    companyId BIGINT NULL,  -- 기업 계정의 경우 소속된 회사 ID (NULL 가능)

    -- ✅ 사업자등록증 이미지 관련 컬럼
    businessFileKey VARCHAR(500) NULL,     -- S3 객체 키 (사업자등록증)
    businessFileName VARCHAR(255) NULL,    -- 업로드된 파일의 원본 이름

    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 계정 생성일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 계정 정보 수정일
    status INT NOT NULL DEFAULT 0,  -- 계정 상태 (0: 활성, 1: 비활성, 2: 기업계정 승인 대기 중, 3: 기업계정 반려됨)
    
    FOREIGN KEY (companyId) REFERENCES company(id) ON DELETE SET NULL  -- 기업 계정 삭제 시 NULL 처리
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 5. admin (관리자 테이블)
--------------------------------------------------
CREATE TABLE admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 관리자 ID (자동 증가)
    email VARCHAR(255) NOT NULL UNIQUE,  -- 관리자 이메일 (로그인 ID)
    password VARCHAR(255) NOT NULL,  -- 비밀번호 (해시 저장)
    name VARCHAR(50) NOT NULL,  -- 관리자 이름
    role INT NOT NULL,          -- 1: SUPER, 2: ADMIN
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- 계정 생성일
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 6. profile (구직자 프로필 테이블)
--------------------------------------------------
CREATE TABLE profile (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 프로필 고유 ID (자동 증가)
   accountId BIGINT NOT NULL UNIQUE,  -- 계정 ID (각 개인 계정은 1개의 프로필만 가짐)
   locationId BIGINT NULL,  -- 거주 지역 ID (NULL 허용)
   experienceLevel INT NULL,  -- 경력 수준 (예: 1년, 3년, 5년 등)
   educationLevel INT NULL,  -- 학력 수준 (예: 학사, 석사 등)
   skills TEXT NULL,  -- 보유 기술 (예: "Java, Spring, SQL")
   desiredJobCategoryId BIGINT NULL,  -- 희망 직무 ID (jobCategory 테이블 FK)
   desiredLocationId BIGINT NULL,  -- 희망 근무 지역 ID (NULL 가능)
   desiredSalaryCode INT NULL,  -- 희망 연봉 코드 (사람인 API 코드와 동일)
   profileImageKey VARCHAR(500) NULL,  -- S3 객체 키 (프로필 이미지)
   createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 프로필 생성일
   updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 프로필 수정일
   FOREIGN KEY (accountId) REFERENCES account(id) ON DELETE CASCADE,  -- 계정 삭제 시 프로필도 삭제
   FOREIGN KEY (locationId) REFERENCES location(id) ON DELETE SET NULL,  -- 거주 지역 삭제 시 NULL 처리
   FOREIGN KEY (desiredLocationId) REFERENCES location(id) ON DELETE SET NULL,  -- 희망 지역 삭제 시 NULL 처리
   FOREIGN KEY (desiredJobCategoryId) REFERENCES jobCategory(id) ON DELETE SET NULL  -- 희망 직무 삭제 시 NULL 처리
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 7. jobPosting (채용 공고 테이블)
-- 사람인 API에서 수집한 채용 공고 데이터를 저장하는 메인 테이블
--------------------------------------------------
CREATE TABLE jobPosting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 채용 공고 고유 ID (자동 증가)

    jobId VARCHAR(50) UNIQUE NULL,         -- 외부 API 공고 ID (예: 사람인 job.id) / 자체 등록 공고는 NULL

    companyId BIGINT NOT NULL,             -- 기업 ID (공고를 등록한 회사)
    businessAccountId BIGINT NULL,         -- 채용 담당자 계정 ID (NULL 가능)

    title VARCHAR(255) NOT NULL,           -- 공고 제목 (position.title)
    jobUrl VARCHAR(500) NULL,              -- 공고 상세 페이지 URL (job.url)
    industry VARCHAR(255) NOT NULL,        -- 산업 분야명 (position.industry.name) - 코드 없이 텍스트만 저장
    jobType VARCHAR(255) NOT NULL,         -- 근무 형태 (position.job-type.name, 예: 정규직)

    locationCode VARCHAR(50) NOT NULL,     -- 근무 지역 코드 (position.location.code)

    experienceLevel INT NOT NULL,          -- 경력 코드 (experience-level.code) / 0~3
    educationLevel INT NOT NULL,           -- 학력 코드 (required-education-level.code) / 0~9

    salaryCode INT NOT NULL,               -- 연봉 코드 (salary.code)
    salaryRange VARCHAR(255) NOT NULL,     -- 연봉 범위 텍스트 (salary.name, 예: "3,000~4,000만원")

    postingDate DATETIME NOT NULL,         -- 공고 게시일 (posting-date 또는 posting-timestamp 기준)
    openingDate DATETIME,                  -- ✅ 공고 접수 시작일 (opening-timestamp)
    expirationDate DATETIME NOT NULL,      -- 공고 마감일 (expiration-date 또는 expiration-timestamp)

    closeType INT NOT NULL,                -- 마감 형식 (close-type.code) / 1: 마감일, 2: 채용 시 등

    viewCount INT NOT NULL DEFAULT 0,      -- ✅ 공고 조회수 (read-cnt, 없으면 0)
    applyCount INT NOT NULL DEFAULT 0,     -- ✅ 공고 지원자 수 (apply-cnt, 없으면 0)

    keyword TEXT,                          -- ✅ 공고 키워드 (keyword 필드 - 쉼표로 구분된 문자열)
    
    templateType INT DEFAULT NULL,         -- ✅ 공고 템플릿 유형 (예: 1: 기본 템플릿, 2: 상세 템플릿 등)
    description TEXT DEFAULT NULL,         -- ✅ 커스텀 공고 상세 설명

    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 공고 등록일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 공고 수정일

    status INT NOT NULL,                   -- 공고 상태 (0: 비활성, 1: 승인 대기, 2: 활성)

    FOREIGN KEY (companyId) REFERENCES company(id) ON DELETE CASCADE,
    FOREIGN KEY (locationCode) REFERENCES location(locationCode) ON DELETE RESTRICT,
    FOREIGN KEY (businessAccountId) REFERENCES account(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 8. jobPostingViewLog (채용 공고 조회 테이블)
--------------------------------------------------
CREATE TABLE jobPostingViewLog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profileId BIGINT NOT NULL,  -- 개인회원 ID (Foreign Key)
    jobPostingId BIGINT NOT NULL,  -- 조회한 채용 공고 ID (Foreign Key)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 조회한 날짜
    FOREIGN KEY (profileId) REFERENCES profile(id),  -- profile 테이블과 연결
    FOREIGN KEY (jobPostingId) REFERENCES jobPosting(id)  -- jobPosting 테이블과 연결
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 9. coverLetter (자기소개서 테이블)
--------------------------------------------------
CREATE TABLE coverLetter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 자기소개서 고유 ID (자동 증가)
    profileId BIGINT NOT NULL,  -- 구직자 프로필 ID
    title VARCHAR(255) NOT NULL,  -- 자기소개서 제목
    status INT NOT NULL DEFAULT 0,  -- 자기소개서 상태 (0: 활성, 1: 임시저장, 2: 삭제 대기)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 자기소개서 생성일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 자기소개서 수정일
    FOREIGN KEY (profileId) REFERENCES profile(id) ON DELETE CASCADE  -- 프로필 삭제 시 함께 삭제됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 10. coverLetterContent (자기소개서 항목 테이블)
--------------------------------------------------
CREATE TABLE coverLetterContent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 자기소개서 항목 고유 ID (자동 증가)
    coverLetterId BIGINT NOT NULL,  -- 자기소개서 ID (외래 키)
    sectionTitle VARCHAR(255) NOT NULL,  -- 항목 제목 (예: "지원 동기", "성장 과정")
    content TEXT NOT NULL,  -- 항목 내용
    contentOrder INT NOT NULL,  -- 순서 (Drag & Drop 정렬 가능)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 항목 생성일
    FOREIGN KEY (coverLetterId) REFERENCES coverLetter(id) ON DELETE CASCADE  -- 자기소개서 삭제 시 함께 삭제됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 11. coverLetterContentFeedback (자기소개서 항목별 AI 코칭 결과)
--------------------------------------------------
CREATE TABLE coverLetterContentFeedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 피드백 고유 ID
    contentId BIGINT NOT NULL,             -- 연관된 자기소개서 항목 ID (FK) - UNIQUE 제약조건 제거됨
    originalContent TEXT NOT NULL,         -- 피드백 요청 시점의 원본 내용
    feedback TEXT NOT NULL,                -- AI 피드백 문장 (1~2문장 요약 피드백)
    revisedContent TEXT NOT NULL,          -- AI가 수정한 전체 자기소개서 문장
    isApplied BOOLEAN DEFAULT FALSE,       -- 적용 여부
    appliedAt TIMESTAMP NULL,              -- 적용 시점
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 생성일시
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 수정일시
    FOREIGN KEY (contentId) REFERENCES coverLetterContent(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 12. coverLetterContentFitAnalysis (자기소개서 항목별 직무 적합도 분석)
--------------------------------------------------
CREATE TABLE coverLetterContentFitAnalysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 고유 ID
	contentId BIGINT NOT NULL, -- 자기소개서 항목 ID (FK)
	jobCategoryId BIGINT NOT NULL, -- 직무 ID (FK → jobCategory)
	fitScore DECIMAL(5,2) NOT NULL, -- 적합도 점수 (0~100)
	comment VARCHAR(1000) NOT NULL, -- 분석 요약 코멘트
	createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 시각
	FOREIGN KEY (contentId) REFERENCES coverLetterContent(id) ON DELETE CASCADE,
	FOREIGN KEY (jobCategoryId) REFERENCES jobCategory(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 13. resume (이력서 정보 테이블)
--------------------------------------------------
CREATE TABLE resume (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 이력서 고유 ID (자동 증가)
    profileId BIGINT NOT NULL,  -- 구직자 프로필 ID
    title VARCHAR(255) NOT NULL,  -- 이력서 제목
    resumeType INT NOT NULL,  -- 이력서 유형 (0: 직접 입력, 1: 파일 첨부, 2: URL)
    
    -- ✅ S3 관련 컬럼
    resumeFileKey VARCHAR(500) NULL,  -- S3 객체 키 (이력서 파일)
    resumeFileName VARCHAR(255) NULL,  -- 원본 파일명 (이력서)
    resumeFileType VARCHAR(100) NULL,  -- 파일 타입 (예: pdf, docx)
    
    resumeUrl VARCHAR(500) NULL,  -- 이력서 URL (웹 이력서 활용 시)
    overview TEXT NOT NULL,  -- 간단한 자기소개 (이력서 개요)
    coverLetterId BIGINT NULL,  -- 연결된 자기소개서 ID (선택 사항)
    extraLink1 VARCHAR(500) NULL,  -- 추가 링크 (GitHub, 포트폴리오)
    extraLink2 VARCHAR(500) NULL,  -- 추가 링크 (LinkedIn, 블로그 등)
    isPrimary BOOLEAN NOT NULL DEFAULT FALSE,  -- 대표 이력서 여부 (TRUE: 대표 이력서, FALSE: 일반 이력서)
    status INT NOT NULL DEFAULT 0,  -- 이력서 상태 (0: 임시저장, 1: 비공개 등록, 2: 공개 등록, 3: 삭제)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 이력서 생성일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 이력서 수정일

    FOREIGN KEY (profileId) REFERENCES profile(id) ON DELETE CASCADE,  -- 프로필 삭제 시 함께 삭제됨
    FOREIGN KEY (coverLetterId) REFERENCES coverLetter(id) ON DELETE SET NULL  -- 자기소개서 삭제 시 NULL 처리
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 14. resumeContent (이력서 항목 테이블)
--------------------------------------------------
CREATE TABLE resumeContent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 이력서 항목 고유 ID (자동 증가)
    resumeId BIGINT NOT NULL,  -- 해당 이력서 ID (외래 키)
    sectionType INT NOT NULL,  -- 항목 유형 (0: 학력, 1: 경력, 2: 자격증, 3: 활동)
    sectionTitle VARCHAR(255) NOT NULL,  -- 항목 제목 ("경력", "학력" 등)
    contentOrder INT NOT NULL,  -- 항목 순서 (낮을수록 상단 표시)
    organization VARCHAR(255) NOT NULL,  -- 기관/기업명/발급기관 (예: "서울대학교", "삼성전자", "한국산업인력공단")
    title VARCHAR(255) NOT NULL,  -- 직책, 학위명 또는 자격증명 (예: "백엔드 개발자", "정보처리기사")
    field VARCHAR(255) NOT NULL,  -- 전공, 직무 분야 (예: "컴퓨터공학"), 관련 분야 (예: "IT", "금융", "교육")
    description TEXT NOT NULL,  -- 상세 설명
    dateFrom DATE NULL,  -- 시작일 또는 발급일 (자격증일 경우 이 컬럼 사용)
    dateTo DATE NULL,  -- 종료일 (자격증이면 NULL)
    
    -- ✅ S3 파일 첨부 항목 (선택적)
    contentFileKey VARCHAR(500) NULL,  -- S3 객체 키 (항목별 첨부파일)
    contentFileName VARCHAR(255) NULL,  -- 원본 파일명 (항목별)
    contentFileType VARCHAR(100) NULL,  -- 파일 타입 (예: pdf, jpg)
    
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 이력서 항목 생성일
    FOREIGN KEY (resumeId) REFERENCES resume(id) ON DELETE CASCADE  -- 이력서 삭제 시 함께 삭제됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 15. application (지원서 테이블)
--------------------------------------------------
CREATE TABLE application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 지원서 고유 ID (자동 증가)
    profileId BIGINT NOT NULL,  -- 지원자의 프로필 ID
    jobPostingId BIGINT NOT NULL,  -- 지원한 채용 공고 ID
    resumeId BIGINT NOT NULL,  -- 제출한 이력서 ID
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 지원 날짜
    status INT NOT NULL DEFAULT 0,  -- 지원 상태 (0: 지원 완료, 1: 서류 통과(면접 예정), 2: 서류 불합격, 3: 면접 완료)
    FOREIGN KEY (profileId) REFERENCES profile(id) ON DELETE CASCADE,  -- 프로필 삭제 시 지원 내역도 삭제됨
    FOREIGN KEY (jobPostingId) REFERENCES jobPosting(id) ON DELETE CASCADE,  -- 채용 공고 삭제 시 지원 내역도 삭제됨
    FOREIGN KEY (resumeId) REFERENCES resume(id) ON DELETE CASCADE  -- 이력서 삭제 시 지원 내역도 삭제됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 16. resumeViewLog (이력서 열람 로그 테이블)
--------------------------------------------------
CREATE TABLE resumeViewLog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 이력서 열람 로그 ID (자동 증가)
    resumeId BIGINT NOT NULL,  -- 열람된 이력서 ID
    companyId BIGINT NOT NULL,  -- 열람한 기업 ID
    applicationId BIGINT NULL,  -- 해당 열람이 특정 지원서와 연관된 경우 (NULL 가능)
    viewType INT NOT NULL,  -- 열람 유형 (0: 일반 열람, 1: 평가 목적으로 열람)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 열람 시간
    isNotified BOOLEAN DEFAULT FALSE,  -- 열람 알림 여부 (TRUE: 알림 발송됨)
    FOREIGN KEY (resumeId) REFERENCES resume(id) ON DELETE CASCADE,  -- 이력서 삭제 시 로그도 삭제됨
    FOREIGN KEY (companyId) REFERENCES company(id) ON DELETE CASCADE,  -- 기업 삭제 시 로그도 삭제됨
    FOREIGN KEY (applicationId) REFERENCES application(id) ON DELETE SET NULL  -- 지원 내역 삭제 시 NULL 처리
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 17. interviewReview (면접 리뷰 테이블)
--------------------------------------------------
CREATE TABLE interviewReview (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 면접 리뷰 고유 ID (자동 증가)
    profileId BIGINT NOT NULL,  -- 리뷰 작성자의 프로필 ID
    companyId BIGINT NOT NULL,  -- 면접 본 기업 ID
    applicationId BIGINT NOT NULL UNIQUE,  -- 리뷰가 연결된 지원 내역 (1:1 관계)
    
    -- 기본 정보 (이미지 1)
    jobCategoryId BIGINT NOT NULL,  -- 직무 카테고리 ID (jobCategory 테이블의 id 참조)
    careerLevel INT NOT NULL,       -- 면접 당시 경력 (0: 신입, 1: 경력)
    interviewYearMonth VARCHAR(7) NOT NULL,  -- 면접 연도와 월 (YYYY-MM 형식)
    
    -- 면접 정보 (이미지 2)
    rating INT NOT NULL,           -- 전반적인 평가 (0: 부정적, 1: 보통, 2: 긍정적)
    difficulty INT NOT NULL,       -- 난이도 (1~5점)
    
    -- 면접 및 전형 유형 (int 형태로 변경, 비트 연산으로 다중 선택 저장)
    interviewType INT NOT NULL DEFAULT 0,  -- 면접 유형 비트맵
                                          -- 1: 직무/인성면접
                                          -- 2: 토론면접
                                          -- 4: 인적성 검사
                                          -- 8: PT면접
                                          -- 16: 실무 과제 및 시험
                                          -- 32: 기타
    
    -- 면접 인원
    interviewParticipants INT NOT NULL,  -- 면접 인원 (0: 1:1면접, 1: 지원자 1명, 면접관 다수, 2: 그룹면접)
    
    -- 면접 질문 (이미지 3)
    hasFrequentQuestions BOOLEAN DEFAULT FALSE,  -- 자주 나오는 질문에서 선택하기
    questionsAsked TEXT,           -- 면접 질문 (여러 질문을 하나의 텍스트로 저장)
    
    interviewTip TEXT,              -- 면접 팁
    
    -- 합격 여부
    result INT NOT NULL,            -- 면접 결과 (0: 불합격, 1: 합격, 2: 대기중)
    
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 리뷰 작성일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 리뷰 수정일
    status INT NOT NULL DEFAULT 0,  -- 리뷰 상태 (0: 활성, 1: 삭제 요청)
    
    FOREIGN KEY (profileId) REFERENCES profile(id) ON DELETE CASCADE,  -- 프로필 삭제 시 리뷰도 삭제됨
    FOREIGN KEY (companyId) REFERENCES company(id) ON DELETE CASCADE,  -- 기업 삭제 시 리뷰도 삭제됨
    FOREIGN KEY (applicationId) REFERENCES application(id) ON DELETE CASCADE,  -- 지원 내역 삭제 시 리뷰도 삭제됨
    FOREIGN KEY (jobCategoryId) REFERENCES jobCategory(id) ON DELETE CASCADE  -- 직무 카테고리 참조
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 18. bookmark (채용 공고 북마크 테이블)
--------------------------------------------------
CREATE TABLE bookmark (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 북마크 ID (자동 증가)
    profileId BIGINT NOT NULL,  -- 북마크한 구직자 프로필 ID
    jobPostingId BIGINT NOT NULL,  -- 북마크한 채용 공고 ID
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 북마크한 날짜
    FOREIGN KEY (profileId) REFERENCES profile(id) ON DELETE CASCADE,  -- 프로필 삭제 시 북마크도 삭제됨
    FOREIGN KEY (jobPostingId) REFERENCES jobPosting(id) ON DELETE CASCADE  -- 공고 삭제 시 북마크도 삭제됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 19. companyFollow (관심 기업 테이블)
--------------------------------------------------
CREATE TABLE companyFollow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 팔로우 ID (자동 증가)
    profileId BIGINT NOT NULL,  -- 팔로우한 구직자 프로필 ID
    companyId BIGINT NOT NULL,  -- 팔로우한 기업 ID
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 팔로우한 날짜
    FOREIGN KEY (profileId) REFERENCES profile(id) ON DELETE CASCADE,  -- 프로필 삭제 시 팔로우 정보도 삭제됨
    FOREIGN KEY (companyId) REFERENCES company(id) ON DELETE CASCADE  -- 기업 삭제 시 팔로우 정보도 삭제됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 20. companyBlock (기업 차단 테이블)
--------------------------------------------------
CREATE TABLE companyBlock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 차단 ID (자동 증가)
    profileId BIGINT NOT NULL,             -- 차단을 설정한 구직자의 프로필 ID
    companyId BIGINT NOT NULL,             -- 차단된 기업 ID
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 차단 생성일
	FOREIGN KEY (profileId) REFERENCES profile(id) ON DELETE CASCADE,  -- 프로필 삭제 시 차단 정보도 삭제
    FOREIGN KEY (companyId) REFERENCES company(id) ON DELETE CASCADE   -- 기업 삭제 시 차단 정보도 삭제
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 21. calendarEvent (캘린더 이벤트 테이블)
--------------------------------------------------
CREATE TABLE calendarEvent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 캘린더 이벤트 ID (자동 증가)
    accountId BIGINT NOT NULL,  -- 일정 등록한 사용자 ID
    companyId BIGINT NULL,  -- 일정이 속한 기업 ID (NULL 가능)
    eventType INT NOT NULL,  -- 일정 유형 (1: 면접, 2: 지원마감, 3: 북마크 마감, 4: 기업 이벤트, 5: 개인 일정)
    title VARCHAR(255) NOT NULL,  -- 일정 제목
    description TEXT NULL,  -- 일정 설명
    relatedId BIGINT NULL,  -- 관련된 데이터 ID (지원 ID, 공고 ID 등)
    startDateTime TIMESTAMP NOT NULL,  -- 일정 시작 날짜 및 시간
    endDateTime TIMESTAMP NOT NULL,  -- 일정 종료 날짜 및 시간
    isAllDay BOOLEAN NOT NULL DEFAULT FALSE,  -- 종일 일정 여부 (TRUE: 종일, FALSE: 시간 지정)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 일정 생성일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 일정 수정일
    FOREIGN KEY (accountId) REFERENCES account(id) ON DELETE CASCADE,  -- 계정 삭제 시 일정도 삭제됨
    FOREIGN KEY (companyId) REFERENCES company(id) ON DELETE SET NULL  -- 기업 삭제 시 NULL 처리
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 22. notification (알림 테이블)
--------------------------------------------------
CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 알림 고유 ID (자동 증가)
    accountId BIGINT NOT NULL,  -- 알림을 받는 사용자 ID
    notificationType INT NOT NULL,  -- 알림 유형 (아래 주석 참조)
    /*
    -- 개인회원 알림
    1: APPLICATION_STATUS - 지원서 상태 변경 (서류 통과, 불합격 등)
    2: INTERVIEW_SCHEDULE - 면접 일정 관련 알림
    3: JOB_RECOMMENDATION - 맞춤 채용공고 추천
    4: OFFER_RECEIVED - 채용 제안 수신
    5: MESSAGE_RECEIVED - 새 메시지 수신
    
    -- 기업회원 알림
    11: NEW_APPLICATION - 새로운 지원자 발생
    12: APPLICATION_WITHDRAWN - 지원 취소
    13: JOB_POSTING_EXPIRING - 공고 만료 임박
    14: PAYMENT_NOTIFICATION - 결제/광고 관련 알림
    15: TALENT_SUGGESTION - 추천 인재 알림
    
    -- 공통 알림
    21: SYSTEM_NOTIFICATION - 시스템 공지/점검
    22: ACCOUNT_SECURITY - 계정 보안 관련 알림
    */
    message TEXT NOT NULL,  -- 알림 메시지 내용
    relatedId BIGINT NOT NULL,  -- 관련된 데이터 ID (지원서, 채용 공고, 채팅방 등과 연관 가능)
    isRead INT NOT NULL DEFAULT 0,  -- 읽음 여부 (0: 안 읽음, 1: 읽음)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 알림 생성일
    FOREIGN KEY (accountId) REFERENCES account(id) ON DELETE CASCADE  -- 계정 삭제 시 알림도 삭제됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 23. chatRoom (채팅방 테이블)
--------------------------------------------------
CREATE TABLE chatRoom (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 채팅방 고유 ID (자동 증가)
    companyId BIGINT NOT NULL,  -- 채팅을 개설한 기업 ID
    businessAccountId  BIGINT NOT NULL,  -- 채팅을 시작한 채용 담당자 ID
    personalAccountId  BIGINT NOT NULL,  -- 채팅을 받은 구직자 ID
    resumeId BIGINT NULL,  -- 관련된 이력서 ID
    status INT NOT NULL DEFAULT 0 COMMENT '0: 열림, 1: 닫힘',  -- 채팅방 상태 (닫힌 채팅방은 메시지 전송 불가)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 채팅방 생성일
    FOREIGN KEY (companyId) REFERENCES company(id) ON DELETE CASCADE,  -- 기업 삭제 시 채팅방도 삭제됨
    FOREIGN KEY (businessAccountId) REFERENCES account(id) ON DELETE CASCADE,  -- 채용 담당자 계정 삭제 시 채팅방도 삭제됨
    FOREIGN KEY (personalAccountId) REFERENCES account(id) ON DELETE CASCADE,  -- 구직자 계정 삭제 시 채팅방도 삭제됨
    FOREIGN KEY (resumeId) REFERENCES resume(id) ON DELETE SET NULL  -- 이력서 삭제 시 NULL 처리됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 24. chatMessage (채팅 메시지 테이블)
--------------------------------------------------
CREATE TABLE chatMessage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 메시지 고유 ID (자동 증가)
    chatRoomId BIGINT NOT NULL,  -- 메시지가 속한 채팅방 ID
    senderId BIGINT NOT NULL,  -- 메시지를 보낸 사용자 ID
    senderType INT NOT NULL,  -- 보낸 사람 유형 (0: 개인 계정, 1: 기업 계정)
    message TEXT NOT NULL,  -- 채팅 메시지 내용
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 메시지 전송일
    isRead INT NOT NULL DEFAULT 0,  -- 읽음 여부 (0: 안 읽음, 1: 읽음)
    FOREIGN KEY (chatRoomId) REFERENCES chatRoom(id) ON DELETE CASCADE,  -- 채팅방 삭제 시 메시지도 삭제됨
    FOREIGN KEY (senderId) REFERENCES account(id) ON DELETE CASCADE  -- 계정 삭제 시 해당 계정의 메시지도 삭제됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 25. offer (채용 제안 테이블)
--------------------------------------------------
CREATE TABLE offer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 채용 제안 고유 ID (자동 증가)
    businessAccountId BIGINT NOT NULL,  -- 제안을 보낸 채용 담당자 ID
    companyId BIGINT NOT NULL,  -- 제안을 보낸 담당자가 소속된 기업 ID
    personalAccountId BIGINT NOT NULL,  -- 제안을 받은 구직자 ID
    chatRoomId BIGINT NOT NULL UNIQUE,  -- 채팅방 ID (한 채팅방당 하나의 제안만 가능)
    status INT NOT NULL DEFAULT 0,  -- 제안 상태 (0: 대기, 1: 수락, 2: 거절, 3: 만료됨)
    message TEXT NOT NULL,  -- 제안 메시지 (예: "당신의 프로필을 보고 연락드립니다.")
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 제안 발송일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 상태 변경일
    FOREIGN KEY (businessAccountId) REFERENCES account(id) ON DELETE CASCADE,  -- 채용 담당자 삭제 시 제안도 삭제됨
    FOREIGN KEY (companyId) REFERENCES company(id) ON DELETE CASCADE,  -- 기업 삭제 시 제안도 삭제됨
    FOREIGN KEY (personalAccountId) REFERENCES account(id) ON DELETE CASCADE,  -- 구직자 삭제 시 제안도 삭제됨
    FOREIGN KEY (chatRoomId) REFERENCES chatRoom(id) ON DELETE CASCADE  -- 채팅방 삭제 시 제안도 삭제됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 26. communityTag (커뮤니티 태그 테이블)
--------------------------------------------------
CREATE TABLE communityTag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 태그 고유 ID (자동 증가)
    name VARCHAR(50) NOT NULL UNIQUE,  -- 태그명 (예: "이직", "면접", "연봉")
    status INT NOT NULL DEFAULT 0,  -- 태그 상태 (0: 활성, 1: 비활성)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 태그 생성일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 태그 수정일
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 27. communityPost (커뮤니티 게시글 테이블)
--------------------------------------------------
CREATE TABLE communityPost (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 게시글 고유 ID (자동 증가)
    accountId BIGINT NOT NULL,  -- 게시글 작성자 ID
    tagId BIGINT NOT NULL,  -- 게시글의 태그 ID
    title VARCHAR(255) NOT NULL,  -- 게시글 제목
    content TEXT NOT NULL,  -- 게시글 내용
    
    postImageKey VARCHAR(500) NULL,  -- S3 객체 키 (대표 이미지)
    
    likeCount INT NOT NULL DEFAULT 0,  -- 좋아요 수
    commentCount INT NOT NULL DEFAULT 0,  -- 댓글 수
    status INT NOT NULL DEFAULT 0,  -- 게시글 상태 (0: 활성, 1: 삭제됨)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 게시글 생성일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 게시글 수정일
    FOREIGN KEY (accountId) REFERENCES account(id) ON DELETE CASCADE,  -- 계정 삭제 시 게시글도 삭제됨
    FOREIGN KEY (tagId) REFERENCES communityTag(id) ON DELETE RESTRICT  -- 태그 삭제 방지
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 28. communityComment (커뮤니티 댓글 테이블)
--------------------------------------------------
CREATE TABLE communityComment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 댓글 고유 ID (자동 증가)
    postId BIGINT NOT NULL,  -- 댓글이 속한 게시글 ID
    accountId BIGINT NOT NULL,  -- 댓글 작성자 ID
    content TEXT NOT NULL,  -- 댓글 내용
    status INT NOT NULL DEFAULT 0,  -- 댓글 상태 (0: 활성, 1: 삭제됨)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 댓글 생성일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 댓글 수정일
    FOREIGN KEY (postId) REFERENCES communityPost(id) ON DELETE CASCADE,  -- 게시글 삭제 시 댓글도 삭제됨
    FOREIGN KEY (accountId) REFERENCES account(id) ON DELETE CASCADE  -- 계정 삭제 시 해당 계정의 댓글도 삭제됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 29. communityLike (게시글 좋아요 테이블)
--------------------------------------------------
CREATE TABLE communityLike (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 좋아요 고유 ID (자동 증가)
    accountId BIGINT NOT NULL,  -- 좋아요를 누른 사용자 ID
    postId BIGINT NULL,  -- 좋아요를 누른 게시글 ID (NULL 가능)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 좋아요 생성일
    FOREIGN KEY (accountId) REFERENCES account(id) ON DELETE CASCADE,  -- 계정 삭제 시 좋아요도 삭제됨
    FOREIGN KEY (postId) REFERENCES communityPost(id) ON DELETE CASCADE  -- 게시글 삭제 시 좋아요도 삭제됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 30. jobPostingJobCategory (공고 - 직무 관계 테이블)
--------------------------------------------------
CREATE TABLE jobPostingJobCategory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 공고-직무 관계 고유 ID (자동 증가)
    
    jobPostingId BIGINT NOT NULL,          -- 공고 ID
    jobCategoryId BIGINT NOT NULL,         -- 직무 ID

    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 관계 생성일

    -- 인덱스 및 제약 조건
    UNIQUE KEY uq_job_posting_category (jobPostingId, jobCategoryId),  -- 중복 방지
    INDEX idx_jobPostingId (jobPostingId),      -- 공고 기준 검색 성능 향상
    INDEX idx_jobCategoryId (jobCategoryId),    -- 직무 기준 검색 성능 향상

    -- 외래 키 제약 조건
    FOREIGN KEY (jobPostingId) REFERENCES jobPosting(id) ON DELETE CASCADE,
    FOREIGN KEY (jobCategoryId) REFERENCES jobCategory(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 31. payment (결제 테이블)
--------------------------------------------------
CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- 결제 ID (자동 증가)
    accountId BIGINT NOT NULL,             -- 결제한 계정 (기업 회원)
    amount DECIMAL(10,2) NOT NULL,         -- 결제 금액
    status INT NOT NULL,                   -- 결제 상태 (0: 실패, 1: 성공)
    provider INT NOT NULL,                 -- 결제 제공업체 (1: TOSS, 2: KAKAO, 3: NAVER)
    method INT NOT NULL,                   -- 결제 방식 (1: CARD, 2: KAKAO_PAY, 3: NAVER_PAY, 4: TOSS_PAY)
    transactionId VARCHAR(100) NOT NULL UNIQUE, -- 거래 ID (중복 방지)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 결제 생성일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 결제 상태 변경일
    FOREIGN KEY (accountId) REFERENCES account(id) ON DELETE CASCADE  -- 결제한 기업 담당자 삭제 시 해당 결제도 삭제됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 32. advertisement (광고 테이블)
--------------------------------------------------
CREATE TABLE advertisement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 광고 고유 ID (자동 증가)
    jobPostingId BIGINT NOT NULL,          -- 광고 대상 공고 ID
    companyId BIGINT NOT NULL,             -- 광고를 신청한 기업 ID
    adType INT NOT NULL,                   -- 광고 유형 (1: BASIC, 2: STANDARD, 3: PREMIUM)
    status INT NOT NULL DEFAULT 1,         -- 광고 상태 (1: 활성, 2: 일시중지, 3: 만료, 4: 승인대기 등)
    durationDays INT NOT NULL,             -- 광고 기간 (단위: 일)
    startDate TIMESTAMP NOT NULL,          -- 광고 시작 날짜
    endDate TIMESTAMP NOT NULL,            -- 광고 종료 날짜
    paymentId BIGINT NULL,                 -- 광고 결제 ID (NULL 가능)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 광고 생성일
    FOREIGN KEY (jobPostingId) REFERENCES jobPosting(id) ON DELETE CASCADE,  -- 공고 삭제 시 해당 광고도 삭제됨
    FOREIGN KEY (companyId) REFERENCES company(id) ON DELETE CASCADE,  -- 기업 삭제 시 광고도 삭제됨
    FOREIGN KEY (paymentId) REFERENCES payment(id) ON DELETE SET NULL  -- 결제 정보 삭제 시 NULL 처리됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 33. customerSupport (고객센터 문의 테이블)
--------------------------------------------------
CREATE TABLE customerSupport (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 고객 문의 고유 ID (자동 증가)
    accountId BIGINT NOT NULL,  -- 문의를 보낸 사용자 ID
    adminId BIGINT NULL,  -- 답변을 담당하는 관리자 ID (NULL 가능)
    category INT NOT NULL,  -- 문의 카테고리 (0: PAYMENT, 1: SERVICE, 2: ACCOUNT, 3: JOB_POSTING, 4: COMPANY, 5: RESUME, 6: APPLICATION, 7: OTHER)
    title VARCHAR(255) NOT NULL,  -- 문의 제목
    description TEXT NOT NULL,  -- 문의 상세 내용
    response TEXT NULL,  -- 관리자의 답변 (NULL 가능)
    status INT NOT NULL DEFAULT 0,  -- 상태 (0: PENDING, 1: IN_PROGRESS, 2: RESOLVED)
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 문의 생성일
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 문의 상태 변경일
    FOREIGN KEY (accountId) REFERENCES account(id) ON DELETE CASCADE,  -- 계정 삭제 시 문의도 삭제됨
    FOREIGN KEY (adminId) REFERENCES admin(id) ON DELETE SET NULL,  -- 담당 관리자 삭제 시 NULL 처리됨
    INDEX idx_cs_accountId (accountId),
    INDEX idx_cs_adminId (adminId),
    INDEX idx_cs_category (category),
    INDEX idx_cs_status (status),
    INDEX idx_cs_createdAt (createdAt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--------------------------------------------------
-- 34. systemLog (시스템 로그 테이블)
--------------------------------------------------
CREATE TABLE systemLog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 시스템 로그 고유 ID (자동 증가)
    accountId BIGINT NULL,  -- 로그를 남긴 사용자 ID (NULL 가능)
    adminId BIGINT NULL,  -- 로그를 남긴 관리자 ID (NULL 가능)
    -- 로그 유형 (INT로 저장하여 성능 최적화)
    -- 0: SECURITY - 보안 관련 로그 (로그인, 비밀번호 변경 등)
    -- 1: TRANSACTION - 거래 관련 로그 (결제, 환불 등)
    -- 2: USER - 사용자 활동 로그 (중요 설정 변경 등)
    -- 3: ADMIN - 관리자 활동 로그 (회원 상태 변경, 콘텐츠 관리 등)
    -- 4: ERROR - 오류 로그 (시스템 오류, 예외 발생 등)
    logType INT NOT NULL,
    
    -- 모듈 유형 (INT로 저장하여 성능 최적화)
    -- 0: AUTH - 인증/인가 모듈 (로그인, 로그아웃, 회원가입, 비밀번호 관리)
    -- 1: USER - 사용자 관리 모듈 (프로필 관리, 계정 설정, 회원 상태 관리)
    -- 2: COMPANY - 기업 관리 모듈 (기업 정보 관리, 기업 검수, 기업 계정 관리)
    -- 3: JOB_POSTING - 채용공고 모듈 (공고 등록/수정/삭제, 공고 검색, 공고 관리)
    -- 4: RESUME - 이력서 관리 모듈 (이력서 작성/수정/삭제, 이력서 공개 설정)
    -- 5: APPLICATION - 지원서 관리 모듈 (입사지원, 지원서 관리, 제안 관리)
    -- 6: PAYMENT - 결제 모듈 (결제 처리, 환불, 결제 내역 관리)
    -- 7: ADMIN - 관리자 모듈 (관리자 계정 관리, 시스템 설정, 통계 관리)
    module INT NOT NULL,
    
    -- 수행한 작업 내용 (예: "로그인 성공", "회원 상태 변경: 활성 → 정지")
    -- 최대 255자로 간결하게 기록하며, 민감한 개인정보는 포함하지 않음
    action VARCHAR(255) NOT NULL,
    
    -- 접속한 IP 주소 (보안 추적 및 악의적인 활동 감지에 활용)
    ipAddress VARCHAR(50) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 로그 생성일
    FOREIGN KEY (accountId) REFERENCES account(id) ON DELETE SET NULL,  -- 계정 삭제 시 NULL 처리됨
    FOREIGN KEY (adminId) REFERENCES admin(id) ON DELETE SET NULL  -- 관리자 삭제 시 NULL 처리됨
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
