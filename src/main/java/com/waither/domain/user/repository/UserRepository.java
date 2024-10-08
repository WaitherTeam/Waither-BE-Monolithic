package com.waither.domain.user.repository;

import com.waither.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String Email);

    Optional<User> findById(Long Id);

    boolean existsByEmail(String Email);

    List<User> findAllByEmailIn(List<String> emails);
}