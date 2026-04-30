package iuh.fit.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.userservice.dto.request.RecruiterRequestDTO;
import iuh.fit.userservice.dto.response.RecruiterPageDTO;
import iuh.fit.userservice.dto.response.RecruiterResponseDTO;
import iuh.fit.userservice.model.RecruiterStatus;
import iuh.fit.userservice.service.RecruiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RecruiterController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for unit test
public class RecruiterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecruiterService recruiterService;

    @Autowired
    private ObjectMapper objectMapper;

    private RecruiterRequestDTO requestDTO;
    private RecruiterResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new RecruiterRequestDTO();
        requestDTO.setUsername("john_doe");
        requestDTO.setEmail("john@example.com");
        requestDTO.setCompanyId("COMP-123");
        requestDTO.setStatus(RecruiterStatus.ACTIVE);

        responseDTO = new RecruiterResponseDTO();
        responseDTO.setId("REC-123");
        responseDTO.setUsername("john_doe");
        responseDTO.setEmail("john@example.com");
        responseDTO.setCompanyName("Company COMP-123");
        responseDTO.setStatus(RecruiterStatus.ACTIVE);
    }

    @Test
    void getAllRecruiters_ShouldReturn200() throws Exception {
        RecruiterPageDTO pageDTO = new RecruiterPageDTO(Collections.singletonList(responseDTO), 0, 10, 1);
        Mockito.when(recruiterService.getAllRecruiters(anyInt(), anyInt())).thenReturn(pageDTO);

        mockMvc.perform(get("/api/v1/admin/recruiters")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content[0].username").value("john_doe"));
    }

    @Test
    void createRecruiter_ShouldReturn201() throws Exception {
        Mockito.when(recruiterService.createRecruiter(any(RecruiterRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/admin/recruiters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk()) // Controller returns 200 OK wrapper with 201 status inside ApiResponse
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.username").value("john_doe"));
    }

    @Test
    void createRecruiter_WhenEmailExists_ShouldReturn400() throws Exception {
        Mockito.when(recruiterService.createRecruiter(any(RecruiterRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Email đã tồn tại"));

        mockMvc.perform(post("/api/v1/admin/recruiters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                // Note: The actual status depends on how GlobalExceptionHandler maps IllegalArgumentException.
                // Normally it maps to 400 Bad Request.
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRecruiter_ShouldReturn200() throws Exception {
        Mockito.when(recruiterService.updateRecruiter(eq("REC-123"), any(RecruiterRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/admin/recruiters/REC-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.username").value("john_doe"));
    }

    @Test
    void updateRecruiter_WhenNotFound_ShouldReturn400Or404() throws Exception {
        Mockito.when(recruiterService.updateRecruiter(eq("REC-999"), any(RecruiterRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Không tìm thấy"));

        mockMvc.perform(put("/api/v1/admin/recruiters/REC-999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest()); // IllegalArgumentException -> 400
    }

    @Test
    void patchStatus_ShouldReturn200() throws Exception {
        responseDTO.setStatus(RecruiterStatus.INACTIVE);
        Mockito.when(recruiterService.changeStatus(eq("REC-123"), eq(RecruiterStatus.INACTIVE)))
                .thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/admin/recruiters/REC-123/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }
}
