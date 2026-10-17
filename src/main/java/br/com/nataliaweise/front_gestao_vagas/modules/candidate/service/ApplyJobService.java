package br.com.nataliaweise.front_gestao_vagas.modules.candidate.service;

import br.com.nataliaweise.front_gestao_vagas.modules.candidate.dto.Token;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${host.api.gestao.vagas}")
    private String hostAPIGestaoVagas;

    public String execute(String token, UUID idJob){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<UUID> request = new HttpEntity<>(idJob, headers);

        var url = hostAPIGestaoVagas.concat("/candidate/job/apply");
        var result = restTemplate.postForObject(url, request, String.class);

        return result;
    }
}
