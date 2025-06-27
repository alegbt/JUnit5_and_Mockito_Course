package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.util.UUID;

@DataJpaTest
public class UserEntityIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager; //fa operazone su db temporaneo

    UserEntity userEntity = new UserEntity();

    @BeforeEach
    void setup(){
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setFirstName("Pino");
        userEntity.setLastName("Turbo");
        userEntity.setEmail("pino@gmail.com");
        userEntity.setEncryptedPassword("12345678");
    }


    @Test
    void testUserEntity_whenValidUserDetailsProvided_shouldReturnStoredUserDetails(){
        //Arrange

        //Act
        //persistAndFlush = persist persiste la entity, per salvarla su db,
        // flush la inserisce immediatamente, altrimenti potrebbe aspettare x fare l'insert alla fine delle transazioni
        UserEntity storedUserEntity = testEntityManager.persistAndFlush(userEntity);


        //Assert
        Assertions.assertTrue(storedUserEntity.getId() > 0);
        Assertions.assertEquals( userEntity.getUserId(), storedUserEntity.getUserId());
        Assertions.assertEquals( userEntity.getFirstName(), storedUserEntity.getFirstName());
    }

    @Test
    void testUserEntity_whenFirstNameIsTooLong_shouldThrowException(){
        //Arrange
        userEntity.setFirstName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        //Assert and Act
        Assertions.assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(userEntity);
        }, "PersistenceException should have been thrown");
    }


    @Test
    void testUserEntity_whenUserIdIsPresent_shouldThrowPersistenceException() {
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setUserId("11223344");
        userEntity1.setFirstName("asd");
        userEntity1.setLastName("Turbo");
        userEntity1.setEmail("pino@gmail.com");
        userEntity1.setEncryptedPassword("12345678");

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setUserId("11223344");
        userEntity2.setFirstName("eee");
        userEntity2.setLastName("Turbo");
        userEntity2.setEmail("qwe@gmail.com");
        userEntity2.setEncryptedPassword("12345678");

        testEntityManager.persistAndFlush(userEntity1);

        Assertions.assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(userEntity2);
        }, "PersistenceException should have been thrown");
    }





}
