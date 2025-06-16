package br.com.nataliaweise.front_gestao_vagas.modules.candidate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDTO {

    private UUID id;
    private String description;
    private String name;
    private String benefits;
    private String level;
    private UUID companyId;
    private Date createdAt;

}
