package com.bettherapy.bettherapy.model.response;

import lombok.Data;



@Data
public class ApiPlayer {
    private String name;
    private String firstname;
    private String lastname;
    private String birth_date;
    private String nationality;
    private String photo;
    private Integer height;
    private Integer weight;
    private Integer number;
    private String position;
}

