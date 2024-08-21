package org.adaschool.api.controller.user;


import org.adaschool.api.exception.UserNotFoundException;
import org.adaschool.api.repository.user.User;
import org.adaschool.api.repository.user.UserDto;
import org.adaschool.api.service.user.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/users/")
public class UsersController {

    private final UsersService usersService;

    public UsersController(@Autowired UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDto userDto) {
        User createdUser = usersService.save(new User(userDto));
        URI createdUserUri = URI.create("/v1/users/" + createdUser.getId());
        return ResponseEntity.created(createdUserUri).body(createdUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(usersService.all());
    }

    @GetMapping("{id}")
    public ResponseEntity<User> findById(@PathVariable("id") String id) {
        return usersService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody UserDto userDto) {
        return usersService.findById(id)
                .map(user -> {
                    user.update(userDto);
                    return ResponseEntity.ok(usersService.save(user));
                })
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) {
        return usersService.findById(id)
                .map(user -> {
                    usersService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}