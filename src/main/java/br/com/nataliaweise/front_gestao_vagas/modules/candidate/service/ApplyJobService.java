package br.com.nataliaweise.front_gestao_vagas.modules.candidate.service;

import br.com.nataliaweise.front_gestao_vagas.modules.candidate.dto.Token;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ApplyJobService {

    public String execute(String token, UUID idJob){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UUID> request = new HttpEntity<>(idJob, headers);

        var result = restTemplate.postForObject("http://localhost:8080/candidate/job/apply",
                request, String.class);

        return result;
    }
}
