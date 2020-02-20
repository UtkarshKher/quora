package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswerById(String uuid) {
        try {
            return entityManager.createNamedQuery("answerById", AnswerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity update(AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }

    public void deleteQuestion(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }

    public List<AnswerEntity> getAllAnswers(String questionId) {
        try {
            return entityManager.createNamedQuery("answersByQuestionId", AnswerEntity.class).setParameter("question_id", questionId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
