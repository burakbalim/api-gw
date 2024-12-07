package com.application.gateway.main.keymanager;

import com.application.gateway.main.keymanager.dto.KeyRequestDTO;
import com.application.gateway.main.keymanager.dto.KeyResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class KeyController {

    private final KeyService keyService;

    @PostMapping("/keys/{username}")
    public ResponseEntity<CreatedKeyResponse> create(@PathVariable("username") String username, @RequestBody KeyRequestDTO keyRequestDTO) {
        CreatedKeyResponse createdKeyResponse = keyService.addKey(username, keyRequestDTO);
        return new ResponseEntity<>(createdKeyResponse, HttpStatus.OK);
    }

    @PostMapping("/apis/keys/basic/{username}")
    public ResponseEntity<CreatedKeyResponse> createBasicAuth(@PathVariable("username") String username, @RequestBody KeyRequestDTO keyRequestDTO) {
        CreatedKeyResponse createdKeyResponse = keyService.addKey(username, keyRequestDTO);
        return new ResponseEntity<>(createdKeyResponse, HttpStatus.OK);
    }

    @GetMapping("/keys")
    public ResponseEntity<KeyResponseDTO> getKeyList() {
        return new ResponseEntity<>(keyService.getList(), HttpStatus.OK);
    }

    @DeleteMapping("/key/{key-id}")
    public ResponseEntity<KeyResponseDTO> deleteKey(@PathVariable("key-id") UUID keyId) {
        keyService.delete(keyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
