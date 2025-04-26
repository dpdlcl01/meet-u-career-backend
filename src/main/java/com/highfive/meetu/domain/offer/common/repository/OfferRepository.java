package com.highfive.meetu.domain.offer.common.repository;

import com.highfive.meetu.domain.offer.common.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

  int countByPersonalAccountId(Long personalAccountId);

}
