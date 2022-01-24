package pl.cieslas.budgetmanager.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.cieslas.budgetmanager.security.CurrentUser;
import pl.cieslas.budgetmanager.entity.User;
import pl.cieslas.budgetmanager.security.UserService;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/create-user")
    @ResponseBody
    public String createUser() {
        User user = new User();
        user.setUsername("ciesls");
        user.setPassword("ciesls");
        user.setEmail("ss@ss.pl");
        userService.saveUser(user);
        return "admin";
    }

//    @GetMapping("/admin")
//    @ResponseBody
//    public String userInfo(@AuthenticationPrincipal UserDetails customUser) {
////        log.info("customUser class {} " , customUser.getClass());
//        return "You are logged as " + customUser;
//    }

    @GetMapping("/admin")
    @ResponseBody
    public String admin(@AuthenticationPrincipal CurrentUser customUser) {
        User entityUser = customUser.getUser();
        return "Hello " + entityUser.getUsername();
    }



}
