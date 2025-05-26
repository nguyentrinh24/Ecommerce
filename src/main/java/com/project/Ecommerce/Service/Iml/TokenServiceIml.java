package com.project.Ecommerce.Service.Iml;

import com.project.Ecommerce.Model.Role;
import com.project.Ecommerce.Model.Token;
import com.project.Ecommerce.Model.User;

public interface TokenServiceIml {
    Token addToken(User user, String token, Role role);

}
