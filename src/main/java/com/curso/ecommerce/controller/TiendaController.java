package com.curso.ecommerce.controller;

import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Tienda;
import com.curso.ecommerce.model.Usuario;
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
		tienda.setUsuario(u);
		
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
		tienda.setUsuario(p.getUsuario());
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
	
	
}
