package com.curso.ecommerce.repository;


import com.curso.ecommerce.model.Tienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITiendaRepository extends JpaRepository<Tienda, Integer> {
    List<Tienda> findByUsuarioId(Integer idUsuario);

}
