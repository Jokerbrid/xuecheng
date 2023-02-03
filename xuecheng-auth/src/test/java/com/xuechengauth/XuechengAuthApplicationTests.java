package com.xuechengauth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


class XuechengAuthApplicationTests {
    @Test
    void contextLoads() {
        System.out.println(new  BCryptPasswordEncoder().encode("xcWebApp"));
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder enCode = new BCryptPasswordEncoder();
        System.out.println(new BCryptPasswordEncoder().encode("111111"));
        System.out.println(enCode.matches("111111",""));
    }

}
