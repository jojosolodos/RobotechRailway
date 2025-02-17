package com.robotech.web.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.robotech.web.models.Enfrentamiento;

public interface IEnfrentamientoDao extends JpaRepository<Enfrentamiento, Integer> {

	@Query("SELECT e FROM Enfrentamiento e WHERE e.participante1.torneoId.id = :torneoId AND e.participante2.torneoId.id = :torneoId")
	List<Enfrentamiento> findByTorneoId(@Param("torneoId") int torneoId);

	@Query("SELECT e FROM Enfrentamiento e WHERE e.participante1.id IN :jugadorIds AND e.participante1.id IN :jugadorIds")
	List<Enfrentamiento> findByJugadorIds(@Param("jugadorIds") List<Integer> jugadorIds);

	// Buscar enfrentamientos donde alguno de los participantes esté inscrito en el
	// torneo
	@Query("SELECT e FROM Enfrentamiento e WHERE e.participante1.torneoId.id = :torneoId OR e.participante2.torneoId.id = :torneoId")
	List<Enfrentamiento> findByTorneo(@Param("torneoId") Integer torneoId);

	@Query("SELECT e FROM Enfrentamiento e "
			+ "WHERE (e.participante1.torneoId.id = :torneoId OR e.participante2.torneoId.id = :torneoId) "
			+ "AND e.faseId.id = :faseId")
	List<Enfrentamiento> findByTorneoAndFase(@Param("torneoId") int torneoId, @Param("faseId") int faseId);

}
