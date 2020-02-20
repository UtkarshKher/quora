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

    @RequestMapping(path = "/question/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(QuestionRequest questionRequest,
                                                           @PathVariable("userId") final String uuid,
                                                             @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = commonBusinessService.getUser(uuid, authorization);

        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUser(userEntity);
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());

        QuestionEntity questionEntity1 = questionBusinessService.createQuestion(questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(questionEntity1.getUuid()).status("Question Created");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/question/all", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestion(@PathVariable("userId") final String uuid,
                                                                     @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        commonBusinessService.getUser(uuid, authorization);

        List<QuestionEntity> questionEntity = questionBusinessService.getAllQuestions();
        List<QuestionDetailsResponse> response = new ArrayList<>();
        for(QuestionEntity question : questionEntity){
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(question.getUuid()).content(question.getContent());
            response.add(questionResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/question/all/{userId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestion(QuestionRequest questionRequest,
                                                                     @PathVariable("userId") final String uuid,
                                                                     @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = commonBusinessService.getUser(uuid, authorization);

        List<QuestionEntity> questionEntity = questionBusinessService.getAllQuestions(userEntity);
        List<QuestionDetailsResponse> response = new ArrayList<>();
        for(QuestionEntity question : questionEntity){
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(question.getUuid()).content(question.getContent());
            response.add(questionResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/question/edit/{questionId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> getQuestion(@PathVariable("questionId") final String quesId,
                                                                     @RequestBody QuestionEditRequest questionEditRequest,
                                                                     @PathVariable("userId") final String uuid,
                                                                     @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {

        commonBusinessService.getUser(uuid, authorization);

        QuestionEntity questionEntity = questionBusinessService.findByQuestionId(quesId);
        if (Objects.isNull(questionEntity)) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        questionEntity.setContent(questionEditRequest.getContent());
        questionBusinessService.update(questionEntity);

        QuestionEditResponse response = new QuestionEditResponse().id(questionEntity.getUuid()).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/question/delete/{questionId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String quesId,
                                                            @PathVariable("userId") final String uuid,
                                                            @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException, InvalidQuestionException {

        UserEntity userEntity = commonBusinessService.getUser(uuid, authorization);

        QuestionEntity questionEntity = questionBusinessService.findByQuestionId(quesId);
        if (Objects.isNull(questionEntity)) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        questionBusinessService.deleteQuestion(userEntity, questionEntity );

        QuestionDeleteResponse response = new QuestionDeleteResponse().id(questionEntity.getUuid()).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(response, HttpStatus.OK);
    }


}
