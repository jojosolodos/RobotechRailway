package com.robotech.web.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.robotech.web.models.Club;
import com.robotech.web.models.Usuario;

public interface IClubDao extends JpaRepository<Club, Integer> {

	Club findByUsuarioId(Usuario usuario);
	
	boolean existsByNombre(String nombre);

}
