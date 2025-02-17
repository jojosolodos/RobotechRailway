package com.robotech.web.models;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "torneo")
public class Torneo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String nombre;
	private String descripcion;
	private String img;
	private String centro;
	private LocalDate fecha;

	@Column(name = "jugadores_requeridos")
	private Integer jugReq;

	@OneToOne
	@JoinColumn(name = "categoria_id")
	private Categoria categoriaId;

	@OneToOne
	@JoinColumn(name = "estado_torneo_id")
	private Estado_torneo estadoTorneoId;

	public Torneo() {
		super();
	}

	public Torneo(String nombre, String descripcion, String img, String centro, LocalDate fecha, Integer jugReq,
			Categoria categoriaId, Estado_torneo estadoTorneoId) {
		super();
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.img = img;
		this.centro = centro;
		this.fecha = fecha;
		this.jugReq = jugReq;
		this.categoriaId = categoriaId;
		this.estadoTorneoId = estadoTorneoId;
	}

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

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getCentro() {
		return centro;
	}

	public void setCentro(String centro) {
		this.centro = centro;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Integer getJugReq() {
		return jugReq;
	}

	public void setJugReq(Integer jugReq) {
		this.jugReq = jugReq;
	}

	public Categoria getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Categoria categoriaId) {
		this.categoriaId = categoriaId;
	}

	public Estado_torneo getEstadoTorneoId() {
		return estadoTorneoId;
	}

	public void setEstadoTorneoId(Estado_torneo estadoTorneoId) {
		this.estadoTorneoId = estadoTorneoId;
	}

}
