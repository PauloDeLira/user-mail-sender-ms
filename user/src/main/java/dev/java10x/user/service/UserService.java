package dev.java10x.user.service;

import dev.java10x.user.domain.UserModel;
import dev.java10x.user.producer.UserProducer;
import dev.java10x.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProducer userProducer;

    public UserService(UserRepository userRepository, UserProducer userProducer) {
        this.userRepository = userRepository;
        this.userProducer = userProducer;
    }

    @Transactional
    public UserModel createUser(UserModel userModel) {
        if(userRepository.findByEmail(userModel.getEmail()).isPresent()){
            throw new IllegalArgumentException("User with email " + userModel.getEmail() + " already exists");
        }

        UserModel savedUser = userRepository.save(userModel);

        userProducer.sendUserCreatedEvent(savedUser);

        return savedUser;
    }


}
