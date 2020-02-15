package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {


    @PersistenceContext
    private EntityManager entityManager;



    public UserEntity getUser(final String uuid) {
        try {
            UserEntity user = entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
            return user;
        } catch (NoResultException nre) {
            return null;
        }
    }


    public UserEntity getUserByUsername(final String username) {
        try {
            UserEntity user = entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username", username).getSingleResult();
            return user;
        } catch (NoResultException nre) {
            return null;
        }
    }


    public UserEntity getUserByEmail(final String email) {
        try {
            UserEntity user = entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
            return user;
        } catch (NoResultException nre) {
            return null;
        }
    }


    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }


}