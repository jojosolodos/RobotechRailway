package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.IFaseDao;
import com.robotech.web.models.Fase;

@Service
public class FaseService {

	@Autowired
	private IFaseDao faseDao;

	public List<Fase> listarFase() {
		return faseDao.findAll();
	}

	public Fase listarID(Integer id) {
		return faseDao.findById(id).orElse(null);
	}

	public void guardarFase(Fase fase) {
		faseDao.save(fase);
	}

	public void eliminarFase(Integer id) {
		faseDao.deleteById(id);
	}
}
