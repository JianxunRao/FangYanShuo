package com.trojx.regularpractice;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.Scanner;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        Scanner scanner=new Scanner(System.in);
        int i=1;
        while(scanner.hasNext()){
            System.out.println(i+scanner.nextLine());
        }
    }
}