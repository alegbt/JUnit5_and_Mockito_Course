package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.security.SecurityConstants;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import javax.validation.Valid;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(
          webEnvironment =
//        SpringBootTest.WebEnvironment.MOCK //crea mock bean senza http (non puoi chiamare mockMvc.perform(get.. ) - si usa x testare parti specifiche della logica
//        SpringBootTest.WebEnvironment.DEFINED_PORT //crea mock http e puoi settare le application.properties specifiche
//          ,properties = {"server.port=8081","hostname=192.168.0.2"} //metodo 1 - priority 2
          SpringBootTest.WebEnvironment.RANDOM_PORT //porta random disponibile
)
        @TestPropertySource(locations = "/application-test.properties"
                ,properties = "server.port=8081") //metodo 2 - priority 3 (piu)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIntegrationTest {

    @Value("${server.port}") //metodo 3 - priority 1 (meno)
    private int serverPort;

    @LocalServerPort
    private int localServerPort; //con RANDOM_PORT ti da la porta come int

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String authorizationToken;


    @Test
    void contextLoads(){
        System.out.println(serverPort);
        System.out.println(localServerPort);
    }


    //FUll integration TEST - si usano le classi vere del progetto (service controller interface)
    //Simulates a real HTTP call to the full running application context, including the web server
    // (Tomcat/Jetty/etc), Spring beans, and actual controller mappings.
    //Si crea il JSON a mano per simulare una reale richiesta esterna

    @Test
    @DisplayName("User can be created")
    @Order(1)
    void testCreateUser_whenValidDetailsProvided_returnsUserDetails() throws JSONException {

        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstName", "Pino");
        userDetailsRequestJson.put("lastName", "Turbo");
        userDetailsRequestJson.put("email", "Pino@gmail.com");
        userDetailsRequestJson.put("password", "12345678");
        userDetailsRequestJson.put("repeatPassword", "12345678");

        HttpHeaders httpHeaders = new HttpHeaders();
        // Specifica il formato dei dati che sto inviando (request)
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        // Specifica il formato dei dati che mi aspetto nella risposta (response)
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));


        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), httpHeaders);

        //Act
        ResponseEntity<UserRest> createdUserDetailsEntity =
                testRestTemplate.postForEntity(
                "/users"
                ,request
                ,UserRest.class);

        UserRest createdUserDetails = createdUserDetailsEntity.getBody();


        //Assert
        Assertions.assertEquals(HttpStatus.OK, createdUserDetailsEntity.getStatusCode());
        Assertions.assertEquals(userDetailsRequestJson.getString("firstName"),
                createdUserDetails.getFirstName(),
                "Returned user's first name seems to be incorrect");
        Assertions.assertEquals(userDetailsRequestJson.getString("lastName"),
                createdUserDetails.getLastName(),
                "Returned user's last name seems to be incorrect");
        Assertions.assertEquals(userDetailsRequestJson.getString("email"),
                createdUserDetails.getEmail(),
                "Returned user's email seems to be incorrect");
        Assertions.assertFalse(createdUserDetails.getUserId().trim().isEmpty(),
                "User id should not be empty");
    }


    //Test APi con token JWT mancante
    @Test
    @DisplayName("GET /users requires JWT")
    @Order(2)
    void testGetUsers_whenMissingJWT_returns403(){
        //Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity requestEntity = new HttpEntity(null, headers);

        //Act
        ResponseEntity<List<UserRest>> response =  testRestTemplate.exchange("/users"
                , HttpMethod.GET
                , requestEntity
                , new ParameterizedTypeReference<List<UserRest>>(){}
        );


        //Assert
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "httpstatus 403 should have been returned");
    }



    @Test
    @DisplayName("login works")
    @Order(3)
    void testUserLogin_whenValidCredentialsProvided_returnsJWTinAuthorizationHeader() throws JSONException {

        // Arrange
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "Pino@gmail.com");
        loginCredentials.put("password", "12345678");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);

        // Act
        ResponseEntity<String> response = testRestTemplate.postForEntity(
                "/users/login",
                request,
                null
        );

        //facendo /users/login vado a fare login tramite spring security - restituisce un token che abilita al login, qui lo salvo in una variabile
        authorizationToken = response.getHeaders().getValuesAsList(SecurityConstants.HEADER_STRING).get(0);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(),
                "HTTP Status code should be 200");

        Assertions.assertNotNull(response.getHeaders()
                        .getValuesAsList(SecurityConstants.HEADER_STRING).get(0),
                "Response should contain Authorization header with JWT");

        Assertions.assertNotNull(response.getHeaders()
                        .getValuesAsList("UserID").get(0),
                "Response should contain UserID in a response header");
    }


    //Chiamata in cui serve valid Authorization token
    @Test
    @DisplayName("GET /users works")
    @Order(4)
    void testGetUsers_whenValidJWTProvided_returnsusers() throws JSONException {
        //Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity requestEntity = new HttpEntity(headers);


        //Act
        ResponseEntity<List<UserRest>> response = testRestTemplate.exchange("/users",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<UserRest>>() {
                });

        //Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be 200");
        Assertions.assertEquals(1, response.getBody().size(), "there should be exactly 1 user");


    }





}
