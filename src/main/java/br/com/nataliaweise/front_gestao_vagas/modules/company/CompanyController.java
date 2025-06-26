package br.com.nataliaweise.front_gestao_vagas.modules.company;

import br.com.nataliaweise.front_gestao_vagas.modules.company.dto.CreateCompanyDTO;
import br.com.nataliaweise.front_gestao_vagas.modules.company.service.CreateCompanyService;
import br.com.nataliaweise.front_gestao_vagas.modules.company.service.LoginCompanyService;
import br.com.nataliaweise.front_gestao_vagas.utils.FormatErrorMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private LoginCompanyService loginCompanyService;

    @Autowired
    private CreateCompanyService createCompanyService;

    @GetMapping("/create")
    public String create(Model model){
        model.addAttribute("company", new CreateCompanyDTO());
        return "company/create";
    }

    @PostMapping("/create")
    public String save(Model model, CreateCompanyDTO company){
        System.out.println(company);
        try{
            createCompanyService.execute(company);
            model.addAttribute("company", new CreateCompanyDTO());
        } catch (HttpClientErrorException ex) {
            model.addAttribute("error_message", FormatErrorMessage.formatErrorMessage(ex.getResponseBodyAsString()));
            model.addAttribute("company", company);
        }

        return "company/login";
    }

    @GetMapping("/login")
    public String login(){
        return "company/login";
    }

    @PostMapping("/signIn")
    public String signIn(RedirectAttributes redirectAttributes, HttpSession session, String username, String password) {
        try {
            var token = loginCompanyService.execute(username, password);
            var grants = token.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase())).toList();

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(null, null, grants);
            auth.setDetails(token.getAccess_token());

            SecurityContextHolder.getContext().setAuthentication(auth);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
            session.setAttribute("token", token);

            return "redirect:/company/jobs";

        } catch (HttpClientErrorException e) {
            redirectAttributes.addFlashAttribute("error_message", "Usuário/Senha incorretos");
            return "redirect:/company/jobs";
        }
    }

    @GetMapping("/jobs")
    @PreAuthorize("hasRole('COMPANY')")
    public String jobs(){
        return "company/jobs";
    }
}
