package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @RequestMapping(path = "/user/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> userSignUp(SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");

        UserEntity createdUserEntity = UserBusinessService.createUser(userEntity);
        SignupUserResponse signupUserResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(signupUserResponse, HttpStatus.CREATED);
    }


    @RequestMapping(path = "/user/signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> userSignIn(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedAuth = new String(decoded);
        String[] decodedArray = decodedAuth.split(":");
        UserAuthenticationEntity userAuthenticationEntity = UserBusinessService.authenticateUser(decodedArray[0], decodedArray[1]);
        UserEntity userEntity = userAuthenticationEntity.getUser();
        SigninResponse signinResponse = new SigninResponse().id(userAuthenticationEntity.getUuid()).message("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", userAuthenticationEntity.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
    }


    @RequestMapping(path = "/user/signout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> userSignOut(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {
        UserAuthenticationEntity userAuthenticationEntity = UserBusinessService.signOut(authorization);
        UserEntity userEntity = userAuthenticationEntity.getUser();
        SignoutResponse signoutResponse = new SignoutResponse().id(userEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}
