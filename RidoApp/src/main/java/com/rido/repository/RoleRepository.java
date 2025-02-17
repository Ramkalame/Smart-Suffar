package com.rido.repository;

//package com.bezkoder.springjwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.Role;
import com.rido.entity.enums.ERole;

//import com.bezkoder.springjwt.models.ERole;
//import com.bezkoder.springjwt.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
	 Optional<Role> findByName(ERole name);
}

