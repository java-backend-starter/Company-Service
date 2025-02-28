package com.company.www.repository.belong;

import com.company.www.entity.belong.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role save(Role role);

    void delete(Role role);

    Role findByRoleName(String roleName);
    Role findByRoleId(Long id);
    List<Role> findAll();

}
