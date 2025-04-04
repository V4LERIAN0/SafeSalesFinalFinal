package com.curso.ecommerce.service;

import com.curso.ecommerce.model.Tienda;
import java.util.List;
import java.util.Optional;

public interface TiendaService {
	public Tienda save( Tienda tienda);
	public Optional<Tienda> get(Integer id);
	public void update(Tienda tienda);
	public void delete(Integer id);
	public List<Tienda> findAll();
	List<Tienda> findByUsuarioId(Integer idUsuario);

}
