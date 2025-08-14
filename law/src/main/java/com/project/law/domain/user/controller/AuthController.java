package com.project.law.domain.user.controller;

import com.project.law.domain.user.dto.request.LocalJoinRequestDto;
import com.project.law.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<?> localJoin(@RequestBody @Valid LocalJoinRequestDto dto){
        authService.localJoin(dto);
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }
}
