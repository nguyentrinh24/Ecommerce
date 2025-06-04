package com.project.Ecommerce.Respones.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.Ecommerce.Model.Role;
import com.project.Ecommerce.Model.User;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserResponses {
    @JsonProperty("id")
    private  Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("fullname")
    private  String fullName;

    @JsonProperty("password")
    private String password;

    @JsonProperty("phonenumber")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("is_active")
    private boolean is_active;

    @JsonProperty("date_brith")
    private Date date_brith;

    @JsonProperty("fb_account_id")
    private int fb_account_id;

    @JsonProperty("gg_account_id")
    private  int gg_account_id;

    @JsonProperty("roleId")
    private Role roleId;
    public static UserResponses fromUser(User user) {
        return UserResponses.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .is_active(user.is_active())
                .date_brith(user.getDate_brith())
                .fb_account_id(user.getFb_account_id())
                .gg_account_id(user.getGg_account_id())
                .roleId(user.getRoleId())
                .build();
    }

}
