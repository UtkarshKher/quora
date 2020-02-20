package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @Autowired
    private AnswerBusinessService answerBusinessService;

    @PostMapping(path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createQuestion(AnswerRequest answerRequest, @PathVariable String questionId, @RequestHeader String authorization) throws InvalidQuestionException, AuthorizationFailedException {

        //check question
        QuestionEntity questionEntity = questionBusinessService.findByQuestionId(questionId);
        if (Objects.isNull(questionEntity)) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        //check access token and if user is signed out
        UserAuthenticationEntity userAuthenticationEntity = commonBusinessService.getUserAuthenticationEntity(authorization);

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setUserId(userAuthenticationEntity.getUser().getUuid());
        answerEntity.setQuestionId(questionId);
        answerEntity.setContent(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());

        //create answer
        AnswerEntity createdAnswerEntity = answerBusinessService.createAnswer(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");

        return new ResponseEntity<>(answerResponse, HttpStatus.CREATED);
    }

    @PutMapping(path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> editAnswerContent(AnswerEditRequest answerEditRequest, @PathVariable String answerId, @RequestHeader String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        //check access token and if user is signed out
        UserAuthenticationEntity userAuthenticationEntity = commonBusinessService.getUserAuthenticationEntity(authorization);

        //check if answer exists
        AnswerEntity answerEntity = answerBusinessService.findAnswerById(answerId);

        //check if user is owner of the answer
        if (!userAuthenticationEntity.getUser().getUuid().equalsIgnoreCase(answerEntity.getUserId())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        //edit answer
        AnswerEntity updatedAnswerEntity = answerBusinessService.updateAnswer(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse().id(updatedAnswerEntity.getUuid()).status("ANSWER EDITED");


        return new ResponseEntity<>(answerResponse, HttpStatus.OK);
    }

    @DeleteMapping(path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> deleteAnswer(@PathVariable String answerId, @RequestHeader String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        //check access token and if user is signed out
        UserAuthenticationEntity userAuthenticationEntity = commonBusinessService.getUserAuthenticationEntity(authorization);

        //check if answer exists
        AnswerEntity answerEntity = answerBusinessService.findAnswerById(answerId);

        //check if user is owner of the answer or admin
        if (!userAuthenticationEntity.getUser().getUuid().equalsIgnoreCase(answerEntity.getUserId()) || (userAuthenticationEntity.getUser().getRole().equalsIgnoreCase("admin"))) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }

        //delete answer
        answerBusinessService.deleteAnswer(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");

        return new ResponseEntity<>(answerResponse, HttpStatus.OK);
    }

    @GetMapping(path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable String questionId, @RequestHeader String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        //check access token and if user is signed out
        commonBusinessService.getUserAuthenticationEntity(authorization);

        //check if question exists
        QuestionEntity questionEntity = questionBusinessService.findByQuestionId(questionId);

        if (Objects.isNull(questionEntity)) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        //get all answers of the question
        List<AnswerEntity> allAnswers = answerBusinessService.getAllAnswers(questionId);
        List<AnswerDetailsResponse> response = new ArrayList<>();
        for (AnswerEntity answer : allAnswers) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse()
                    .id(questionId).answerContent(answer.getContent())
                    .questionContent(questionEntity.getContent());
            response.add(answerDetailsResponse);
        }
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
