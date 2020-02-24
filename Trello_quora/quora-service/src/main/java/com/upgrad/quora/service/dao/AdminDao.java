package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@SuppressWarnings("JpaQueryApiInspection")
@Repository
public class AdminDao {

    @PersistenceContext
    private EntityManager entityManager;


    public UserEntity deleteUser(final String uuid) {

        try {
            UserEntity user = entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
            entityManager.remove(user);
            return user;
        } catch (NoResultException nre) {
            return null;
        }

    }

    public UserAuthenticationEntity getAuthToken(final String authorizationToken) {

        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthenticationEntity.class).setParameter("accessToken", authorizationToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}