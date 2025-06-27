package com.appsdeveloperblog.tutorials.junit.ui.controllers;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserControllerWithTestContainersTest {

    // Avvia un container MySQL temporaneo per i test con Testcontainers con i dati impostati
    @Container
    private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.4.0")
            .withDatabaseName("photo_app")
            .withUsername("sergey")
            .withPassword("sergey");

    // Sovrascrive le proprietà di connessione al DB con quelle del container MySQL
    @DynamicPropertySource
    public static void overridProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl); //setting up port number
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }


    @Test
    @DisplayName("MySql container is created and running")
    void testContainerIsRunning(){
        assertTrue(mySQLContainer.isCreated(), "mysqlcontainer not created");
        assertTrue(mySQLContainer.isRunning(), "mysqlcontainer not running");
    }



}
