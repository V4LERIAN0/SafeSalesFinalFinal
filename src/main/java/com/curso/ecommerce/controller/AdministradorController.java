package com.curso.ecommerce.controller;

import java.util.List;

import com.curso.ecommerce.exception.ResourceNotFoundException;
import com.curso.ecommerce.model.Tienda;
import com.curso.ecommerce.service.TiendaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.service.IOrdenService;
import com.curso.ecommerce.service.IUsuarioService;
import com.curso.ecommerce.service.ProductoService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

//	@Autowired
//	private ProductoService productoService;

	@Autowired
	private TiendaService tiendaService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordensService;
	
	private Logger logg= LoggerFactory.getLogger(AdministradorController.class);

	@GetMapping("")
	public String home(Model model, HttpSession session) {
		int idUsuario = Integer.parseInt(session.getAttribute("idusuario").toString());
		List<Tienda> tiendas = tiendaService.findByOwnerId(idUsuario);
		model.addAttribute("tiendas", tiendas);
		return "administrador/home";
	}

	@GetMapping("/ordenes")
	public String ordenes(Model model) {
		model.addAttribute("ordenes", ordensService.findAll());
		return "administrador/ordenes";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalle(Model model, @PathVariable Integer id) {
		logg.info("Id de la orden {}",id);
		Orden orden= ordensService.findById(id).get();
		
		model.addAttribute("detalles", orden.getDetalle());
		
		return "administrador/detalleorden";
	}

	@GetMapping("/admintienda/{id}")
	public String adminTienda(@PathVariable("id") Integer id, Model model) {
		Tienda tienda = tiendaService.get(id)
				.orElseThrow(() -> new ResourceNotFoundException("Tienda not found"));
		List<Producto> productos = tienda.getProductos();
		model.addAttribute("tienda", tienda);
		model.addAttribute("productos", productos);
		return "tiendas/productos";
	}

}
