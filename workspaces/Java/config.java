package com.settermjd.twilio.envvars;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        System.out.println(String.format(
            "Hello World. Shell is: %s. Name is: %s",
            System.getenv("SHELL"),
            dotenv.get("NAME")
        ));
    }
}