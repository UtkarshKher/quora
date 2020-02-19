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

    public List<QuestionEntity> getAllQuestion() {
        try {
            List<QuestionEntity> questionEntities = entityManager.createNamedQuery("Allquestion", QuestionEntity.class).getResultList();
            return questionEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity getQuestion(String uuid) {
        try {
            QuestionEntity questionEntity = entityManager.createNamedQuery("questionById", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
            return questionEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }


    public void update(QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
    }

    public void deleteQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }
}
