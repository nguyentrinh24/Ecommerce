package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface CategoriRepository  extends JpaRepository <Category,Long> {
}
