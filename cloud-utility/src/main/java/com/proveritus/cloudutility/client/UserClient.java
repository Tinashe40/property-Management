package com.proveritus.cloudutility.client;

import com.proveritus.cloudutility.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/users/by-username")
    UserDTO getUserByUsername(@RequestParam("username") String username);

    @PostMapping("/api/users/by-ids")
    List<UserDTO> getUsersByIds(@RequestBody List<Long> ids);
}
