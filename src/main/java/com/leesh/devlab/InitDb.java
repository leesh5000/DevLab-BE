package com.leesh.devlab;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class InitDb implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
    }
}
