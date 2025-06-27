package com.appsdeveloperblog.UsersService.ui;

import com.appsdeveloperblog.UsersService.ui.model.User;
import com.appsdeveloperblog.UsersService.ui.model.UserRest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
public class UsersControllerWithTestContainerITest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:9.2.0");

    //serve x identificare la port scelta da WebEnvironment.RANDOM_PORT e assegnarla ad 1 variabile
    @LocalServerPort
    private int port;

    private final String TEST_EMAIL = "ale@gmail.com";
    private final String TEST_PASSWORD = "123456789";

    private String userId;
    private String token;



    private final RequestLoggingFilter requestLoggingFilter = new RequestLoggingFilter(); //log di tutto nella request
                                                            //new RequestLoggingFilter(LogDetail.BODY) //log 1 valore singolo
    //private final RequestLoggingFilter requestLoggingFilter = RequestLoggingFilter.with(LogDetail.BODY, LogDetail.HEADERS); //log valore multiplo

    private final ResponseLoggingFilter responseLoggingFilter = new ResponseLoggingFilter(); //log di tutto nella response
                                                              //new ResponseLoggingFilter(LogDetail.BODY) //log 1 valroe singolo
    //private final ResponseLoggingFilter responseLoggingFilter = ResponseLoggingFilter.with(LogDetail.BODY, LogDetail.HEADERS); ////log valore multiplo



    @BeforeAll
    void setUp(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port=port;
        RestAssured.filters(requestLoggingFilter); //log request attivo x tutti i metodi
        RestAssured.filters(responseLoggingFilter); //log response attivo x tutti i metodi

        //setting globali di request e response
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON) //setto gli header x tuti i metodi
                .setAccept(ContentType.JSON)
                //.addQueryParam("test") //puoi aggiungere diversi parametri a tutti i emtodi della classe
                .build();

        RestAssured.responseSpecification = new ResponseSpecBuilder()
                //.expectStatusCode(anyOf(is(200), is(201), is(204))) //accetto tutti questi status code in ogni metodo
                //.expectBody("id", notNullValue()) //controlla che la response abbia un id not null
                .build();
    }

    @Order(1)
    @Test
    void testContainerIsRunning() {
        assertTrue(mysqlContainer.isRunning());
    }


    //valido i campi entrando nella response tramite "pathing" nell'object di response
    @Order(2)
    @Test
    @Disabled
    void testCreateuser_whenValidDetailsProvided_returnsCreatedUser_USINGResponse(){
        //Arrange
        //sostituito da RestAssured.requestSpecification = new RequestSpecBuilder() in  @BeforeAll
//        Headers headers = new Headers(
//                new Header("Content-Type", "application/json"),
//                new Header("Accept", "application/json")
//        );

        User  newUser = new User("ale","gob",TEST_EMAIL,TEST_PASSWORD);

        //Act
        //Chiamata API:
        Response response = given()
                //.headers(headers) //cosi li metto in 1 sola line //sostituito da RestAssured.requestSpecification = new RequestSpecBuilder() in  @BeforeAll
                .body(newUser)
        .when()
                .post("/users")
        .then()
                .extract()
                .response();

        //Assert
        //2
        assertEquals(201,response.statusCode());
        assertEquals(newUser.getFirstName(),response.jsonPath().getString("firstName"));
        assertEquals(newUser.getLastName(),response.jsonPath().getString("lastName"));
        assertEquals(newUser.getEmail(),response.jsonPath().getString("email"));
        assertNotNull(response.jsonPath().getString("id"));
    }


    //creo 1 UserRest in cui assegno il risultato della chiamata e poi controllo
    @Order(3)
    @Test
    @Disabled
    void testCreateuser_whenValidDetailsProvided_returnsCreatedUser_USINGextractAs(){
        //Arrange
        //sostituito da RestAssured.requestSpecification = new RequestSpecBuilder() in  @BeforeAll
//        Headers headers = new Headers(
//                new Header("Content-Type", "application/json"),
//                new Header("Accept", "application/json")
//        );

        User  newUser = new User("ale","gob",TEST_EMAIL,TEST_PASSWORD);

        //Act
        //Chiamata API:
        //metodo 1
        UserRest createdUser = given()
                                //diversi modi x mettere gli headers
                                //.contentType(ContentType.JSON)  //.header("Content-Type","application/json")
                                //.accept(ContentType.JSON)       //.header("Accept", "application/json")
        //        .headers(headers) //sostituito da RestAssured.requestSpecification = new RequestSpecBuilder() in  @BeforeAll
                .body(newUser)
        .when()
                .post("/users")
        .then()
                .extract()
                .as(UserRest.class);

        //Assert
        assertEquals(newUser.getFirstName(), createdUser.getFirstName());
        assertEquals(newUser.getLastName(), createdUser.getLastName());
        assertEquals(newUser.getEmail(), createdUser.getEmail());
        assertNotNull(createdUser.getId());
    }



    //faccio vaLidation nella chiamata stessa con chaining usando i metodi di REST assured
    @Order(4)
    @Test
    void testCreateuser_whenValidDetailsProvided_returnsCreatedUser_USINGRestAssuredBuiltInMethods(){
        //Arrange
        //sostituito da RestAssured.requestSpecification = new RequestSpecBuilder() in  @BeforeAll
//        Headers headers = new Headers(
//                new Header("Content-Type", "application/json"),
//                new Header("Accept", "application/json")
//        );

        User  newUser = new User("ale","gob",TEST_EMAIL,TEST_PASSWORD);

        //Act
        //Chiamata API:
        given()
               //.log().all() //log di HTTP request details
  //              .headers(headers) //sostituito da RestAssured.requestSpecification = new RequestSpecBuilder() in  @BeforeAll
                .body(newUser)
            .when()
                .post("/users")
            .then()
               //.log().all() //log di HTTP response details
                .statusCode(201)
                .body("id", notNullValue())
                .body("firstName", equalTo(newUser.getFirstName()))
                .body("lastName", equalTo(newUser.getLastName()))
                .body("email", equalTo(newUser.getEmail()));
    }


    @Test
    @Order(5)
    void testLogin_whenValidCredentialsProvided_returnTokenAndUserIdHeaders(){
        //Arrange
        Map<String, String > credentials = new HashMap<>();
        credentials.put("email", TEST_EMAIL);
        credentials.put("password", TEST_PASSWORD);

        //Act
        Response response = given()
                .body(credentials)
        .when()
                .post("/login")
        ;

        this.userId = response.header("userId"); // aggiunti all'header in AuthenticationFilter
        this.token = response.header("token");

        //Assert
        assertEquals(200, response.statusCode());
        assertNotNull(userId);
        assertNotNull(token);
    }


    @Test
    @Order(6)
    void testGetUser_withValidAuthenticationToken_returnsUser(){
        given()
                .pathParam("userId", this.userId)              //path param {userId} nel controller
                .header("Authorization", "Bearer " + token) //token scritto a mano
                //.auth().oauth2(token)                         //REST assured method che fa la stessa cosa sopra, e lo nasconde dai log

        .when()
                .get("/users/{userId}")
        .then()
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("email", equalTo(TEST_EMAIL))
                .body("firstName", notNullValue())
                .body("lastName", notNullValue())
                ;
    }

//test che non funzioni senza auth token
    @Test
    @Order(7)
    void testGetUser_withMissingAuthHeader_returnsForbidden() {
        given()
                .pathParam("userId", this.userId)
                .when()
                .get("/users/{userId}")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }


    //get con una List di return
    @Test
    @Order(8)
    void testGetUsersWithValidTokenAndQueryparams_returnsPaginatedUsersList(){
        given()
                .headers("Authorization", "Bearer " + token)
                .queryParam("page",1)
                .queryParam("limit",10)
        .when()
                .get("/users")
        .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                ;
    }



}
