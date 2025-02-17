package com.robotech.web.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.robotech.web.models.Torneo;

public interface ITorneoDao extends JpaRepository<Torneo, Integer> {

	@Query("SELECT t FROM Torneo t ORDER BY t.Id DESC")
	Page<Torneo> findAllDesc(Pageable pageable);
	
	Optional<Torneo> findByNombre(String nombre);

}
