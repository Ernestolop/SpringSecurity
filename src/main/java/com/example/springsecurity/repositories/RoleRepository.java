package com.example.springsecurity.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springsecurity.models.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long>{
    
    List<RoleEntity> findRoleEntitiesByRoleEnumIn(List<String> roleList);

}
