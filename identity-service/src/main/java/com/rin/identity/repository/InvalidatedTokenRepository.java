package com.rin.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rin.identity.entity.InvalidatedToken;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {}
