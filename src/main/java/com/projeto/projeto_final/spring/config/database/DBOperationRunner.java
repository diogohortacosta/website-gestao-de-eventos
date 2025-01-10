package com.projeto.projeto_final.spring.config.database;

import com.projeto.projeto_final.spring.activity.Activity;
import com.projeto.projeto_final.spring.activity.ActivityRepository;
import com.projeto.projeto_final.spring.board.BoardRepository;
import com.projeto.projeto_final.spring.event.Event;
import com.projeto.projeto_final.spring.event.EventRepository;
import com.projeto.projeto_final.spring.role.Role;
import com.projeto.projeto_final.spring.role.RoleRepository;
import com.projeto.projeto_final.spring.task.Task;
import com.projeto.projeto_final.spring.task.TaskRepository;
import com.projeto.projeto_final.spring.user.User;
import com.projeto.projeto_final.spring.user.UserRepository;
import com.projeto.projeto_final.spring.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class DBOperationRunner implements CommandLineRunner {

    @Autowired
    private RoleRepository rRepo;

    @Autowired
    private UserRepository uRepo;

    @Autowired
    private EventRepository eRepo;

    @Autowired
    private TaskRepository tRepo;

    @Autowired
    private BoardRepository bRepo;

    @Autowired
    private ActivityRepository aRepo;

    @Autowired
    private UserService uServ;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Variável para controle de inicialização
    @Autowired
    private InitializationStatusRepository initStatusRepo;

    @Override
    public void run(String... args) throws Exception {
        // Quando o programa corre muda todos os users para offline
        uServ.setAllUsersOffline();

        // Verificar o status de inicialização no banco de dados
        Optional<InitializationStatus> initializationStatus = initStatusRepo.findById(1); // Assumindo que o ID 1 seja utilizado para armazenar o estado único

        if (initializationStatus.isPresent() && initializationStatus.get().isInitialized()) {
            System.out.println("DBOperationRunner já foi executado. Ignorando a execução novamente.");
            return;
        }

        // Se não estiver inicializado, realizar as operações de inicialização
        rRepo.saveAll(Arrays.asList(
                new Role(1, "ROLE_ADMIN"),
                new Role(2, "ROLE_USER")
        ));

        Set<Role> rolesAdmin = new HashSet<>();
        rolesAdmin.add(rRepo.findByName("ROLE_ADMIN"));

        Set<Role> rolesUser = new HashSet<>();
        rolesUser.add(rRepo.findByName("ROLE_USER"));

        uRepo.saveAll(Arrays.asList(
                new User(1, "Administrador", "admin", passwordEncoder.encode("admin"), rolesAdmin),
                new User(2, "Diogo Horta e Costa", "diogo", passwordEncoder.encode("123"), rolesUser),
                new User(3, "Daniel Gomes", "daniel", passwordEncoder.encode("123"), rolesUser),
                new User(4, "Rita Santos", "rita", passwordEncoder.encode("123"), rolesUser)
        ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        String dateStringStart1 = "2024-10-18T00:00";
        String dateStringEnd1 = "2024-10-20T00:00";
        LocalDateTime dateTimeStart1 = LocalDateTime.parse(dateStringStart1, formatter);
        LocalDateTime dateTimeEnd1 = LocalDateTime.parse(dateStringEnd1, formatter);

        String dateStringStart2 = "2024-10-18T10:00";
        String dateStringEnd2 = "2024-10-19T10:00";
        LocalDateTime dateTimeStart2 = LocalDateTime.parse(dateStringStart2, formatter);
        LocalDateTime dateTimeEnd2 = LocalDateTime.parse(dateStringEnd2, formatter);

        eRepo.saveAll(Arrays.asList(
                new Event(1, "Evento 1", "Descrição Evento 1", false, dateTimeStart2, dateTimeEnd1, "#0088ff", "#ffffff", uRepo.findByUsername("admin")),
                new Event(2, "Evento 2", "Descrição Evento 2", false, dateTimeStart2, dateTimeEnd2, "#0088ff", "#ffffff", uRepo.findByUsername("diogo")),
                new Event(3, "Evento 3", "Descrição Evento 3", true, dateTimeStart1, dateTimeEnd2, "#0088ff", "#ffffff", uRepo.findByUsername("admin"))
        ));

        tRepo.saveAll(Arrays.asList(
                new Task(1, "Entrega", dateTimeStart2, dateTimeEnd2, eRepo.findById(1), 1),
                new Task(2, "Saída", dateTimeStart2, dateTimeEnd2, eRepo.findById(1), 2),
                new Task(3, "Comer", dateTimeStart2, dateTimeEnd2, eRepo.findById(2), 1)
        ));

        aRepo.saveAll(Arrays.asList(
                new Activity(1, "Encomendar Pratos", "Tenho que encomendar pratos", bRepo.findByEventIdName(1, "To Do")),
                new Activity(2, "Telefonar Organizadora", "Tenho que telefonar a organizadora", bRepo.findByEventIdName(1, "To Do")),
                new Activity(3, "Telefonar Organizadora", "Tenho que telefonar a organizadora", bRepo.findByEventIdName(1, "Doing")),
                new Activity(4, "Comprar Panfeletos", "Tenho que comprar panfeletos", bRepo.findByEventIdName(2, "Review"))
        ));

        // Atualizar o status de inicialização no banco de dados
        InitializationStatus status = initializationStatus.orElse(new InitializationStatus());
        status.setInitialized(true);
        initStatusRepo.save(status);

        System.out.println("----------------------All Data saved into Database----------------------");
    }
}
