package com.bettherapy.bettherapy.service;

import com.bettherapy.bettherapy.model.entity.Country;
import com.bettherapy.bettherapy.model.repository.CountryRepository;
import com.bettherapy.bettherapy.model.request.CountryRequest;
import com.bettherapy.bettherapy.model.response.CountryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CountryService {

    @Autowired
    CountryRepository countryRepository;

    public CountryResponse save(CountryRequest request) {
        Country country = new Country();
        country.setContinent(request.getContinent());
        country.setIso(request.getIso());
        country.setNationality(request.getNationality());
        country.setPhoneCode(request.getPhoneCode());
        country.setName(request.getName());
        country.setUrl(request.getUrl());

        Country saved = countryRepository.save(country);
        return toResponse(saved);
    }

    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    public CountryResponse toResponse(Country country){
        CountryResponse response = new CountryResponse();
        response.setId(country.getId());
        response.setName(country.getName());
        response.setIso(country.getIso());
        response.setContinent(country.getContinent());
        response.setNationality(country.getNationality());
        response.setPhoneCode(country.getPhoneCode());
        response.setUrl(country.getUrl());
        return response;
    }
}
