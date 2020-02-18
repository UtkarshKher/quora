package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {

    private static QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public static QuestionEntity createQuestion(QuestionEntity questionEntity){
        QuestionEntity questionEntity1 = questionDao.createQuestion(questionEntity);
        return questionEntity1;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public static List<QuestionEntity> getAllQuestioons(UserEntity userEntity){
        List<QuestionEntity> questionEntity = questionDao.getAllQuestion(userEntity);
        return questionEntity;
    }



}
