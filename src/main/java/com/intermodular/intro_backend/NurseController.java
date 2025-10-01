package com.intermodular.intro_backend;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nurse")
public class NurseController {

    private final JSONArray listNurses = new JSONArray();
    JSONObject nurse1 = new JSONObject();
    
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
}