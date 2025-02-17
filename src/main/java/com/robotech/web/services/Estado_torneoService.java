package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.IEstado_torneoDao;
import com.robotech.web.models.Estado_torneo;

@Service
public class Estado_torneoService {

	@Autowired
	private IEstado_torneoDao estadoTorDao;

	public List<Estado_torneo> listarEstadoTorn() {
		return estadoTorDao.findAll();
	}

	public Estado_torneo listarID(Integer id) {
		return estadoTorDao.findById(id).orElse(null);
	}

	public void guardarEstadoTorneo(Estado_torneo estadoTor) {
		estadoTorDao.save(estadoTor);
	}

	public void eliminarEstadoTorneo(Integer id) {
		estadoTorDao.deleteById(id);
	}
}
