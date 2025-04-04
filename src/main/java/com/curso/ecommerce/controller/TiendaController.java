package com.curso.ecommerce.controller;

import com.curso.ecommerce.exception.ResourceNotFoundException;
import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Tienda;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.repository.IProductoRepository;
import com.curso.ecommerce.repository.ITiendaRepository;
import com.curso.ecommerce.service.IUsuarioService;
import com.curso.ecommerce.service.ProductoService;
import com.curso.ecommerce.service.TiendaService;
import com.curso.ecommerce.service.UploadFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.Optional;

@Controller
@RequestMapping("/tiendas")
public class TiendaController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(TiendaController.class);
	
	@Autowired
	private TiendaService tiendaService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private UploadFileService upload;
    @Autowired
    private ProductoService productoService;

	@GetMapping("")
	public String show(Model model) {
		model.addAttribute("tiendas", tiendaService.findAll());
		return "tiendas/show";
	}
	
	@GetMapping("/create")
	public String create() {
		return "tiendas/create";
	}
	
	@PostMapping("/save")
	public String save(Tienda tienda, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
		LOGGER.info("Este es el objeto producto {}",tienda);
		
		
		Usuario u= usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString() )).get();
		tienda.setOwner(u);
		
		//imagen
		if (tienda.getId()==null) { // cuando se crea un producto
			String nombreImagen= upload.saveImage(file);
			tienda.setImagen(nombreImagen);
		}else {
			
		}
		
		tiendaService.save(tienda);
		return "redirect:/tiendas";
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		Tienda tienda= new Tienda();
		Optional<Tienda> optionalTienda=tiendaService.get(id);
		tienda= optionalTienda.get();

		LOGGER.info("tienda buscado: {}",tienda);
		model.addAttribute("tienda", tienda);

		return "tiendas/edit";
	}

	@PostMapping("/update")
	public String update(Tienda tienda, @RequestParam("img") MultipartFile file ) throws IOException {
		Tienda p= new Tienda();
		p=tiendaService.get(tienda.getId()).get();

		if (file.isEmpty()) { // editamos el producto pero no cambiamos la imagem

			tienda.setImagen(p.getImagen());
		}else {// cuando se edita tbn la imagen
			//eliminar cuando no sea la imagen por defecto
			if (!p.getImagen().equals("default.jpg")) {
				upload.deleteImage(p.getImagen());
			}
			String nombreImagen= upload.saveImage(file);
			tienda.setImagen(nombreImagen);
		}
		tienda.setOwner(p.getOwner());
		tiendaService.update(tienda);
		return "redirect:/productos";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {

		Tienda p = new Tienda();
		p=tiendaService.get(id).get();

		//eliminar cuando no sea la imagen por defecto
		if (!p.getImagen().equals("default.jpg")) {
			upload.deleteImage(p.getImagen());
		}

		tiendaService.delete(id);
		return "redirect:/Tiendas";
	}

	@GetMapping("/{tiendaId}/productos")
	public String showTiendaProducts(@PathVariable("tiendaId") Integer tiendaId, Model model) {
		Tienda tienda = tiendaService.get(tiendaId)
				.orElseThrow(() -> new ResourceNotFoundException("Tienda not found"));

		List<Producto> productos = tienda.getProductos(); // Assuming the relationship is set up

		model.addAttribute("tienda", tienda);
		model.addAttribute("productos", productos);
		return "tiendas/productos"; // Make sure this Thymeleaf template exists
	}

	// In TiendaController.java

	// Display the product creation form for a given store
	@GetMapping("/{tiendaId}/productos/new")
	public String showProductForm(@PathVariable("tiendaId") Integer tiendaId, Model model) {
		Tienda tienda = tiendaService.get(tiendaId)
				.orElseThrow(() -> new ResourceNotFoundException("Tienda not found"));
		model.addAttribute("tienda", tienda);
		model.addAttribute("producto", new Producto());
		return "productos/form";  // Ensure you have this template or adjust the name accordingly
	}

	// Process the product creation form submission
	@PostMapping("/{tiendaId}/productos")
	public String saveProduct(@PathVariable("tiendaId") Integer tiendaId, @ModelAttribute Producto producto, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
		// Retrieve the store from the tiendaService
		Tienda tienda = tiendaService.get(tiendaId)
				.orElseThrow(() -> new ResourceNotFoundException("No se encontró la tienda"));

		// Associate the product with this store
		producto.setTienda(tienda);

		// Optionally, set the user (if needed):
		// Usuario usuario = usuarioService.findById(...).get();
		// producto.setUsuario(usuario);

		// Save the image if necessary (using your UploadFileService)
		if (!file.isEmpty()) {
			String nombreImagen = upload.saveImage(file);
			producto.setImagen(nombreImagen);
		}

		// Save the product (using your existing ProductoService or repository)
		productoService.save(producto);

		// Redirect back to the store's products list
		return "redirect:/tiendas/" + tiendaId + "/productos";
	}

}
