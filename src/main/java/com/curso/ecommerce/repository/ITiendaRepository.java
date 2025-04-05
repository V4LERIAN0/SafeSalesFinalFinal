package com.curso.ecommerce.repository;
import java.util.*;

import com.curso.ecommerce.model.Tienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITiendaRepository extends JpaRepository<Tienda, Integer> {
    List<Tienda> findByOwnerId(Integer ownerId);
}
