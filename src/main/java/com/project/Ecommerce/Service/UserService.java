package com.project.Ecommerce.Service;

import com.project.Ecommerce.Component.JwtUtil;
import com.project.Ecommerce.DTOs.UserDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Exceptions.ExpiredTokenException;
import com.project.Ecommerce.Exceptions.PermissionDenyException;
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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public User findById(Long id) {
        return null;
    }

    @Override
    @Transactional
    //register
    public User createUser(UserDTOs user) throws Exception {
        // check phone number
        String phoneNumber = user.getPhoneNumber();

        Role role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role Not Found"));

        if(userRepository.findByPhoneNumber(phoneNumber).isPresent() )
        {
            throw  new DataNotFoundException("Phone number used");
        }
        if(role.getName().equals("ADMIN") )
        {
            throw  new PermissionDenyException("You can not register account with ADMIN");
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
    @Transactional
    public User updateUser(Long id, UserDTOs userDTO) {
        User existingUser = null;
        try {
            existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("User not found with ID: " + id));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Lấy role mới nếu có truyền
        if (userDTO.getRoleId() != null) {
            Role role = null;
            try {
                role = roleRepository.findById(userDTO.getRoleId())
                        .orElseThrow(() -> new DataNotFoundException("Role Not Found"));
            } catch (DataNotFoundException e) {
                throw new RuntimeException(e);
            }

            if (role.getName().equals("ADMIN")) {
                try {
                    throw new PermissionDenyException("Không được cập nhật role lên ADMIN");
                } catch (PermissionDenyException e) {
                    throw new RuntimeException(e);
                }
            }

            existingUser.setRoleId(role);
        }

        // Cập nhật số điện thoại nếu thay đổi
        if (userDTO.getPhoneNumber() != null &&
                !userDTO.getPhoneNumber().isBlank() &&
                !userDTO.getPhoneNumber().equals(existingUser.getPhoneNumber())) {

            if (userRepository.findByPhoneNumber(userDTO.getPhoneNumber()).isPresent()) {
                try {
                    throw new DataNotFoundException("Phone number already in use");
                } catch (DataNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        }

        // Cập nhật các field còn lại nếu có giá trị mới
        if (userDTO.getFullName() != null && !userDTO.getFullName().isBlank()) {
            existingUser.setFullName(userDTO.getFullName());
        }

        if (userDTO.getAddress() != null && !userDTO.getAddress().isBlank()) {
            existingUser.setAddress(userDTO.getAddress());
        }

        if (userDTO.getDateOfBirth() != null) {
            existingUser.setDate_brith(userDTO.getDateOfBirth());
        }

        // Với int cần đổi sang Integer trong DTO để tránh lỗi null check
        if (userDTO.getFacebookAccountId() != null && userDTO.getFacebookAccountId() != 0) {
            existingUser.setFb_account_id(userDTO.getFacebookAccountId());
        }

        if (userDTO.getGoogleAccountId() != null && userDTO.getGoogleAccountId() != 0) {
            existingUser.setGg_account_id(userDTO.getGoogleAccountId());
        }

        // Kiểm tra nếu password khác thì mã hoá lại
        if (userDTO.getPassword() != null &&
                !userDTO.getPassword().isBlank() &&
                !passwordEncoder.matches(userDTO.getPassword(), existingUser.getPassword())) {

            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public User deleteUser(Long id) {
        return null;
    }

    @Override
    @Transactional
    public Page<User> findAllUsers(String key, Pageable pageable) {
        return null;
    }

    @Override
    @Transactional
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
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(phoneNumber, password,exitsUser.getAuthorities());

      //authentication with security ->username/pass
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

      return jwtUtil.generateToken(exitsUser);// jwt

    }



    @Override
    @Transactional
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    @Transactional
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return Optional.empty();
    }

    // lấy ra user_detail
    @Override
    public User getUserDetailsFromToken(String token) throws ExpiredTokenException {
        if(jwtUtil.checkExpiration(token))
        {
            throw new ExpiredTokenException("Token is expired");
        }
        String phoneNumber = jwtUtil.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if(user.isPresent())
        {
            return user.get();
        }
        else {
            try {
                throw new Exception("User not found");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public User validateUser(String phone, String password) {
        User user = userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Sai mật khẩu");
        }

        return user;
    }

    public String generateToken(User user) {
        return jwtUtil.generateToken(user);
    }




}
