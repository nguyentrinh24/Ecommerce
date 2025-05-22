package com.project.Ecommerce.Service;

import com.project.Ecommerce.Component.JwtUtil;
import com.project.Ecommerce.DTOs.UserDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Model.Role;
import com.project.Ecommerce.Model.User;
import com.project.Ecommerce.Repository.RoleRepository;
import com.project.Ecommerce.Repository.UserRepository;
import com.project.Ecommerce.Service.Iml.UserServiceIml;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
//DI
@RequiredArgsConstructor
public class UserService implements UserServiceIml {


    private final UserRepository  userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    //register
    public User createUser(UserDTOs user) throws DataNotFoundException {
        // check phone number
        String phoneNumber = user.getPhoneNumber();

        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role Not Found"));

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

        newuser.setRoleId(role);
        //check user login used Oauth co account_id ? yeu cau pass
        if(user.getFacebookAccountId()==0 || user.getGoogleAccountId()==0)
        {
            String password = user.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            //update pass after bcrypt
            newuser.setPassword(encodedPassword);
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
      Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
      if(user.isEmpty())
      {
          try {
              throw new DataNotFoundException("user password invalid");
          } catch (DataNotFoundException e) {
              throw new RuntimeException(e);
          }
      }
      //return optional user.get
      User exitsUser = user.get();
      //check pass
        if(exitsUser.getGg_account_id()==0 || exitsUser.getFb_account_id()==0)
        {
            if(!passwordEncoder.matches(password,exitsUser.getPassword()))
            {
                throw new BadCredentialsException("Wrong password");
            }
        }
      //take out username/pass
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(phoneNumber, password);

      //authentication with security ->username/pass
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

      return jwtUtil.generateToken(exitsUser);// jwt

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
