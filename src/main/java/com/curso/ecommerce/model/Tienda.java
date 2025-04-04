package com.curso.ecommerce.model;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "tiendas")
public class Tienda {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String nombre;
	private String descripcion;
	private String imagen;
//	private double precio;
//	private int cantidad;

	// Remove or comment out the redundant usuario field
	// @ManyToOne
	// private Usuario usuario;

	// This collection holds all products belonging to this store.
	@OneToMany(mappedBy = "tienda", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Producto> productos = new ArrayList<>();

	// Store owner: this is the only user relationship needed.
	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario owner;

	// Constructors
	public Tienda() {}

	public Tienda(Integer id, String nombre, String descripcion, String imagen, double precio, int cantidad) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.imagen = imagen;
//		this.precio = precio;
//		this.cantidad = cantidad;
	}

	// Getters and Setters


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getImagen() {
		return imagen;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

//	public double getPrecio() {
//		return precio;
//	}
//	public void setPrecio(double precio) {
//		this.precio = precio;
//	}
//
//	public int getCantidad() {
//		return cantidad;
//	}
//	public void setCantidad(int cantidad) {
//		this.cantidad = cantidad;
//	}

	public List<Producto> getProductos() {
		return productos;
	}
	public void setProductos(List<Producto> productos) {
		this.productos = productos;
	}

	public Usuario getOwner() {
		return owner;
	}
	public void setOwner(Usuario owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Tienda{" +
				"id=" + id +
				", nombre='" + nombre + '\'' +
				", descripcion='" + descripcion + '\'' +
				", imagen='" + imagen + '\'' +
//				", precio=" + precio +
//				", cantidad=" + cantidad +
				", owner=" + owner +
				'}';
	}
}
