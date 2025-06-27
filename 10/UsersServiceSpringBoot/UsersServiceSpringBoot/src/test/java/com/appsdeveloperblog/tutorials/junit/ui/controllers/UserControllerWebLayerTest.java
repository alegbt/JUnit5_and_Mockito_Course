package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.UsersServiceSpringBootApplication;
import com.appsdeveloperblog.tutorials.junit.service.UsersService;
import com.appsdeveloperblog.tutorials.junit.service.UsersServiceImpl;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UsersController.class, //solo usercontroller funziona dei controller
excludeAutoConfiguration = {SecurityAutoConfiguration.class}) //esclude le configuration di springsecurity
@MockBean({UsersServiceImpl.class,}) //mockbean usato cosi mocka 1 classe specifica - mockbean aggiunge il bean al lifecycle, mock da solo aggiunge solo 1 classe mock ma non il bean
public class UserControllerWebLayerTest {

    @Autowired
    MockMvc mockMvc;

    //@MockBean //mockbean usato qui mocka tutte le implementazioni di qst interfaccia
    @Autowired
    UsersService usersService;


    //MockMvc with mocked service
    //Tests only the web layer (e.g., the @RestController) without running a full server.
    // Internal service dependencies are mocked (e.g., using Mockito).
    // non si crea un JSOn da 0 per simulare al 100% ma piuttosto si testano componenti specifiche interne
    //del programma, in qst caso usiamo userDetailsRequestModel per dare 1 obj gia serializzato (presupponiamo payload corretto)
    @Test
    @DisplayName("User can be created")
    void testCreateUser_whenValidUserDetailsProvided_returnsCreatedUserDetails() throws Exception {

        //Arrange
        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("Pino");
        userDetailsRequestModel.setLastName("Turbo");
        userDetailsRequestModel.setEmail("pino@gmail.com");
        userDetailsRequestModel.setPassword("12345678");
        userDetailsRequestModel.setRepeatPassword("12345678");

        UserDto userDto = new ModelMapper().map(userDetailsRequestModel, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());
        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);



        //qui buildo la request, quella chiamata da postman
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        UserRest createdUser = new ObjectMapper()
                .readValue(responseBodyAsString, UserRest.class);

        //Assert
        Assertions.assertEquals(userDetailsRequestModel.getFirstName(), createdUser.getFirstName(), "Returned firstname does not match");
        Assertions.assertEquals(userDetailsRequestModel.getLastName(), createdUser.getLastName(), "Returned lastname does not match");
        Assertions.assertEquals(userDetailsRequestModel.getEmail(), createdUser.getEmail(), "Returned email does not match");
        Assertions.assertFalse(createdUser.getUserId().isEmpty(), "userid should not be empty");
    }



    @Test
    @DisplayName("Last name is not empty")
    void testCreateUser_whenFirstNameIsNotProvided_returns400StatusCode() throws Exception {
        //Arrange
        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("");
        userDetailsRequestModel.setLastName("Turbo");
        userDetailsRequestModel.setEmail("pino@gmail.com");
        userDetailsRequestModel.setPassword("12345678");
        userDetailsRequestModel.setRepeatPassword("12345678");


        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //Assert
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(), "not HTTP status 400");
    }


    @Test
    @DisplayName("Last name is not empty")
    void testCreateUser_whenFirstNameIsShorterThan2returns400StatusCode() throws Exception {
        //Arrange
        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("P");
        userDetailsRequestModel.setLastName("Turbo");
        userDetailsRequestModel.setEmail("pino@gmail.com");
        userDetailsRequestModel.setPassword("12345678");
        userDetailsRequestModel.setRepeatPassword("12345678");


        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //Assert
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(), "not HTTP status 400");
    }






}
