package com.salesforce.utils;

public class RandomPhoneNumberGenerator {

    public static void main(String[] args){
        for (int i = 0; i < 50; i++){
            System.out.println(getRandomNumber());
        }
    }

    public static String getRandomNumber(){
        return String.valueOf((int) ((Math.random() * (798 - 201)) + 201)) + " " +
                String.valueOf((int) ((Math.random() * (798 - 201)) + 201)) + " " +
                String.valueOf((int) ((Math.random() * (9999 - 1000)) + 1000));
    }
}
