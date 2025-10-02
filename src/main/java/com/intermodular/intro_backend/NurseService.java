package com.intermodular.intro_backend;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class NurseService {
    private static final String NURSE_JSON_PATH = "src/main/resources/data/nurse.json";
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public JSONArray getAllNurses() throws IOException {
        String content = Files.readString(Paths.get(NURSE_JSON_PATH));
        return new JSONArray(content);
    }

    public void saveAllNurses(JSONArray nurses) throws IOException {
        Files.writeString(Paths.get(NURSE_JSON_PATH), nurses.toString(2));
    }

    public boolean existsById(int id) throws IOException {
        JSONArray nurses = getAllNurses();
        for (int i = 0; i < nurses.length(); i++) {
            if (nurses.getJSONObject(i).getInt("nurse_id") == id) {
                return true;
            }
        }
        return false;
    }

    public boolean existsByEmail(String email) throws IOException {
        JSONArray nurses = getAllNurses();
        for (int i = 0; i < nurses.length(); i++) {
            if (nurses.getJSONObject(i).getString("email").equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public JSONObject registerNurse(int id, String firstName, String lastName, String email, String password) throws IOException {
        if (existsById(id)) {
            throw new IllegalArgumentException("El id ya existe");
        }
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya existe");
        }
        String hashedPassword = passwordEncoder.encode(password);
        JSONObject newNurse = new JSONObject();
        newNurse.put("nurse_id", id);
        newNurse.put("first_name", firstName);
        newNurse.put("last_name", lastName);
        newNurse.put("email", email);
        newNurse.put("password", hashedPassword);
        JSONArray nurses = getAllNurses();
        nurses.put(newNurse);
        saveAllNurses(nurses);
        return newNurse;
    }
}
