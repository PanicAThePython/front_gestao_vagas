package br.com.nataliaweise.front_gestao_vagas.modules.candidate.controller;

import br.com.nataliaweise.front_gestao_vagas.modules.candidate.dto.CreateCandidateDTO;
import br.com.nataliaweise.front_gestao_vagas.modules.candidate.service.*;
import br.com.nataliaweise.front_gestao_vagas.utils.FormatErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

import java.util.UUID;

@Controller
@RequestMapping("/candidate")
public class CandidateController {
//    senha: teste12345

    @Autowired
    private ProfileCandidateService profileCandidateService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private FindJobService findJobService;

    @Autowired
    private ApplyJobService applyJobService;

    @Autowired
    private CreateCandidateService createCandidateService;

    @GetMapping("/login")
    public String login(){
        return "candidate/login";
    }

    public record Usuario(String username, String password){};
    @PostMapping("/signIn")
    public String signIn(RedirectAttributes redirectAttributes,
                         HttpSession session,
                         @ModelAttribute(value="usuario") Usuario usuario){
        try {
            if (!usuario.username.isEmpty() && !usuario.password.isEmpty()){
                var token = candidateService.login(usuario.username, usuario.password);
                var grants = token.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase())).toList();

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(null, null, grants);
                auth.setDetails(token.getAccess_token());

                SecurityContextHolder.getContext().setAuthentication(auth);
                SecurityContext securityContext = SecurityContextHolder.getContext();
                session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
                session.setAttribute("token", token);
                return "redirect:/candidate/profile";
            }
        } catch (HttpClientErrorException e) {
            redirectAttributes.addFlashAttribute("error_message", "Usuário ou senha inválidos");
            return "redirect:/candidate/login";
        }
        return "";
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CANDIDATE')")
    public String profile(Model model){
        try {
            var user = this.profileCandidateService.execute(getToken());

            model.addAttribute("user", user);
            return "candidate/profile";

        } catch(HttpClientErrorException e){
            return "redirect:/candidate/login";
        }
    }

    @GetMapping("/jobs")
    @PreAuthorize("hasRole('CANDIDATE')")
    public String jobs(Model model, String filter){
        try {
            if (filter != null){
                var jobs = findJobService.execute(getToken(), filter);
                model.addAttribute("jobs", jobs);
            }
        } catch (HttpClientErrorException e) {
            return "redirect:/candidate/login";
        }

        return "candidate/jobs";
    }

    @PostMapping("/jobs/apply")
    @PreAuthorize("hasRole('CANDIDATE')")
    public String applyJob(@RequestParam("jobId") UUID jobId){
        applyJobService.execute(getToken(), jobId);
        return "redirect:/candidate/jobs";
    }

    @GetMapping("/create")
    public String create(Model model){
        model.addAttribute("candidate", new CreateCandidateDTO());
        return "candidate/create";
    }

    @PostMapping("/create")
    public String save(CreateCandidateDTO candidate, Model model){
        try{
            createCandidateService.execute(candidate);

        } catch(HttpClientErrorException ex){
            model.addAttribute("error_message", FormatErrorMessage.formatErrorMessage(ex.getResponseBodyAsString()));
        }

        model.addAttribute("candidate", candidate);
        return "candidate/create";
    }

    private String getToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getDetails().toString();
    }
}
