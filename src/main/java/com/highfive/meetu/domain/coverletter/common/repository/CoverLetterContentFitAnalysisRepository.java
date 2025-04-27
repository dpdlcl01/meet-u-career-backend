package com.highfive.meetu.domain.coverletter.common.repository;

import com.highfive.meetu.domain.coverletter.common.entity.CoverLetterContentFitAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverLetterContentFitAnalysisRepository extends JpaRepository<CoverLetterContentFitAnalysis, Long> {


}
