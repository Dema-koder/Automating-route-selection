package org.example.project.service;

import org.example.project.domain.Users;
import org.example.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRemoveUser() {
        Users user = new Users();
        doNothing().when(userRepository).delete(user);

        userService.removeUser(user);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testAddUser() {
        Long chatId = 12345L;
        String name = "John Doe";
        Users user = new Users();
        user.setChatId(chatId);
        user.setName(name);

        when(userRepository.save(any(Users.class))).thenReturn(user);

        Users result = userService.addUser(chatId, name);

        assertNotNull(result);
        assertEquals(chatId, result.getChatId());
        assertEquals(name, result.getName());
        verify(userRepository, times(1)).save(any(Users.class));
    }

    @Test
    void testGetUserByChatId() {
        Long chatId = 12345L;
        Users user = new Users();
        user.setChatId(chatId);

        when(userRepository.findByChatId(chatId)).thenReturn(user);

        Users result = userService.getUserByChatId(chatId);

        assertNotNull(result);
        assertEquals(chatId, result.getChatId());
        verify(userRepository, times(1)).findByChatId(chatId);
    }

    @Test
    void testGetUserByName() {
        String name = "John Doe";
        Users user = new Users();
        user.setName(name);

        when(userRepository.findByName(name)).thenReturn(user);

        Users result = userService.getUserByName(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(userRepository, times(1)).findByName(name);
    }
}