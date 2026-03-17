package iuh.fit.paymentservice.controller;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobpackage")
public class JobPackageController {
    @GetMapping
    public String  test (){
        return "job package service is running";
    }
}
