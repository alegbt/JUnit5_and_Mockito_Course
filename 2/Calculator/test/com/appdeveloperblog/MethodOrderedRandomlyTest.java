package com.appdeveloperblog;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

//@TestMethodOrder(MethodOrderer.Random.class) //run test in random order
//@TestMethodOrder(MethodOrderer.MethodName.class) //run test in name order (A,B,C,D)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //run test in a determined order (C,A,B,D)
public class MethodOrderedRandomlyTest {


    @Order(1)
    @Test
    void testC(){
        System.out.println("test C");
    }

    @Order(2)
    @Test
    void testA(){
        System.out.println("test A");
    }

    @Order(3)
    @Test
    void testB(){
        System.out.println("test B");
    }

    @Order(4)
    @Test
    void testD(){
        System.out.println("test D");
    }





}
