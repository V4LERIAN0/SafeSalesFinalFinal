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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
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
	public String show(Model model, HttpSession session) {
		int idUsuario = Integer.parseInt(session.getAttribute("idusuario").toString());
		List<Tienda> tiendas = tiendaService.findByOwnerId(idUsuario);
		model.addAttribute("tiendas", tiendas);
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
	public String edit(@PathVariable Integer id, Model model, HttpSession session) throws AccessDeniedException {
		Tienda tienda = tiendaService.get(id)
				.orElseThrow(() -> new ResourceNotFoundException("Tienda not found"));

		int idUsuario = Integer.parseInt(session.getAttribute("idusuario").toString());
		if (!tienda.getOwner().getId().equals(idUsuario)) {
			// You can throw an AccessDeniedException or handle it as you prefer
			throw new AccessDeniedException("No tienes permiso para editar esta tienda");
		}

		model.addAttribute("tienda", tienda);
		return "tiendas/edit";
	}

	@PostMapping("/update")
	public String update(Tienda tienda, @RequestParam("img") MultipartFile file ) throws IOException {
		// Consigue los datos actuales de la tienda
		Tienda p = tiendaService.get(tienda.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Tienda not found"));

		// Si no hay imagen nueva, mantiene la vieja
		if (file.isEmpty()) {
			tienda.setImagen(p.getImagen());
		} else {
			// Si hay una imagen nueva que no es la original, la borra
			if (p.getImagen() != null && !p.getImagen().equals("default.jpg")) {
				upload.deleteImage(p.getImagen());
			}
			String nombreImagen = upload.saveImage(file);
			tienda.setImagen(nombreImagen);
		}

		tienda.setOwner(p.getOwner());
		tienda.setProductos(p.getProductos());

		// Actualiza la base de datos
		tiendaService.update(tienda);

		return "redirect:/tiendas";
	}


	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
		// consigue la tienda
		Tienda tienda = tiendaService.get(id)
				.orElseThrow(() -> new ResourceNotFoundException("Tienda not found"));

		// Revisa si eres el propietario de la tienda a borrar
		int idUsuario = Integer.parseInt(session.getAttribute("idusuario").toString());
		if (!tienda.getOwner().getId().equals(idUsuario)) {
			redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar esta tienda");
			return "redirect:/tiendas";
		}

		// Revisa si algún producto de la tienda está en los detalles de orden
		for (Producto prod : tienda.getProductos()) {
			if (prod.getDetalles() != null && !prod.getDetalles().isEmpty()) {
				// Tira el error tipo "flash"
				redirectAttributes.addFlashAttribute("error", "No se puede eliminar la tienda porque algunos productos están en órdenes.");
				return "redirect:/tiendas";
			}
		}

		// Borra la imagen
		if (tienda.getImagen() != null && !tienda.getImagen().equals("default.jpg")) {
			upload.deleteImage(tienda.getImagen());
		}

		// Elimina la tienda y si no se puede maneja los errores
		try {
			tiendaService.delete(id);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error al eliminar la tienda: " + e.getMessage());
			return "redirect:/tiendas";
		}
		return "redirect:/tiendas";
	}

	@GetMapping("/{tiendaId}/productos")
	public String showTiendaProducts(@PathVariable("tiendaId") Integer tiendaId, Model model, HttpSession session)
			throws AccessDeniedException {
		Tienda tienda = tiendaService.get(tiendaId)
				.orElseThrow(() -> new ResourceNotFoundException("Tienda not found"));

		int idUsuario = Integer.parseInt(session.getAttribute("idusuario").toString());
		boolean preview = tienda.getOwner().getId().equals(idUsuario);

		model.addAttribute("tienda", tienda);
		model.addAttribute("productos", tienda.getProductos());
		model.addAttribute("preview", preview); // cierto si el usuario logeado es el dueño

		return "usuario/productos_tiendas";
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
