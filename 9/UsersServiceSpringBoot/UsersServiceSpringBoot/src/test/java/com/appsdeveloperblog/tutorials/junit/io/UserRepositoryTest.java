package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

@DataJpaTest
public class UserRepositoryTest {


    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    UsersRepository usersRepository;

    UserEntity user1 = new UserEntity();
    UserEntity user2 = new UserEntity();


    private final String userid1 = UUID.randomUUID().toString();
    private final String userid2 = UUID.randomUUID().toString();
    private final String email1 = "pino1@gmail.com";
    private final String email2 = "pino2@gmail.com";




    @BeforeEach
    void setup(){
        user1.setUserId(userid1);
        user1.setFirstName("Pino");
        user1.setLastName("Turbo");
        user1.setEmail(email1);
        user1.setEncryptedPassword("12345678");
        testEntityManager.persistAndFlush(user1);

        user2.setUserId(userid2);
        user2.setFirstName("asd");
        user2.setLastName("Turasdbo");
        user2.setEmail(email2);
        user2.setEncryptedPassword("12345678");
        testEntityManager.persistAndFlush(user2);
    }


    @Test
    void testFindByEmail_whenGivenCorrectEmail_returnUserEntity() {
        //Arrange

        //Act
        UserEntity storedUser = usersRepository.findByEmail(user1.getEmail());

        //Assert
        Assertions.assertEquals(user1.getEmail(), storedUser.getEmail(), "email not match");
    }



    @Test
    void  testFindByUserId_whenCorrectUserId_ReturnUserEntity() {
        //Arrange

        //Act
        UserEntity storedUser = usersRepository.findByUserId(userid2);

        //Assert
        Assertions.assertNotNull(storedUser, "user should not be null");
        Assertions.assertEquals(storedUser.getUserId(), userid2, "incorrect id");

    }


    @Test
    void testFindUsersWithEmailEndsWith_whenGiveEmailDomain_returnsUsersWithGivenDomain() {

        UserEntity user3 = new UserEntity();

        //Arrange
        user3.setUserId(UUID.randomUUID().toString());
        user3.setFirstName("Ugo");
        user3.setLastName("puno");
        user3.setEmail("pino@gmail.itz");
        user3.setEncryptedPassword("12345678");
        testEntityManager.persistAndFlush(user3);

        String emailDomainName = "@gmail.itz";

        //Act
        List<UserEntity> users = usersRepository.findUsersWithEmailEndingWith(emailDomainName);



        //Assert
        Assertions.assertEquals(1, users.size(), "should have been 1 result");
        Assertions.assertTrue(users.get(0).getEmail().endsWith(emailDomainName));
    }
}






