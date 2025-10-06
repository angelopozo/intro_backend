package com.intermodular.intro_backend;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/nurse")
public class NurseController {

    private static final String NURSE_JSON_PATH = "src/main/resources/data/nurse.json";

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private JSONArray getListNurses() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(NURSE_JSON_PATH)));
            return new JSONArray(content);
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private void saveAllNurses(JSONArray nurses) throws IOException {
        Files.writeString(Paths.get(NURSE_JSON_PATH), nurses.toString(2));
    }

    private boolean existsById(int id) throws IOException {
        JSONArray nurses = getListNurses();
        for (int i = 0; i < nurses.length(); i++) {
            if (nurses.getJSONObject(i).getInt("nurse_id") == id) {
                return true;
            }
        }
        return false;
    }

    private boolean existsByEmail(String email) throws IOException {
        JSONArray nurses = getListNurses();
        for (int i = 0; i < nurses.length(); i++) {
            if (nurses.getJSONObject(i).getString("email").equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNurse(@RequestBody NurseRegisterRequest request) {
        try {
            if (existsById(request.nurse_id())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El id ya existe");
            }
            if (existsByEmail(request.email())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya existe");
            }

            JSONObject newNurse = new JSONObject();
            newNurse.put("nurse_id", request.nurse_id());
            newNurse.put("first_name", request.first_name());
            newNurse.put("last_name", request.last_name());
            newNurse.put("email", request.email());
            newNurse.put("password", passwordEncoder.encode(request.password()));

            JSONArray nurses = getListNurses();
            nurses.put(newNurse);
            saveAllNurses(nurses);

            return ResponseEntity.status(HttpStatus.CREATED).body(newNurse.toMap());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }

    public record NurseRegisterRequest(int nurse_id, String first_name, String last_name, String email,
            String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Boolean>> login(@RequestBody Map<String, String> body, HttpSession session) {
        JSONArray listNurses = getListNurses();
        String firstName = body.get("first_name");
        String password = body.get("password");
        boolean authenticated = false;

        for (int i = 0; i < listNurses.length(); i++) {
            JSONObject nurse = listNurses.getJSONObject(i);
            if (nurse.getString("first_name").equalsIgnoreCase(firstName) &&
                    passwordEncoder.matches(password, nurse.getString("password"))) {
                authenticated = true;
                // Marcamos la sesión como iniciada con el first_name
                session.setAttribute("user", firstName);
                break;
            }
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("authenticated", authenticated);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/index")
    public ResponseEntity<JSONArray> getAllNurses() {
        return ResponseEntity.ok(getListNurses());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Map<String, Object>> findByName(@PathVariable String name) {
        JSONArray nurses = getListNurses();
        for (int i = 0; i < nurses.length(); i++) {
            JSONObject n = nurses.getJSONObject(i);
            if (n.getString("first_name").equalsIgnoreCase(name)) {
                return ResponseEntity.ok(n.toMap());
            }
        }
        return ResponseEntity.notFound().build();
    }
}
