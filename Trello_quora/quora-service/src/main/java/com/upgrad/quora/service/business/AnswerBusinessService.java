package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class AnswerBusinessService {
    private AnswerDao answerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        return answerDao.createAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity findAnswerById(String answerId) throws AnswerNotFoundException {
        AnswerEntity answerEntity = answerDao.getAnswerById(answerId);
        if(Objects.isNull(answerEntity)){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }
        return answerEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer(AnswerEntity answerEntity) {
        return answerDao.update(answerEntity);
    }

    public void deleteAnswer(AnswerEntity answerEntity) {
        answerDao.deleteQuestion(answerEntity);
    }

    public List<AnswerEntity> getAllAnswers(String questionId) {return answerDao.getAllAnswers(questionId);}
}
