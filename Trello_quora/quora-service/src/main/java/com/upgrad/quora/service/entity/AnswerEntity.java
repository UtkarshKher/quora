package com.upgrad.quora.service.entity;

import javax.validation.constraints.NotNull;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "answer")
@NamedQueries({
        @NamedQuery(name = "answerById", query = "select aE from AnswerEntity aE where aE.uuid = :uuid"),
        @NamedQuery(name = "answersByQuestionId", query = "select aES from AnswerEntity aES where aES.questionId = :question_id")
})
public class AnswerEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @NotNull
    @Column(name = "ans")
    @Size(max = 255)
    private String content;

    @Column(name = "date")
    @javax.validation.constraints.NotNull
    private ZonedDateTime date;

    @Column(name = "user_id")
    @Size(max = 200)
    @javax.validation.constraints.NotNull
    private String userId;

    @Column(name = "question_id")
    @Size(max = 200)
    private String questionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
