package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        QuestionEntity questionEntity1 = questionDao.createQuestion(questionEntity);
        return questionEntity1;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(UserEntity userEntity){
        List<QuestionEntity> questionEntity = questionDao.getAllQuestion(userEntity);
        return questionEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(){
        List<QuestionEntity> questionEntity = questionDao.getAllQuestion();
        return questionEntity;
    }

    public QuestionEntity findByQuestionId(String uuid) {
        return questionDao.getQuestion(uuid);
    }

    public void update(QuestionEntity questionEntity) {
        questionDao.update(questionEntity);
    }

    public void deleteQuestion(UserEntity userEntity, QuestionEntity questionEntity) throws AuthorizationFailedException {
        if(!questionEntity.getUser().equals(userEntity) || !userEntity.getRole().equals("admin")){
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question'");
        }
        questionDao.deleteQuestion(questionEntity);
    }
}
