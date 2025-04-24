package com.highfive.meetu.domain.job.common.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationQueryPersonalRepositoryImpl implements ApplicationQueryPersonalRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public int countApplicantsByJobPostingId(Long jobPostingId) {
        return em.createQuery("""
            SELECT COUNT(a) FROM application a
            WHERE a.jobPosting.id = :jobPostingId
        """, Long.class).setParameter("jobPostingId", jobPostingId).getSingleResult().intValue();
    }

    @Override
    public int countNewApplicants(Long jobPostingId) {
        return em.createQuery("""
            SELECT COUNT(a) FROM application a
            JOIN a.profile p
            WHERE a.jobPosting.id = :jobPostingId AND p.experienceLevel = 1
        """, Long.class).setParameter("jobPostingId", jobPostingId).getSingleResult().intValue();
    }

    @Override
    public int countExperiencedApplicants(Long jobPostingId) {
        return em.createQuery("""
            SELECT COUNT(a) FROM application a
            JOIN a.profile p
            WHERE a.jobPosting.id = :jobPostingId AND p.experienceLevel = 2
        """, Long.class).setParameter("jobPostingId", jobPostingId).getSingleResult().intValue();
    }

    @Override
    public int countApplicantsByEducationLevel(Long jobPostingId, int educationLevel) {
        return em.createQuery("""
            SELECT COUNT(a) FROM application a
            JOIN a.profile p
            WHERE a.jobPosting.id = :jobPostingId AND p.educationLevel = :educationLevel
        """, Long.class)
            .setParameter("jobPostingId", jobPostingId)
            .setParameter("educationLevel", educationLevel)
            .getSingleResult().intValue();
    }

    @Override
    public int countSalaryBelow4000(Long jobPostingId) {
        return em.createQuery("""
            SELECT COUNT(a) FROM application a
            JOIN a.profile p
            WHERE a.jobPosting.id = :jobPostingId
            AND p.desiredSalaryCode IN (9,10,11,12,13,14,15)
        """, Long.class).setParameter("jobPostingId", jobPostingId).getSingleResult().intValue();
    }

    @Override
    public int countSalaryRange4000to6000(Long jobPostingId) {
        return em.createQuery("""
            SELECT COUNT(a) FROM application a
            JOIN a.profile p
            WHERE a.jobPosting.id = :jobPostingId
            AND p.desiredSalaryCode IN (16,17)
        """, Long.class).setParameter("jobPostingId", jobPostingId).getSingleResult().intValue();
    }

    @Override
    public int countSalaryRange6000to8000(Long jobPostingId) {
        return em.createQuery("""
            SELECT COUNT(a) FROM application a
            JOIN a.profile p
            WHERE a.jobPosting.id = :jobPostingId
            AND p.desiredSalaryCode IN (18,19)
        """, Long.class).setParameter("jobPostingId", jobPostingId).getSingleResult().intValue();
    }

    @Override
    public int countSalaryAbove8000(Long jobPostingId) {
        return em.createQuery("""
            SELECT COUNT(a) FROM application a
            JOIN a.profile p
            WHERE a.jobPosting.id = :jobPostingId
            AND p.desiredSalaryCode >= 20
        """, Long.class).setParameter("jobPostingId", jobPostingId).getSingleResult().intValue();
    }

    @Override
    public int countNegotiableSalary(Long jobPostingId) {
        return em.createQuery("""
            SELECT COUNT(a) FROM application a
            JOIN a.profile p
            WHERE a.jobPosting.id = :jobPostingId
            AND (p.desiredSalaryCode = 0 OR p.desiredSalaryCode = 99)
        """, Long.class).setParameter("jobPostingId", jobPostingId).getSingleResult().intValue();
    }
}
