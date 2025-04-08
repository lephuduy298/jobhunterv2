package com.lephuduy.jobhunter.repository;

import com.lephuduy.jobhunter.domain.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {
    Subscriber findByEmail(String email);

    boolean existsByEmail(String email);
}
