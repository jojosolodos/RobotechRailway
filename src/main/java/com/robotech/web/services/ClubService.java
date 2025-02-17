package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.IClubDao;
import com.robotech.web.models.Club;

@Service
public class ClubService {

	@Autowired
	private IClubDao clubDao;

	public List<Club> listarClub() {
		return clubDao.findAll();
	}

	public Club listarID(Integer id) {
		return clubDao.findById(id).orElse(null);
	}

	public void guardarClub(Club club) {
		clubDao.save(club);
	}

	public void eliminarClub(Integer id) {
		clubDao.deleteById(id);
	}
	
	///////////////////////////////////////////////////////
	
	public boolean existePorNombre(String nombre) {
		return clubDao.existsByNombre(nombre);
	}

}
