package org.example.caffe.controller;

import lombok.RequiredArgsConstructor;
import org.example.caffe.domain.User;
import org.example.caffe.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    final private UserRepository userRepository;

    @PostMapping("/user/save")
    public User saveUser(@RequestBody User user){
        return userRepository.save(user);
    }
}
