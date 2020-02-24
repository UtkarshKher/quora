package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/")
public class QuestionController {


    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @PostMapping(path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(QuestionRequest questionRequest,
                                                             @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = commonBusinessService.getUserAuthenticationEntity(authorization).getUser();

        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUser(userEntity);
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());

        QuestionEntity questionEntity1 = questionBusinessService.createQuestion(questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(questionEntity1.getUuid()).status("Question Created");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @GetMapping(path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestion(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        commonBusinessService.getUserAuthenticationEntity(authorization);

        List<QuestionEntity> questionEntity = questionBusinessService.getAllQuestions();
        List<QuestionDetailsResponse> response = new ArrayList<>();
        for(QuestionEntity question : questionEntity){
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(question.getUuid()).content(question.getContent());
            response.add(questionResponse);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestion(@PathVariable("userId") final String uuid,
                                                                     @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = commonBusinessService.getUser(uuid, authorization);

        List<QuestionEntity> questionEntity = questionBusinessService.getAllQuestions(userEntity);
        List<QuestionDetailsResponse> response = new ArrayList<>();
        for(QuestionEntity question : questionEntity){
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(question.getUuid()).content(question.getContent());
            response.add(questionResponse);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(@PathVariable("questionId") final String quesId,
                                                                      QuestionEditRequest questionEditRequest,
                                                                     @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthenticationEntity userAuthenticationEntity = commonBusinessService.getUserAuthenticationEntity(authorization);

        QuestionEntity questionEntity = questionBusinessService.findByQuestionId(quesId);
        if (Objects.isNull(questionEntity)) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if (!userAuthenticationEntity.getUser().getUuid().equalsIgnoreCase(questionEntity.getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        questionEntity.setContent(questionEditRequest.getContent());

        questionBusinessService.update(questionEntity);

        QuestionEditResponse response = new QuestionEditResponse().id(questionEntity.getUuid()).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(response, HttpStatus.OK);
    }

    @DeleteMapping(path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String quesId,
                                                            @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserEntity userEntity = commonBusinessService.getUserAuthenticationEntity(authorization).getUser();

        QuestionEntity questionEntity = questionBusinessService.findByQuestionId(quesId);
        if (Objects.isNull(questionEntity)) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        questionBusinessService.deleteQuestion(userEntity, questionEntity );

        QuestionDeleteResponse response = new QuestionDeleteResponse().id(questionEntity.getUuid()).status("QUESTION DELETED");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
