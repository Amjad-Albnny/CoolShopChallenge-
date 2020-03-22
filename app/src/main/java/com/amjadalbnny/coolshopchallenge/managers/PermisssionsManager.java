package com.amjadalbnny.coolshopchallenge.managers;

public class PermisssionsManager {

    private static PermisssionsManager instance;

    public static PermisssionsManager getInstance() {

        if(instance == null)
            instance = new PermisssionsManager();

        return instance;
    }

    //public void check

}
