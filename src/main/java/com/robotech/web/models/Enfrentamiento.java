package com.robotech.web.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "enfrentamiento")
public class Enfrentamiento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne
	@JoinColumn(name = "fase_id")
	private Fase faseId;

	@OneToOne
	@JoinColumn(name = "participante_1")
	private Inscripciones participante1;

	@OneToOne
	@JoinColumn(name = "participante_2")
	private Inscripciones participante2;

	@OneToOne
	@JoinColumn(name = "ganador_id")
	private Inscripciones ganador;
	

	public Enfrentamiento() {
		super();
	}

	public Enfrentamiento(Fase faseId, Inscripciones participante1, Inscripciones participante2,
			Inscripciones ganador) {
		super();
		this.faseId = faseId;
		this.participante1 = participante1;
		this.participante2 = participante2;
		this.ganador = ganador;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Fase getFaseId() {
		return faseId;
	}

	public void setFaseId(Fase faseId) {
		this.faseId = faseId;
	}

	public Inscripciones getParticipante1() {
		return participante1;
	}

	public void setParticipante1(Inscripciones participante1) {
		this.participante1 = participante1;
	}

	public Inscripciones getParticipante2() {
		return participante2;
	}

	public void setParticipante2(Inscripciones participante2) {
		this.participante2 = participante2;
	}

	public Inscripciones getGanador() {
		return ganador;
	}

	public void setGanador(Inscripciones ganador) {
		this.ganador = ganador;
	}

	

}
