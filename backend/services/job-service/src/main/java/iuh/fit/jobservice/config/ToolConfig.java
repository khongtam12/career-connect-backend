package iuh.fit.jobservice.config;

import iuh.fit.jobservice.service.JobService;
import iuh.fit.jobservice.tools.JobTools;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolConfig {

    @Bean
    public JobTools jobTools(JobService jobService) {
        return new JobTools(jobService);
    }
}
