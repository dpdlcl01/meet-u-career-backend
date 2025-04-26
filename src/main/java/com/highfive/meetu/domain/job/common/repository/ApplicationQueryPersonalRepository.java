package com.highfive.meetu.domain.job.common.repository;

/**
 * 개인회원용 채용공고 지원자 통계 조회 Repository
 *
 * - 채용공고별 지원자 수, 신입/경력자 수, 학력별 지원자 수, 희망 연봉별 지원자 수 등을 조회
 * - EntityManager를 사용하는 Custom Repository 구현체(ApplicationQueryPersonalRepositoryImpl)에서 실제 로직 처리
 */
public interface ApplicationQueryPersonalRepository {

    /**
     * 특정 채용공고에 지원한 전체 지원자 수 조회
     *
     * @param jobPostingId 채용공고 ID
     * @return 지원자 수
     */
    int countApplicantsByJobPostingId(Long jobPostingId);

    /**
     * 특정 채용공고에 신입 지원자 수 조회
     *
     * @param jobPostingId 채용공고 ID
     * @return 신입 지원자 수
     */
    int countNewApplicants(Long jobPostingId);

    /**
     * 특정 채용공고에 경력 지원자 수 조회
     *
     * @param jobPostingId 채용공고 ID
     * @return 경력 지원자 수
     */
    int countExperiencedApplicants(Long jobPostingId);

    /**
     * 특정 채용공고에 학력별 지원자 수 조회
     *
     * @param jobPostingId 채용공고 ID
     * @param educationLevel 학력 코드 (예: 1=고졸, 2=전문대졸, 3=대졸, 4=석사, 5=박사)
     * @return 해당 학력 수준 지원자 수
     */
    int countApplicantsByEducationLevel(Long jobPostingId, int educationLevel);

    /**
     * 특정 채용공고에 희망 연봉 4000만원 미만 지원자 수 조회
     *
     * @param jobPostingId 채용공고 ID
     * @return 4000만원 미만 지원자 수
     */
    int countSalaryBelow4000(Long jobPostingId);

    /**
     * 특정 채용공고에 희망 연봉 4000~6000만원 지원자 수 조회
     *
     * @param jobPostingId 채용공고 ID
     * @return 4000~6000만원 지원자 수
     */
    int countSalaryRange4000to6000(Long jobPostingId);

    /**
     * 특정 채용공고에 희망 연봉 6000~8000만원 지원자 수 조회
     *
     * @param jobPostingId 채용공고 ID
     * @return 6000~8000만원 지원자 수
     */
    int countSalaryRange6000to8000(Long jobPostingId);

    /**
     * 특정 채용공고에 희망 연봉 8000만원 이상 지원자 수 조회
     *
     * @param jobPostingId 채용공고 ID
     * @return 8000만원 이상 지원자 수
     */
    int countSalaryAbove8000(Long jobPostingId);

    /**
     * 특정 채용공고에 희망 연봉 '회사 내규/면접 후 결정' 지원자 수 조회
     *
     * @param jobPostingId 채용공고 ID
     * @return 희망 연봉 미지정(내규/면접결정) 지원자 수
     */
    int countNegotiableSalary(Long jobPostingId);
}
