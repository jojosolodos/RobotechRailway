package com.robotech.web.models;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "inscripciones")
public class Inscripciones {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuarioId;

	@OneToOne
	@JoinColumn(name = "torneo_id")
	private Torneo torneoId;

	@OneToOne
	@JoinColumn(name = "estado_inscripcion_id")
	private Estado_inscripcion estadoInscripcionId;

	@JoinColumn(name = "fecha_inscripcion")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInscripcion;

	public Inscripciones() {
		super();
	}

	public Inscripciones(Usuario usuarioId, Torneo torneoId, Estado_inscripcion estadoInscripcionId) {
		super();
		this.usuarioId = usuarioId;
		this.torneoId = torneoId;
		this.estadoInscripcionId = estadoInscripcionId;
	}

	@PrePersist
	protected void onCreate() {
		this.fechaInscripcion = new Date();
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

	public Torneo getTorneoId() {
		return torneoId;
	}

	public void setTorneoId(Torneo torneoId) {
		this.torneoId = torneoId;
	}

	public Estado_inscripcion getEstadoInscripcionId() {
		return estadoInscripcionId;
	}

	public void setEstadoInscripcionId(Estado_inscripcion estadoInscripcionId) {
		this.estadoInscripcionId = estadoInscripcionId;
	}

	public Date getFechaInscripcion() {
		return fechaInscripcion;
	}

	public void setFechaInscripcion(Date fechaInscripcion) {
		this.fechaInscripcion = fechaInscripcion;
	}
}
