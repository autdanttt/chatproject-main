package com.forcy.chatapp.user;

import com.forcy.chatapp.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface
RoleRepository extends CrudRepository<Role, Long> {
    Optional<Object> findByName(String name);
}
