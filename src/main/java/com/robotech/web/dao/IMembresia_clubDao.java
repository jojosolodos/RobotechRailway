package com.robotech.web.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.robotech.web.models.Membresia_club;

public interface IMembresia_clubDao extends JpaRepository<Membresia_club, Integer> {

	Membresia_club findByUsuarioId_Id(Integer usuarioId);

	@Query("SELECT m FROM Membresia_club m WHERE m.clubId.id = :clubId AND m.estadoMembresiaId.id = 1")
	List<Membresia_club> listarJugadoresPorClubYEstado(@Param("clubId") Integer clubId);

	@Query("SELECT m FROM Membresia_club m WHERE m.clubId.id = :clubId AND m.estadoMembresiaId.id = :estadoMembresiaId")
	List<Membresia_club> obtenerJugadoresPorClubYEstado(@Param("clubId") Integer clubId,
			@Param("estadoMembresiaId") Integer estadoMembresiaId);

	Membresia_club findByUsuarioId_IdAndClubId_Id(int usuarioId, int clubId);

	@Query("SELECT COUNT(m) FROM Membresia_club m WHERE m.clubId.id = :clubId AND m.estadoMembresiaId.id = 1")
	int countByClubIdAndEstadoMembresiaId(@Param("clubId") int clubId,
			@Param("estadoMembresiaId") int estadoMembresiaId);
	
	@Query("SELECT m FROM Membresia_club m WHERE m.clubId.id = :clubId")
	Page<Membresia_club> listarJugadoresPorClubYEstadoPages(@Param("clubId") int clubId, Pageable pageable);

	@Query("SELECT m FROM Membresia_club m WHERE m.clubId.id = :clubId AND m.estadoMembresiaId.id = :estado ORDER BY m.id DESC")
	Page<Membresia_club> obtenerMiembrosPorEstado(@Param("clubId") int clubId, @Param("estado") int estado, Pageable pageable);
	
	//vista home
	@Query("SELECT m FROM Membresia_club m WHERE m.clubId.id = :clubId AND m.estadoMembresiaId.id = 1")
	Page<Membresia_club> listarJugadoresAprobadosPorClub(@Param("clubId") int clubId, Pageable pageable);

	@Query("SELECT m FROM Membresia_club m WHERE m.clubId.id = :clubId AND m.estadoMembresiaId.id = 1 ORDER BY m.fechaInscripcion DESC")
    Page<Membresia_club> findJugadoresAprobadosPorClubOrderByFechaDescH(int clubId, Pageable pageable);
	
	@Query("SELECT m FROM Membresia_club m WHERE m.usuarioId.id = :usuarioId AND m.estadoMembresiaId.id = 2")
	Membresia_club findFirstByUsuarioId_IdAndEstadoMembresiaId_Id(@Param("usuarioId") int usuarioId, @Param("estadoMembresiaId") int estadoMembresiaId);
	
	//vist dueño club
	@Query("SELECT m FROM Membresia_club m WHERE m.clubId.id = :clubId AND m.estadoMembresiaId.id = :estadoMembresiaId ORDER BY m.fechaInscripcion DESC")
    Page<Membresia_club> findByClubIdAndEstadoMembresiaOrderByFechaDesc(int clubId, int estadoMembresiaId, Pageable pageable);
    
}
