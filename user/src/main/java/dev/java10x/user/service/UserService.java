package dev.java10x.user.service;

import dev.java10x.user.domain.UserModel;
import dev.java10x.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserModel createUser(UserModel userModel) {
        return userRepository.save(userModel);
    }


}
