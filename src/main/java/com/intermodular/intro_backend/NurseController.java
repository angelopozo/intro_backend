package com.intermodular.intro_backend;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/nurse")
public class NurseController {

    public JSONArray getAllNurses() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("src/main/resources/data/nurse.json")));
        JSONArray nurses = new JSONArray(content);

        System.out.println(content);

        return nurses;
    }



}
