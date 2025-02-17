package com.robotech.web.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "membresia_club")
public class Membresia_club {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuarioId;

	@OneToOne
	@JoinColumn(name = "club_id")
	private Club clubId;

	@OneToOne
	@JoinColumn(name = "estado_membresia_id")
	private Estado_membresia estadoMembresiaId;
	
	@Column(name = "fecha_inscripcion")
    private LocalDateTime fechaInscripcion;

	public Membresia_club() {
		super();
	}

	public Membresia_club(Usuario usuarioId, Club clubId, Estado_membresia estadoMembresiaId,
			LocalDateTime fechaInscripcion) {
		super();
		this.usuarioId = usuarioId;
		this.clubId = clubId;
		this.estadoMembresiaId = estadoMembresiaId;
		this.fechaInscripcion = fechaInscripcion;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Usuario getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Usuario usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Club getClubId() {
		return clubId;
	}

	public void setClubId(Club clubId) {
		this.clubId = clubId;
	}

	public Estado_membresia getEstadoMembresiaId() {
		return estadoMembresiaId;
	}

	public void setEstadoMembresiaId(Estado_membresia estadoMembresiaId) {
		this.estadoMembresiaId = estadoMembresiaId;
	}

	public LocalDateTime getFechaInscripcion() {
		return fechaInscripcion;
	}

	public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
		this.fechaInscripcion = fechaInscripcion;
	}

	
}
