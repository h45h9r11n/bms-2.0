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
        String pathDir = "/tmp/" + filename;
        String command = "cp -r ./public " + pathDir + " && chdir /tmp && zip -r " + filename + ".zip " + pathDir;
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
