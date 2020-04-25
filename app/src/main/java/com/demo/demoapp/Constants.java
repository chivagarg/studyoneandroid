package com.demo.demoapp;

public final class Constants {
    // GOOGLE does NOT let you configure another project with the same SHA-1
    public static final String CLIENT_ID = "349372081929-amvj2cn1vkk4gp3u6dk2c23q0mrms6l7.apps.googleusercontent.com";

    // Also note that the address 127.0.0.1 on your development machine corresponds to the
    // emulator's own loopback interface. If you want to access services running on your
    // development machine loopback interface (a.k.a. 127.0.0.1 on your machine), you should use
    // the special address 10.0.2.2 instead.
    //public static final String GET_OR_CREATE_URL = "http://10.0.2.2:8080/auth/getorcreate";
    // Based on
    // https://stackoverflow.com/questions/4779963/how-can-i-access-my-localhost-from-my-android-device
    // TODO: Delete usesClearTraffic = True before launch.
    public static final String GET_OR_CREATE_URL = "http://10.0.0.184:8080/auth/getorcreate";

    public static final String DAILY_WORDS_URL = "http://10.0.0.184:8080/v1/words/dailywords";
    // public static final String DAILY_WORDS_URL = "http://10.0.2.2:8080/v1/words/dailywords";
}
