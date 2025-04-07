package com.curso.ecommerce.service.buscar;

import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Tienda;
import com.curso.ecommerce.repository.IProductoRepository;
import com.curso.ecommerce.repository.ITiendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;import java.util.*;
import java.util.stream.Collectors;

@Service
public class BuscarImpl {

    @Autowired
    private IProductoRepository productoRepository;

    @Autowired
    private ITiendaRepository tiendaRepository;

    private static final Set<String> palabrasCortas = Set.of(
            "de", "la", "el", "en", "y", "a", "para", "por", "con",
            "una", "un", "los", "las", "al", "del"
    );

    private static final List<String> palabrasCaro = List.of(
            "caro", "costoso", "lujoso", "exclusivo", "premium",
            "alto precio", "de lujo", "carísima", "carísimo",
            "precio alto", "gama alta", "top", "élite"
    );

    private static final List<String> palabrasBarato = List.of(
            "barato", "económico", "económica", "accesible",
            "asequible", "rebajado", "oferta", "promoción",
            "descuento", "lowcost", "low-cost", "ganga", "chollo",
            "precio bajo", "precio económico", "bueno bonito barato"
    );

    public List<String> palabrasClave(String frase) {
        return Arrays.stream(frase.toLowerCase().split("\\s+"))
                .map(String::trim)
                .filter(palabra -> !palabrasCortas.contains(palabra))
                .filter(palabra -> palabra.length() > 2)
                .collect(Collectors.toList());
    }

    public String detectarFiltroPrecio(String frase) {
        String fraseLower = frase.toLowerCase();

        if (palabrasBarato.stream().anyMatch(fraseLower::contains)) {
            return "bajo";
        } else if (palabrasCaro.stream().anyMatch(fraseLower::contains)) {
            return "alto";
        }

        return null;
    }

    public ResultadoBusqueda buscar(String frase) {
        List<String> claves = palabrasClave(frase);
        String filtroPrecio = detectarFiltroPrecio(frase);

        List<Producto> productos = buscarProductos(claves, filtroPrecio);
        List<Tienda> tiendas = buscarTiendas(claves);

        return new ResultadoBusqueda(productos, tiendas);
    }

    private List<Producto> buscarProductos(List<String> palabrasClave, String filtroPrecio) {
        List<Producto> todos = productoRepository.findAll();

        return todos.stream()
                .filter(p -> contieneAlguna(p.getNombre(), palabrasClave) ||
                        contieneAlguna(p.getDescripcion(), palabrasClave))
                .filter(p -> {
                    if (filtroPrecio == null) return true;
                    if (filtroPrecio.equals("bajo")) return p.getPrecio() < 100; // ajustar umbral según tu lógica
                    if (filtroPrecio.equals("alto")) return p.getPrecio() > 500;
                    return true;
                })
                .collect(Collectors.toList());
    }

    private List<Tienda> buscarTiendas(List<String> palabrasClave) {
        List<Tienda> todas = tiendaRepository.findAll();

        return todas.stream()
                .filter(t -> contieneAlguna(t.getNombre(), palabrasClave) ||
                        contieneAlguna(t.getDescripcion(), palabrasClave) ||
                        contieneAlguna(t.getOwner().getNombre(), palabrasClave))
                .collect(Collectors.toList());
    }

    private boolean contieneAlguna(String texto, List<String> palabrasClave) {
        if (texto == null) return false;
        String lower = texto.toLowerCase();
        return palabrasClave.stream().anyMatch(lower::contains);
    }

    public static class ResultadoBusqueda {
        private List<Producto> productos;
        private List<Tienda> tiendas;

        public ResultadoBusqueda(List<Producto> productos, List<Tienda> tiendas) {
            this.productos = productos;
            this.tiendas = tiendas;
        }

        public List<Producto> getProductos() {
            return productos;
        }

        public List<Tienda> getTiendas() {
            return tiendas;
        }
    }
}
