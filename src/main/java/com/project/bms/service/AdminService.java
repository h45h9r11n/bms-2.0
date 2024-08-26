package com.project.bms.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {
    public boolean backup(String filename){

        String command = "chdir public && mkdir " + filename + " && cp -r images avatars " + filename + " && zip -r " + filename + ".zip " + filename;
        System.out.println(command);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
