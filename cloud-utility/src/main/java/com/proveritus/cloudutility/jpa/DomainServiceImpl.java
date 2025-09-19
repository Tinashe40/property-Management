package com.proveritus.cloudutility.jpa;

import com.proveritus.cloudutility.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public abstract class DomainServiceImpl <T, C, U> implements DomainService<T, C, U> {

    private final BaseDao<T, Long>  baseDao;

    protected DomainServiceImpl(BaseDao<T, Long> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public T findById(Long id) {
        return baseDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEntityClass().getSimpleName().concat(" record not found with id: " + id)));
    }

    public void deleteById(Long id) {
        baseDao.deleteById(id);
    }

    @Override
    public Collection<T> findAll() {
        return baseDao.findAll();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return baseDao.findAll(pageable);
    }

    public abstract Class<T> getEntityClass();
}

