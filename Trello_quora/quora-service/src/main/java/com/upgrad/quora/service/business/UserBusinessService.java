package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthenticationDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class
UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthenticationDao userAuthenticationDao;


    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;


    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(UserEntity userEntity) throws SignUpRestrictedException {
        userDao.getUserByUsername(userEntity.getUserName());

        // To check for the condition username already exists
        if (userDao.getUserByUsername(userEntity.getUserName()) != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        // To check the condition userEmail already exists
        if (userDao.getUserByEmail(userEntity.getEmail()) != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        //Hashing implementation

        String[] encryptedPassword = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedPassword[0]);
        userEntity.setPassword(encryptedPassword[1]);
        userDao.createUser(userEntity);
        return userEntity;

    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthenticationEntity authenticateUser(String username, String password) throws AuthenticationFailedException {
        UserEntity user = userDao.getUserByUsername(username);

        //Username does not exist in DB
        if (user == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        String encryptedPassword = passwordCryptographyProvider.encrypt(password, user.getSalt());
        if (encryptedPassword.equals(user.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthenticationEntity userAuthenticationEntity = new UserAuthenticationEntity();
            userAuthenticationEntity.setUser(user);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthenticationEntity.setAccessToken(jwtTokenProvider.generateToken(user.getUuid(), now, expiresAt));
            userAuthenticationEntity.setLoginAt(now);
            userAuthenticationEntity.setExpiresAt(expiresAt);
            userAuthenticationEntity.setUuid(UUID.randomUUID().toString());
            userAuthenticationDao.createAuthToken(userAuthenticationEntity);

            return userAuthenticationEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthenticationEntity signOut(String accessToken) throws SignOutRestrictedException {
        UserAuthenticationEntity userAuthenticationEntity = userAuthenticationDao.getAuthToken(accessToken);
        if (userAuthenticationEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        userAuthenticationEntity.setLogoutAt(ZonedDateTime.now());
        return userAuthenticationDao.logOut(userAuthenticationEntity);
    }
}