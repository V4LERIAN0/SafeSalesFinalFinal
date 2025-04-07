package com.curso.ecommerce.controller;

import com.curso.ecommerce.service.buscar.BuscarImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BuscarController {


    @Autowired
    private BuscarImpl buscarImpl;

            @PostMapping("/buscar")
    public String buscar(@RequestParam("nombre") String nombre, Model model) {
        BuscarImpl.ResultadoBusqueda resultado = buscarImpl.buscar(nombre);

                    model.addAttribute("productos", resultado.getProductos());
        model.addAttribute("tiendas", resultado.getTiendas());
        return "usuario/buscarShow";
    }
}