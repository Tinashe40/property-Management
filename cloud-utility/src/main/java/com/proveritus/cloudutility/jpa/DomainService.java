package com.proveritus.cloudutility.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface DomainService <T, C, U>{
    T create(C createCommand);

    T update(U updateCommand);

    T findById(Long id);

    Collection<T> findAll();

    Page<T> findAll(Pageable pageable);

}
