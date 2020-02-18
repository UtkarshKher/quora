package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.entity.UserAuthenticationEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {

    @Autowired
    private AdminDao adminDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String uuid, final String authorizationToken) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthenticationEntity userAuthEntity = adminDao.getAuthToken(authorizationToken);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        String role = userAuthEntity.getUser().getRole();

        if (role.equals("admin")) {
            UserEntity deletedUser = adminDao.deleteUser(uuid);
            if (deletedUser == null) {
                throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
            }
            return deletedUser;
        }
        throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
    }


}