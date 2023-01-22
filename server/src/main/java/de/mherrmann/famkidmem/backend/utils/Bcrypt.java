package de.mherrmann.famkidmem.backend.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Bcrypt {

    private static final int STRENGTH = findBestStrength();

    public static String hash(String message){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(STRENGTH);
        return encoder.encode(message);
    }

    public static boolean check(String message, String hash){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(message, hash);
    }


    private static int findBestStrength(){
        int strength = 8;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(strength);
        String message = "test1234";
        long start = System.currentTimeMillis();
        encoder.encode(message);
        long stop = System.currentTimeMillis();
        long cost = stop - start;
        while(cost < 800){
            strength++;
            cost *= 2;
        }
        return strength;
    }
}
