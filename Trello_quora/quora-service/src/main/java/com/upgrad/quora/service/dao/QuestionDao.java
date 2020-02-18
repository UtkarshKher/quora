package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;


    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestion(UserEntity userEntity) {
        try {
            List<QuestionEntity> questionEntities = entityManager.createNamedQuery("questionByUserId", QuestionEntity.class).setParameter("user_id", userEntity.getUuid()).getResultList();
            return questionEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }



}
