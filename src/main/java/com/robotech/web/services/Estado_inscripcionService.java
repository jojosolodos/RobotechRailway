package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.IEstado_inscripcionDao;
import com.robotech.web.models.Estado_inscripcion;

@Service
public class Estado_inscripcionService {

	@Autowired
	private IEstado_inscripcionDao estadoInscDao;

	public List<Estado_inscripcion> listarEstadoIns() {
		return estadoInscDao.findAll();
	}

	public Estado_inscripcion listarID(Integer id) {
		return estadoInscDao.findById(id).orElse(null);
	}

	public void guardarEstadoInsc(Estado_inscripcion estadoIns) {
		estadoInscDao.save(estadoIns);
	}

	public void eliminarEstadoIns(Integer id) {
		estadoInscDao.deleteById(id);
	}
}
