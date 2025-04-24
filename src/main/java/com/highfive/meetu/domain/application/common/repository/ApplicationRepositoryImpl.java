package com.highfive.meetu.domain.application.common.repository;

import com.highfive.meetu.domain.application.business.dto.ApplicationDetailDTO;
import com.highfive.meetu.domain.application.common.entity.QApplication;
import com.highfive.meetu.domain.job.common.entity.QLocation;
import com.highfive.meetu.domain.user.common.entity.QAccount;
import com.highfive.meetu.domain.user.common.entity.QProfile;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<ApplicationDetailDTO> findApplicationDetails() {
    QApplication application = QApplication.application;
    QProfile profile = QProfile.profile;
    QAccount account = QAccount.account;
    QLocation location = QLocation.location;

    return queryFactory
        .select(Projections.constructor(ApplicationDetailDTO.class,
            application.id,
            account.name,
            account.email,
            location.fullLocation
        ))
        .from(application)
        .join(application.profile, profile)
        .join(profile.account, account)
        .join(profile.location, location)
        .fetch();
  }
}
