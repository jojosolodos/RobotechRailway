package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.ITorneoDao;
import com.robotech.web.models.Torneo;

@Service
public class TorneoService {

	@Autowired
	private ITorneoDao torneoDao;

	public List<Torneo> listarTorneo() {
		return torneoDao.findAll();
	}

	public Torneo listarID(Integer id) {
		return torneoDao.findById(id).orElse(null);
	}

	public void guardarTorneo(Torneo torneo) {
		torneoDao.save(torneo);
	}

	public void eliminarTorneo(Integer id) {
		torneoDao.deleteById(id);
	}

	///////////////////////////////////////////////////////

	public Page<Torneo> listarTorneoPaginado(Pageable pageable) {
		return torneoDao.findAllDesc(pageable);
	}
	
	///////////////////////////////////////////////////////
	
	public boolean exitePorNombre(String nombre) {
		return torneoDao.findByNombre(nombre).isPresent();
	}
}
