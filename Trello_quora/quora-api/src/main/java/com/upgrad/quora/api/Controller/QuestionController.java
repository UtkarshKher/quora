package com.upgrad.quora.api.Controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.*;

@RestController
@RequestMapping("/")
public class QuestionController {

    @RequestMapping(path = "/question/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(QuestionRequest questionRequest,
                                                             @RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedAuth = new String(decoded);
        String[] decodedArray = decodedAuth.split(":");
        UserAuthenticationEntity userAuthenticationEntity = UserBusinessService.authenticateUser(decodedArray[0], decodedArray[1]);

        if(Objects.isNull(userAuthenticationEntity)){
            throw new AuthenticationFailedException("ATHR-001", "User has not signed in");
        }else if(Objects.nonNull(userAuthenticationEntity) && userAuthenticationEntity.getLogoutAt() != null){
            throw new AuthenticationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
            QuestionEntity questionEntity = new QuestionEntity();
            UserEntity userEntity = userAuthenticationEntity.getUser();
            questionEntity.setUser(userEntity);
            questionEntity.setContent(questionRequest.getContent());
            questionEntity.setDate(ZonedDateTime.now());

            QuestionEntity questionEntity1 = QuestionBusinessService.createQuestion(questionEntity);

            QuestionResponse questionResponse = new QuestionResponse().id(questionEntity1.getUuid()).status("Question Created");


        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/question/all", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestion(QuestionRequest questionRequest,
                                                           @RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedAuth = new String(decoded);
        String[] decodedArray = decodedAuth.split(":");
        UserAuthenticationEntity userAuthenticationEntity = UserBusinessService.authenticateUser(decodedArray[0], decodedArray[1]);

        if(Objects.isNull(userAuthenticationEntity)){
            throw new AuthenticationFailedException("ATHR-001", "User has not signed in");
        }else if(Objects.nonNull(userAuthenticationEntity) && userAuthenticationEntity.getLogoutAt() != null){
            throw new AuthenticationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        UserEntity userEntity = userAuthenticationEntity.getUser();
        List<QuestionEntity> questionEntity = QuestionBusinessService.getAllQuestioons(userEntity);
        List<QuestionDetailsResponse> response = new ArrayList<>();
        for(QuestionEntity question : questionEntity){
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(question.getUuid()).content(question.getContent());
            response.add(questionResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(response, HttpStatus.OK);
    }



}
