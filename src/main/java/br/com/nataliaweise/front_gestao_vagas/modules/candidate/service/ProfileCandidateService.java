package br.com.nataliaweise.front_gestao_vagas.modules.candidate.service;

import br.com.nataliaweise.front_gestao_vagas.modules.candidate.dto.ProfileUserDTO;
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

    public ProfileUserDTO execute(String token){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(headers);

        try {
            var result = restTemplate.exchange("http://localhost:8080/candidate/",
                    HttpMethod.GET, request, ProfileUserDTO.class);
            System.out.println(result);
            return result.getBody();
        } catch (Unauthorized e) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }
}
