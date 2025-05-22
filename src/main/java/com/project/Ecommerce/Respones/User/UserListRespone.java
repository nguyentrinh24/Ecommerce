package com.project.Ecommerce.Respones.User;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor

public class UserListRespone {
    private List<UserResponses> users;
    private int totalPages;
}
