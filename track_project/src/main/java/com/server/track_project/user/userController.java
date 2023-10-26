package com.server.track_project.user;

import com.server.track_project.user.Object.userVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/*")
public class userController {

    private final userService userService;

    @GetMapping("/signUp")
    public String signUp() {
        return "signup_form";
    }

    @PostMapping("/signUp")
    public String signUp(@Valid userVO uservo) {
        userService.signUp(uservo);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }


    @GetMapping("/user_access")
    public String userAccess(Model model, Authentication authentication) {
        userVO user = (userVO)authentication.getPrincipal();
        model.addAttribute("info", user.getUserId()+"님");
        return "user_access";
    }

}

