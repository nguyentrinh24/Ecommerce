package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.SocialNetwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SocialNetworkRepository extends JpaRepository<SocialNetwork,Long> {
}
