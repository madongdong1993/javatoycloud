package org.javatoy.provider;

import org.apache.catalina.User;
import org.javatoy.commons.UserTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    @Value( "${server.port}" )
    Integer port;

    @GetMapping("/hello")
    public String hello(){
        return "hello boy" + port;
    }

    @GetMapping("/hello2")
    public String hello2(String name){
        return "hello boy" + name;
    }

    // 以  key / value 形式进行传参
    @PostMapping("/user1")
    public UserTest addUser1(UserTest user){
        return user;
    }

    // 以json方式进行传参
    @PostMapping("/user2")
    public UserTest addUser2(@RequestBody  UserTest user){
        return user;
    }

    @PutMapping("/user3")
    public void updateUser3(UserTest userTest){

    }

    @PutMapping("/user4")
    public void updateUser4(@RequestBody UserTest userTest){

    }

    @DeleteMapping("/user5")
    public void deleteUser5( Integer  id ){

    }

    @DeleteMapping("/user6")
    public void deleteUserTest6(@PathVariable Integer id){

    }
}
