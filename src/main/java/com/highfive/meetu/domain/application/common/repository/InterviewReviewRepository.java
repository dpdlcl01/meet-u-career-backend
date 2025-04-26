package com.highfive.meetu.domain.application.common.repository;

import com.highfive.meetu.domain.application.common.entity.InterviewReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewReviewRepository extends JpaRepository<InterviewReview, Long> {
    @Query("""
        SELECT COUNT(ir) FROM interviewReview ir
        WHERE ir.company.id = :companyId
    """)
    int countByCompanyId(Long companyId);
}
