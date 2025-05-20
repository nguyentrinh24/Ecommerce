package com.project.Ecommerce.Service;

import com.project.Ecommerce.DTOs.UserDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Model.User;
import com.project.Ecommerce.Repository.UserRepository;
import com.project.Ecommerce.Service.Iml.UserServiceIml;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.OptionalInt;


@Service
//DI
@RequiredArgsConstructor
public class UserService implements UserServiceIml {
    private final UserRepository  userRepository;

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    //register
    public User createUser(UserDTOs user) throws DataNotFoundException {
        // check phone number
        String phoneNumber = user.getPhoneNumber();

        if(userRepository.findByPhoneNumber(phoneNumber).isPresent() )
        {
            throw  new DataNotFoundException("Phone number used");
        }
        //convert DTOs-> DTO
        User newuser = User.builder()
                .fullName(user.getFullName())
                .phoneNumber(String.valueOf(user.getPhoneNumber()))
                .password(user.getPassword())
                .address(user.getAddress())
                .date_brith(user.getDateOfBirth())
                .fb_account_id(user.getFacebookAccountId())
                .gg_account_id(user.getGoogleAccountId())
                .is_active(true)
                .build();

        //check user login used Oauth
        if(user.getFacebookAccountId()==0 || user.getGoogleAccountId()==0)
        {
            String password = user.getPassword();
        }

        newuser = userRepository.save(newuser);

        return userRepository.save(newuser);
    }

    @Override
    public User updateUser(Long id, UserDTOs user) {
        return null;
    }

    @Override
    public User deleteUser(Long id) {
        return null;
    }

    @Override
    public Page<User> findAllUsers(String key, Pageable pageable) {
        return null;
    }

    @Override
    public String login(String phoneNumber, String password) {
        return "";
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return Optional.empty();
    }


}
