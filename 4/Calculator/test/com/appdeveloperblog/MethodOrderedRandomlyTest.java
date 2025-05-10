package com.appdeveloperblog;

import org.junit.jupiter.api.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS) //PER_CLASS non instanzia la classe per ogni metodo, si usa x sharare una variabile tra diversi metodi

//@TestMethodOrder(MethodOrderer.Random.class) //run test in random order
//@TestMethodOrder(MethodOrderer.MethodName.class) //run test in name order (A,B,C,D)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //run test in a determined order (C,A,B,D)
public class MethodOrderedRandomlyTest {


    StringBuilder completed = new StringBuilder("");

    @AfterEach
    void afterEach(){
        System.out.println("completed state = " + completed);
    }

    @Order(1)
    @Test
    void testC(){
        System.out.println("test C");
        completed.append("1");
    }

    @Order(2)
    @Test
    void testA(){
        System.out.println("test A");
        completed.append("2");
    }

    @Order(3)
    @Test
    void testB(){
        System.out.println("test B");
        completed.append("3");
    }

    @Order(4)
    @Test
    void testD(){
        System.out.println("test D");
        completed.append("4");
    }





}
