package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;


@Repository
public class UserAuthenticationDao {

    @PersistenceContext
    private EntityManager entityManager;


    public UserAuthenticationEntity getAuthToken(final String authorizationToken) {

        try {
            UserAuthenticationEntity userAuthenticationEntity = entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthenticationEntity.class).setParameter("accessToken", authorizationToken).getSingleResult();
            return userAuthenticationEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }


    public UserAuthenticationEntity createAuthToken(UserAuthenticationEntity userAuthenticationEntity) {
        entityManager.persist(userAuthenticationEntity);
        return userAuthenticationEntity;
    }


    public UserAuthenticationEntity logOut(UserAuthenticationEntity userAuthenticationEntity) {
        entityManager.merge(userAuthenticationEntity);
        return userAuthenticationEntity;
    }
}