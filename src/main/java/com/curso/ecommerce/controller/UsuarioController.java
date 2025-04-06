package com.curso.ecommerce.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.curso.ecommerce.exception.ResourceNotFoundException;
import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Tienda;
import com.curso.ecommerce.service.ProductoService;
import com.curso.ecommerce.service.TiendaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.curso.ecommerce.model.Orden;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.IOrdenService;
import com.curso.ecommerce.service.IUsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	
	private final Logger logger= LoggerFactory.getLogger(UsuarioController.class);
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private TiendaService TiendaService;

	@Autowired
	private ProductoService productoService;
	
	BCryptPasswordEncoder passEncode= new BCryptPasswordEncoder();
	
	
	// /usuario/registro
	@GetMapping("/registro")
	public String create() {
		return "usuario/registro";
	}

	@PostMapping("/save")
	public String save(Usuario usuario) {
		logger.info("Usuario registro: {}", usuario);
		usuario.setPassword( passEncode.encode(usuario.getPassword()));
		usuarioService.save(usuario);
		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {
		return "usuario/login";
	}

	@GetMapping("/acceder")
	public String acceder(Usuario usuario, HttpSession session) {
		logger.info("Accesos : {}", usuario);

		Optional<Usuario> user = usuarioService.findById(
				Integer.parseInt(session.getAttribute("idusuario").toString())
		);

		if (user.isPresent()) {
			session.setAttribute("idusuario", user.get().getId());
			if (user.get().getTipo().equals("ADMIN")) {
				return "redirect:/administrador";
			} else {
				// Redirige a los que no son admin a la vista no admin
				return "redirect:/usuario";
			}
		} else {
			logger.info("Usuario no existe");
		}

		return "redirect:/";
	}

	@GetMapping("")
	public String dashboard() {
		return "usuario/dashboard";
	}


	@GetMapping("/compras")
	public String obtenerCompras(Model model, HttpSession session) {
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		Usuario usuario= usuarioService.findById(  Integer.parseInt(session.getAttribute("idusuario").toString()) ).get();
		List<Orden> ordenes= ordenService.findByUsuario(usuario);
		logger.info("ordenes {}", ordenes);
		
		model.addAttribute("ordenes", ordenes);
		
		return "usuario/compras";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalleCompra(@PathVariable Integer id, HttpSession session, Model model) {
		logger.info("Id de la orden: {}", id);
		Optional<Orden> orden=ordenService.findById(id);
		
		model.addAttribute("detalles", orden.get().getDetalle());
		
		
		//session
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		return "usuario/detallecompra";
	}
	
	@GetMapping("/cerrar")
	public String cerrarSesion( HttpSession session ) {
		session.removeAttribute("idusuario");
		return "redirect:/";
	}

	@GetMapping("/tiendas")
	public String showStores(Model model) {
		List<Tienda> tiendas = TiendaService.findAll();
		model.addAttribute("tiendas", tiendas);
		return "usuario/tiendas";
	}

	@GetMapping("/tiendas/{tiendaId}/productos")
	public String showStoreProducts(@PathVariable("tiendaId") Integer tiendaId, Model model) {
		Tienda tienda = TiendaService.get(tiendaId)
				.orElseThrow(() -> new ResourceNotFoundException("Tienda not found"));
		List<Producto> productos = tienda.getProductos();
		model.addAttribute("tienda", tienda);
		model.addAttribute("productos", productos);
		return "usuario/productos_tiendas";
	}

	@GetMapping("/productos")
	public String allProducts(Model model) {
		model.addAttribute("productos", productoService.findAll());
		return "usuario/productos";
	}
}
