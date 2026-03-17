package iuh.fit.applicationservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/apply")
public class JobApplicationController {
    @GetMapping
    public String apply() {
        return "Job Application Service";
    }
}
