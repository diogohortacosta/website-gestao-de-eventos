package com.projeto.projeto_final.spring.user;

import com.projeto.projeto_final.spring.event.EventDTO;
import com.projeto.projeto_final.spring.event.EventService;
import com.projeto.projeto_final.spring.role.RoleDTO;
import com.projeto.projeto_final.spring.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EventService eventService;

    @GetMapping("/")
    public String home(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            String name = authentication.getName();
            if (name != null) {
                return "redirect:/home";
            }
        }

        return "redirect:/login";
    }

    @GetMapping("/home")
    public String userHome(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserDTO user = userService.convertEntityToDto(userService.findByUsername(username));
        List<RoleDTO> roleList = roleService.findByUser(username);
        List<EventDTO> eventList = eventService.findActiveEventsUsername(username);
        boolean isAdmin = false;

        for (RoleDTO role : roleList) {
            if ("ROLE_ADMIN".equals(role.getName())) {
                isAdmin = true;
                break;
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("roleList", roleList);
        model.addAttribute("eventList", eventList);
        model.addAttribute("isAdmin", isAdmin);

        return "home";
    }

    @MessageMapping("/user.connectUser")
    @SendTo("/topic/public")
    public UserDTO connectUser(@Payload UserDTO user) {
        userService.connect(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/topic/public")
    public UserDTO disconnectUser(@Payload UserDTO user) {
        userService.disconnect(user);
        return user;
    }
}
