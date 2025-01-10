package com.projeto.projeto_final.spring.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index")
    public String home(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            String name = authentication.getName();
            if (name != null) {
                return "redirect:/home";
            }
        }

        return "index";
    }

    @GetMapping("/login")
    public String loginForm() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            String name = authentication.getName();
            if (name != null) {
                return "redirect:/home";
            }
        }

        return "login";
    }

    // handler method to handle user registration request
    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        LoginRegister user = new LoginRegister();

        model.addAttribute("user", user);

        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") LoginRegister user, BindingResult result, Model model) {
        User existing = userService.findByUsername(user.getUsername());

        if (existing != null) {
            result.rejectValue("username", null, "There is already an account registered with that username");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }

        userService.saveUser(user);

        return "redirect:/register?success";
    }
}