package com.bettherapy.bettherapy.model.repository;

import com.bettherapy.bettherapy.model.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SportRepository extends JpaRepository<Sport, Long> {
}
