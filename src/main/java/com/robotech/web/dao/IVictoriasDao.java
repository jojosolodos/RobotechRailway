package com.robotech.web.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.robotech.web.models.Usuario;
import com.robotech.web.models.Victorias;

public interface IVictoriasDao extends JpaRepository<Victorias, Integer> {

	List<Victorias> findAllByOrderByCantidadDesc();

	List<Victorias> findAllByOrderByCantidadDesc(Pageable pageable);

	@Query("SELECT v FROM Victorias v ORDER BY v.cantidad DESC")
	Page<Victorias> findAllByOrderByCantidadDescPageable(Pageable pageable);

	@Query("SELECT SUM(v.cantidad) FROM Victorias v WHERE v.usuarioId IN "
			+ "(SELECT m.usuarioId FROM Membresia_club m WHERE m.clubId.id = :clubId)")
	Integer sumarVictoriasPorClub(@Param("clubId") int clubId);

	Victorias findByUsuarioId_Id(Integer usuarioId);
	
	@Query("SELECT v FROM Victorias v ORDER BY v.cantidad DESC")
	List<Victorias> findTop24ByOrderByCantidadDesc(Pageable pageable);
	Victorias findByUsuarioId(Usuario usuario);


}
