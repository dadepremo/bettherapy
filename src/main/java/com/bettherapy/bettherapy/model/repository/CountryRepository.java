package com.bettherapy.bettherapy.model.repository;

import com.bettherapy.bettherapy.model.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByNameIgnoreCase(String name);
}
