package com.appsdeveloperblog.estore.service;

import com.appsdeveloperblog.estore.model.User;

import java.util.UUID;

public class UserServiceImpl implements UserService {

    @Override
    public User createUser(String firstName,
                           String lastName,
                           String email,
                           String password,
                           String repeatPassword) {

        if(firstName == null || firstName.trim().length() == 0){
            throw new IllegalArgumentException("User's firstname is empty");
        }

        if(lastName == null || lastName.trim().length() == 0){
            throw new IllegalArgumentException("User's lastname is empty");
        }



        return new User(firstName, lastName, email, UUID.randomUUID().toString());
    }

}
