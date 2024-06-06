package com.billybang.userservice.controller;

import com.billybang.userservice.api.DemoApi;
import com.billybang.userservice.dto.request.DemoRequestDto;
import com.billybang.userservice.dto.response.DemoResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DemoController implements DemoApi {

    @Override
    public ResponseEntity<DemoResponseDto> demo(DemoRequestDto requestDto) {
        return ResponseEntity.ok(null);
    }
}
