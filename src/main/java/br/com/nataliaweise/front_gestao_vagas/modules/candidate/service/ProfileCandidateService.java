package br.com.nataliaweise.front_gestao_vagas.modules.candidate.service;

import br.com.nataliaweise.front_gestao_vagas.modules.candidate.dto.ProfileUserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ProfileCandidateService {

    @Value("${host.api.gestao.vagas}")
    private String hostAPIGestaoVagas;

    public ProfileUserDTO execute(String token){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);

        try {
            var url = hostAPIGestaoVagas.concat("/candidate/");
            var result = restTemplate.exchange(url, HttpMethod.GET, request, ProfileUserDTO.class);
            return result.getBody();
        } catch (Unauthorized e) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }
}
