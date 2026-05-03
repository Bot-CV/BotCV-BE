package org.toanehihi.botcv.application.role.service;

import org.toanehihi.botcv.domain.model.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(String name);
}
