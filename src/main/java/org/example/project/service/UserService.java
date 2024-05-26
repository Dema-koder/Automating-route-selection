package org.example.project.service;


import org.example.project.domain.Users;
import org.example.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void removeUser(Users users) {
        userRepository.delete(users);
    }

    public Users addUser(Long chatId, String name) {
        Users users = new Users();
        users.setName(name);
        users.setChatId(chatId);
        return userRepository.save(users);
    }

    public Users getUserByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public Users getUserByName(String name) {
        return userRepository.findByName(name);
    }
}
