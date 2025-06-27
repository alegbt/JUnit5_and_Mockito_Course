package com.appsdeveloperblog.estore.service;

import com.appsdeveloperblog.estore.data.UsersRepository;
import com.appsdeveloperblog.estore.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks //classe con mock iniettati x evitare che salvi su db
    UserServiceImpl userService;

    @Mock //crea instance mock di questa. lo usiamo x injectare nelle lcassi service che vogliamo siano mockate (x non salvare su db x esempio)
    UsersRepository usersRepository;

    @Mock
    EmailVerificationServiceImpl emailVerificationServiceImpl;

    String firstName;
    String lastName;
    String email;
    String password;
    String repeatPassword;

    @BeforeEach
    void init() {
        firstName = "Sergey";
        lastName  = "Kargopolov";
        email = "test@test.com";
        password = "12345678";
        repeatPassword = "12345678";
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("User object created")
    @Test
    void testCreateUser_whenUserDetailsProvided_returnsUserObject() {
        // Arrange
        Mockito.when(usersRepository.save(any(User.class))).thenReturn(true); //quando .save viene chiamato invece di fare il .save ritorna true e basta
        //qui .save viene chiamato da .createUser - e dentro di esso il .save restituisce true

        // Act
        User user = userService.createUser(firstName, lastName, email, password, repeatPassword);

        // Assert
        assertNotNull(user, "The createUser() should not have returned null");
        assertEquals(firstName, user.getFirstName(), "User's first name is incorrect.");
        assertEquals(lastName, user.getLastName(), "User's last name is incorrect");
        assertEquals(email, user.getEmail(), "User's email is incorrect");
        assertNotNull(user.getId(), "User id is missing");
        Mockito.verify(usersRepository)
                .save(any(User.class));
    }

    @DisplayName("Empty first name causes correct exception")
    @Test
    void testCreateUser_whenFirstNameIsEmpty_throwsIllegalArgumentException() {
        // Arrange
        String firstName = "";
        String expectedExceptionMessage = "User's first name is empty";

        // Act & Assert
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, ()-> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        },"Empty first name should have caused an Illegal Argument Exception");

        // Assert
        assertEquals(expectedExceptionMessage,thrown.getMessage(),
                "Exception error message is not correct");
    }

    @DisplayName("Empty last name causes correct exception")
    @Test
    void testCreateUser_whenLastNameIsEmpty_throwsIllegalArgumentException() {
        // Arrange
        String lastName = "";
        String expectedExceptionMessage = "User's last name is empty";

        // Act & Assert
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, ()-> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        },"Empty last name should have caused an Illegal Argument Exception");

        // Assert
        assertEquals(expectedExceptionMessage,thrown.getMessage(),
                "Exception error message is not correct");
    }

    @DisplayName("If save() method causes RuntimeException, a UserServiceException is thrown")
    @Test
    void testCreateUser_whenSaveMethodThrowsException_thenThrowsUserServiceException() {
        // Arrange
        when(usersRepository.save(any(User.class))).thenThrow(RuntimeException.class);

        // Act & Assert
        assertThrows(UserServiceException.class, ()-> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Should have thrown UserServiceException instead");
    }

    @Test
    @DisplayName("EmailNotificationException is handled")
    void testCreateUser_whenEmailNotificationExceptionThrown_throwsUserServiceException() {
        // Arrange
       when(usersRepository.save(any(User.class))).thenReturn(true);

       //questo si usa per i metodi void come .scheduleEmailConfirmartion
       doThrow(EmailNotificationServiceException.class)
               .when(emailVerificationServiceImpl)
               .scheduleEmailConfirmation(any(User.class));

       //donothing fa si che non si triggeri il metodo quando chiamato, non da l'eccezione o si risolve
       //doNothing().when(emailVerificationService).scheduleEmailConfirmation(any(User.class));

       // Act & Assert
        assertThrows(UserServiceException.class, ()-> {
            userService.createUser(firstName, lastName, email, password, repeatPassword);
        }, "Should have thrown UserServiceException instead");

        // Assert
        //controlla quante volte e stato chiamato un metodo
        verify(emailVerificationServiceImpl, times(1)).
                scheduleEmailConfirmation(any(User.class));

    }

    @DisplayName("Schedule Email Confirmation is executed")
    @Test
    void testCreateUser_whenUserCreated_schedulesEmailConfirmation() {
        // Arrange
        when(usersRepository.save(any(User.class))).thenReturn(true);

        //chiama un metodo reale, anche se emailVerificationServiceImpl e implementato come mock
        doCallRealMethod().when(emailVerificationServiceImpl)
                .scheduleEmailConfirmation(any(User.class));

        // Act
        userService.createUser(firstName, lastName, email, password, repeatPassword);

        // Assert
        verify(emailVerificationServiceImpl, times(1))
                .scheduleEmailConfirmation(any(User.class));
    }


}
