package br.com.nataliaweise.front_gestao_vagas.modules.candidate.dto;

import lombok.Data;

import java.util.List;

@Data
public class Token {

    private String access_token;
    private Long expires_in;
    private List<String> roles;
}
