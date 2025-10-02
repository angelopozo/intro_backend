package com.intermodular.intro_backend;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/nurse")
public class NurseController {

    private final JSONArray listNurses = getListNurses();
    private final NurseService nurseService;

    @Autowired
    public NurseController(NurseService nurseService) {
        this.nurseService = nurseService;
    }

    private JSONArray getListNurses() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("src/main/resources/data/nurse.json")));
            return new JSONArray(content);
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<JSONObject> findByName(@PathVariable String name) {
        for (int i = 0; i < listNurses.length(); i++) {
            JSONObject n = listNurses.getJSONObject(i);

            if (n.getString("name").equals(name)) {
                return ResponseEntity.ok(n);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNurse(@RequestBody NurseRegisterRequest request) {
        try {
            JSONObject newNurse = nurseService.registerNurse(
                    request.nurse_id(),
                    request.first_name(),
                    request.last_name(),
                    request.email(),
                    request.password());
            return ResponseEntity.status(HttpStatus.CREATED).body(newNurse.toMap());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
        }
    }

    public record NurseRegisterRequest(int nurse_id, String first_name, String last_name, String email,
            String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Boolean>> login(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String password = body.get("password");
        boolean answer = false;
        for (int i = 0; i < listNurses.length(); i++) {
            JSONObject nurse = listNurses.getJSONObject(i);
            if (nurse.getString("name").equals(name) && nurseService.isPasswordCorrect(password, nurse.getString("password"))) {
                answer = true;
                break;
            }
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("response", answer);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/index")
    public ResponseEntity<JSONArray> getAllNurses() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("src/main/resources/data/nurse.json")));
        JSONArray nurses = new JSONArray(content);

        System.out.println(content);

        return ResponseEntity.ok(nurses);
    }
}
