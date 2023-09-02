package isthatkirill.main.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import isthatkirill.main.user.dto.UserDto;
import isthatkirill.main.user.service.UserServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserAdminController.class)
class UserAdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;

    private UserDto userDto;

    @BeforeEach
    void buildUser() {
        userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@email.ru")
                .build();
    }

    @Test
    @SneakyThrows
    void createTest() {
        when(userService.create(any())).thenReturn(userDto);

        mvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        verify(userService, times(1)).create(userDto);
    }

    @Test
    @SneakyThrows
    void createWithInvalidEmailTest() {
        userDto.setEmail("email");
        when(userService.create(any())).thenReturn(userDto);

        mvc.perform(post("/admin/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(userService, never()).create(userDto);
    }

    @Test
    @SneakyThrows
    void deleteTest() {
        Long userId = 1L;

        mvc.perform(delete("/admin/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(userService, times(1)).delete(userId);
    }

    @Test
    @SneakyThrows
    void getUsersTest() {
        when(userService.getUsers(anyList(), anyInt(), anyInt()))
                .thenReturn(List.of(userDto));

        Integer from = 0;
        Integer size = 4;
        List<Long> ids = Collections.singletonList(1L);

        mvc.perform(get("/admin/users")
                        .param("ids", String.valueOf(ids.get(0)))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$[0].email").value(userDto.getEmail()));

        verify(userService, times(1)).getUsers(ids, from, size);
    }

    @Test
    @SneakyThrows
    void getUsersWithInvalidParamsTest() {
        when(userService.getUsers(anyList(), anyInt(), anyInt()))
                .thenReturn(List.of(userDto));

        Integer from = Integer.MIN_VALUE;
        Integer size = 10;
        List<Long> ids = Collections.singletonList(1L);

        mvc.perform(get("/admin/users")
                        .param("ids", String.valueOf(ids.get(0)))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Incorrectly made request"));

        verify(userService, never()).getUsers(anyList(), anyInt(), anyInt());
    }
}