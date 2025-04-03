package com.curso.ecommerce.service;


import com.curso.ecommerce.model.Tienda;
import com.curso.ecommerce.repository.ITiendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TiendaServiceImpl implements TiendaService {

@Autowired
private ITiendaRepository tiendaRepository;


	@Override
	public Tienda save(Tienda tienda) {
		return tiendaRepository.save(tienda);
	}

	@Override
	public Optional<Tienda> get(Integer id) {
		return tiendaRepository.findById(id);
	}

	@Override
	public void update(Tienda tienda) {
		tiendaRepository.save(tienda);

	}

	@Override
	public void delete(Integer id) {
		tiendaRepository.deleteById(id);

	}

	@Override
	public List<Tienda> findAll() {
		return tiendaRepository.findAll();
	}
}
