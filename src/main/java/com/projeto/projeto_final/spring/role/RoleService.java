package com.projeto.projeto_final.spring.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<RoleDTO> findByUser(String username) {
        List<RoleDTO> roleDTOList = new ArrayList<>();
        List<Role> roleList = new ArrayList<>(roleRepository.findByUsername(username));

        for (Role role : roleList) {
            roleDTOList.add(convertEntityToDto(role));
        }

        return roleDTOList;
    }

    public RoleDTO findByName(String roleName) {
        return convertEntityToDto(roleRepository.findByName(roleName));
    }

    public RoleDTO convertEntityToDto(Role role) {
        RoleDTO roleDTO = new RoleDTO();

        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());

        return roleDTO;
    }
}
