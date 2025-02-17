package com.robotech.web.controllers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.robotech.web.dao.IVictoriasDao;
import com.robotech.web.models.Club;
import com.robotech.web.models.Comunidad;
import com.robotech.web.models.Enfrentamiento;
import com.robotech.web.models.Estado_membresia;
import com.robotech.web.models.Inscripciones;
import com.robotech.web.models.Membresia_club;
import com.robotech.web.models.Torneo;
import com.robotech.web.models.Usuario;
import com.robotech.web.models.Victorias;
import com.robotech.web.services.ClubService;
import com.robotech.web.services.ComunidadService;
import com.robotech.web.services.EnfrentamientoService;
import com.robotech.web.services.Estado_inscripcionService;
import com.robotech.web.services.Estado_membresiaService;
import com.robotech.web.services.Estado_torneoService;
import com.robotech.web.services.InscripcionesService;
import com.robotech.web.services.Membresia_ClubService;
import com.robotech.web.services.TorneoService;
import com.robotech.web.services.UploadFileService;
import com.robotech.web.services.UsuarioService;
import com.robotech.web.services.VictoriasService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/home")
public class HomeController {

	@Autowired
	private ComunidadService comunidadService;
	@Autowired
	private VictoriasService victoriasService;
	@Autowired
	private TorneoService torneoService;
	@Autowired
	private ClubService clubService;
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private Membresia_ClubService membresiaClubService;
	@Autowired
	private InscripcionesService inscripcionesService;
	@Autowired
	private UploadFileService uploadService;
	@Autowired
	private Estado_membresiaService estadoMembresiaService;
	@Autowired
	private Estado_inscripcionService estadoInscripcionesService;
	@Autowired
	private Estado_torneoService estadoTorneoService;
	@Autowired
	private EnfrentamientoService enfrentamientoService;
	@Autowired
	private IVictoriasDao victoriasDao;

	// INICIO
	@GetMapping("")
	public String inicio(Model model, HttpSession session) {
		List<Victorias> victorias = victoriasService.listarTop3Victorias();
		model.addAttribute("victorias", victorias);
		return "home/home";
	}

	///////////////////////////////////////////////////////////////////////
	/////////// TORNEOS //////////////
	//////////////////////////////////////////////////////////////////////

	// VER TORNEOS
	@GetMapping("/torneos")
	public String listarTorneos(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "6") int size, Model model) {

		Page<Torneo> torneo = torneoService.listarTorneoPaginado(PageRequest.of(page, size));

		model.addAttribute("torneo", torneo.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("size", size);
		model.addAttribute("totalPages", torneo.getTotalPages());

		return "home/torneo";
	}

	@GetMapping("/torneos/{id}")
	public String listarTorneosId(Model model, HttpSession session, @PathVariable("id") int id,
	        @RequestParam(value = "inscripcionExitosa", required = false) String inscripcionExitosa) {
	    
	    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
	    Torneo torneoDet = torneoService.listarID(id);
	    List<Inscripciones> inscripciones = inscripcionesService.obtenerPorTorneo(id, 2);
	    model.addAttribute("inscripciones", inscripciones);

	    if (usuarioSesion == null) {
	        model.addAttribute("mensajeCuenta", "Debe tener una cuenta registrada con nosotros.");
	    } else {
	        Membresia_club membresia = membresiaClubService.obtenerPorUsuario(usuarioSesion.getId());
	        if (membresia == null) {
	            model.addAttribute("mensajeMembresia", "Usted no pertenece a ningún club.");
	        } else if (membresia.getEstadoMembresiaId().getId() != 1) {
	            model.addAttribute("mensajeMembresia", "Aún no puede participar, su estado de membresía se encuentra "
	                    + membresia.getEstadoMembresiaId().getNombre() + ".");
	        } else {
	            int categoriaUsuario = usuarioSesion.getCategoriaId().getId();
	            int categoriaTorneo = torneoDet.getCategoriaId().getId();

	            if (categoriaUsuario != categoriaTorneo) {
	                model.addAttribute("mensajeCategoria", "Este torneo no pertenece a tu categoría.");
	            } else {
	                boolean yaInscrito = inscripcionesService.existeInscripcion(usuarioSesion.getId(), id);
	                model.addAttribute("yaInscrito", yaInscrito);

	                if (!yaInscrito) {
	                    Inscripciones otraInscripcion = inscripcionesService.obtenerOtraInscripcion(usuarioSesion.getId(), id);
	                    boolean puedeInscribirse = inscripcionesService.puedeInscribirse(usuarioSesion.getId());

	                    if (otraInscripcion != null && !puedeInscribirse) {
	                        model.addAttribute("mensajeOtroTorneo",
	                                "Ya estás inscrito en otro torneo: " + otraInscripcion.getTorneoId().getNombre());
	                    } else {
	                        Integer clubId = membresia.getClubId().getId();
	                        boolean puedeInscribirMas = inscripcionesService.puedeInscribirMasParticipantes(id, clubId);

	                        if (!puedeInscribirMas) {
	                            model.addAttribute("mensajeClub", "Tu club ya tiene 3 participantes inscritos en este torneo.");
	                        } else {
	                            if (inscripciones.size() >= torneoDet.getJugReq()) {
	                                model.addAttribute("mensajeClub", "Este torneo ya alcanzó el límite de participantes.");
	                            } else {
	                                model.addAttribute("mostrarBotonInscripcion", true);
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }

	    if (inscripcionExitosa != null) {
	        model.addAttribute("inscripcionExitosa", true);
	    }

	    // Obtener y agrupar enfrentamientos por fase
	    List<Enfrentamiento> enfrentamientos = enfrentamientoService.obtenerEnfrentamientosPorTorneo(id);
	    Map<Integer, List<Enfrentamiento>> enfrentamientosPorFase = new TreeMap<>();
	    for (Enfrentamiento e : enfrentamientos) {
	        enfrentamientosPorFase.computeIfAbsent(e.getFaseId().getId(), k -> new ArrayList<>()).add(e);
	    }
	    
	 // Obtener el ganador final si existe
	    Enfrentamiento finalMatch = enfrentamientos.stream()
	        .filter(e -> e.getFaseId().getId() == 3) // Suponiendo que la fase 3 es la final
	        .findFirst()
	        .orElse(null);
	    
	    Usuario ganadorFinal = (finalMatch != null && finalMatch.getGanador() != null) 
	    	    ? finalMatch.getGanador().getUsuarioId() 
	    	    : null;
	    
	    model.addAttribute("enfrentamientosPorFase", enfrentamientosPorFase);
	    model.addAttribute("ganadorFinal", ganadorFinal);

	    model.addAttribute("torneoDet", torneoDet);
	    
	    return "home/torneoDetalles";
	}



	@PostMapping("/torneos/{id}/inscribirse")
	public String incribirseTorneo(@PathVariable("id") int id, HttpSession session, RedirectAttributes ra) {
	    // Verificar si el usuario está en sesión
	    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
	    if (usuarioSesion == null) {
	        ra.addFlashAttribute("error", "Debe iniciar sesión para inscribirse en un torneo.");
	        return "redirect:/login";
	    }
	    System.out.println("Usuario en sesión: " + usuarioSesion.getUsername()); // Debug

	    // Obtener el torneo
	    Torneo torneo = torneoService.listarID(id);
	    if (torneo == null) {
	        ra.addFlashAttribute("error", "El torneo no existe.");
	        return "redirect:/home/torneos";
	    }
	    System.out.println("Torneo encontrado: " + torneo.getNombre()); // Debug

	    // Verificar si el usuario tiene una membresía activa
	    Membresia_club membresia = membresiaClubService.obtenerPorUsuario(usuarioSesion.getId());
	    if (membresia == null || membresia.getEstadoMembresiaId().getId() != 1) {
	        System.out.println("Membresía no activa o no existe para el usuario: " + usuarioSesion.getUsername()); // Debug
	        ra.addFlashAttribute("error", "Tu membresía no está activa o no perteneces a ningún club.");
	        return "redirect:/home/torneos/" + id;
	    }
	    System.out.println("Membresía activa encontrada para el usuario: " + usuarioSesion.getUsername()); // Debug

	    // Verificar si el club ya alcanzó el límite de 3 inscritos en este torneo
	    Integer clubId = membresia.getClubId().getId();
	    boolean puedeInscribirMas = inscripcionesService.puedeInscribirMasParticipantes(id, clubId);
	    if (!puedeInscribirMas) {
	        System.out.println("El club ya tiene 3 inscritos en el torneo: " + torneo.getNombre()); // Debug
	        ra.addFlashAttribute("error", "Tu club ya tiene 3 participantes inscritos en este torneo.");
	        return "redirect:/home/torneos/" + id;
	    }
	    System.out.println("El club puede inscribir más participantes."); // Debug

	    // Verificar si el usuario puede inscribirse (no ha participado en los últimos 5 días)
	    if (!inscripcionesService.puedeInscribirse(usuarioSesion.getId())) {
	        System.out.println("El usuario no puede inscribirse debido a la regla de los 5 días."); // Debug
	        ra.addFlashAttribute("error",
	                "No puedes inscribirte en este torneo. Debes esperar al menos 5 días desde tu última inscripción.");
	        return "redirect:/home/torneos/" + id;
	    }
	    System.out.println("El usuario puede inscribirse."); // Debug

	    // Verificar si el usuario ya está inscrito en el torneo
	    boolean yaInscrito = inscripcionesService.existeInscripcion(usuarioSesion.getId(), id);
	    if (yaInscrito) {
	        System.out.println("El usuario ya está inscrito en el torneo: " + torneo.getNombre()); // Debug
	        ra.addFlashAttribute("error", "Ya estas inscrito en este torneo.");
	        return "redirect:/home/torneos/" + id;
	    }
	    System.out.println("El usuario no está inscrito en el torneo."); // Debug

	    // Verificar si el número de inscripciones ya alcanzó el límite del torneo
	    int inscritos = inscripcionesService.contarInscripcionesPorTorneo(id);
	    if (inscritos >= torneo.getJugReq()) {
	        System.out.println("El torneo ya alcanzó el límite de jugadores permitidos."); // Debug
	        ra.addFlashAttribute("error", "El torneo ya alcanzo el limite de jugadores permitidos.");
	        return "redirect:/home/torneos/" + id;
	    }
	    System.out.println("Aún hay cupo disponible en el torneo."); // Debug

	    // Verificar si la categoría del usuario coincide con la del torneo
	    int categoriaUsuario = usuarioSesion.getCategoriaId().getId();
	    int categoriaTorneo = torneo.getCategoriaId().getId();
	    if (categoriaUsuario != categoriaTorneo) {
	        System.out.println("La categoría del usuario no coincide con la del torneo."); // Debug
	        ra.addFlashAttribute("error",
	                "No puedes inscribirte a este torneo, ya que no pertenece a tu categoría.");
	        return "redirect:/home/torneos/" + id;
	    }
	    System.out.println("La categoría del usuario coincide con la del torneo."); // Debug

	    try {
	        // Crear la nueva inscripción
	        Inscripciones newIncription = new Inscripciones();
	        newIncription.setUsuarioId(usuarioSesion);
	        newIncription.setTorneoId(torneo);
	        newIncription.setEstadoInscripcionId(estadoInscripcionesService.listarID(2));

	        // Guardar la inscripción
	        inscripcionesService.guaradarInscripciones(newIncription);
	        System.out.println("Inscripción guardada con ID: " + newIncription.getId()); // Debug

	        // Verificar si el torneo alcanzó el límite de jugadores
	        int inscritosC = inscripcionesService.contarInscripcionesPorTorneo(id);
	        if (inscritosC >= torneo.getJugReq()) {
	            System.out.println("El torneo alcanzó el límite de jugadores. Cerrando torneo..."); // Debug
	            torneo.setEstadoTorneoId(estadoTorneoService.listarID(2));
	            torneoService.guardarTorneo(torneo);
	        }

	        ra.addFlashAttribute("inscripcionExitosa", true);
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("Error al guardar la inscripción: " + e.getMessage()); // Debug
	        ra.addFlashAttribute("error", "Hubo un error al inscribir en el torneo");
	    }

	    return "redirect:/home/torneos/" + id;
	}


///////////////////////////////////////////////////////////////////////
///////////					CLUBS						//////////////
//////////////////////////////////////////////////////////////////////

	// VER CLUBES
	@GetMapping("/clubes")
	public String listarClubes(Model model) {
		List<Club> club = clubService.listarClub();
		model.addAttribute("club", club);
		return "home/club";
	}

	// VER INFORMACIÓN DEL CLUB
	@GetMapping("/clubes/{id}")
	public String listarClubId(Model model, HttpSession session, @PathVariable("id") int id,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "8") int size) {

	    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

	    if (usuarioSesion == null) {
	        model.addAttribute("mensajeCuenta", "Debe tener una cuenta registrada con nosotros.");
	    } else {
	        Usuario usuarioActualizado = usuarioService.listarID(usuarioSesion.getId());
	        session.setAttribute("usuario", usuarioActualizado);
	        model.addAttribute("usuario", usuarioActualizado);

	        Membresia_club membresia = membresiaClubService.obtenerPorUsuario(usuarioSesion.getId());

	        if (membresia != null && membresia.getEstadoMembresiaId() != null) {
	            int estadoMembresia = membresia.getEstadoMembresiaId().getId();
	            int clubMembresia = membresia.getClubId() != null ? membresia.getClubId().getId() : -1;

	            if (estadoMembresia == 2) { // Estado 2 = Rechazado
	                LocalDateTime fechaRechazo = membresia.getFechaInscripcion(); // Última fecha de inscripción
	                long minutosPasados = -1; // Declarar la variable fuera del bloque if

	                if (fechaRechazo != null) {
	                    LocalDateTime ahora = LocalDateTime.now();
	                    Duration duracion = Duration.between(fechaRechazo, ahora);
	                    minutosPasados = duracion.toMinutes(); // Actualizar el valor de minutosPasados

	                    if (minutosPasados < 10) {
	                        long minutosRestantes = 10 - minutosPasados;
	                        String fechaHoraRechazo = fechaRechazo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	                        model.addAttribute("mensajeDesaprobado", "Su solicitud fue rechazada el " + fechaHoraRechazo + ". Espere " + minutosRestantes + " minutos antes de intentar nuevamente.");
	                        model.addAttribute("mensajeMembresia", "Debes esperar " + minutosRestantes + " minutos antes de volver a inscribirte.");
	                        model.addAttribute("mostrarBotonInscripcion", false);
	                    } else {
	                        // Si han pasado más de 10 minutos, no agregar el mensaje de membresía
	                        model.addAttribute("mostrarBotonInscripcion", true);
	                        model.addAttribute("mensajeMembresia", null); // Asegúrate de no agregar el mensaje de membresía
	                    }
	                } else {
	                    // Si no hay fecha de rechazo, permitir la inscripción
	                    model.addAttribute("mostrarBotonInscripcion", true);
	                    model.addAttribute("mensajeMembresia", null);
	                }

	                // Mensajes adicionales basados en el club
	                if (clubMembresia == id) {
	                    if (minutosPasados < 10) {
	                        model.addAttribute("mensajeMembresia", "Tu solicitud fue DESAPROBADA por el Dueño del Club porque no cumplió con los requisitos necesarios para ingresar.");
	                    }
	                } else {
	                    if (minutosPasados < 10) {
	                        model.addAttribute("mensajeMembresia", "Tu solicitud a otro club fue desaprobada. Después de 10 minutos podrás volver a postular.");
	                    }
	                }

	            } else if (estadoMembresia == 1 && clubMembresia == id) {
	                model.addAttribute("mensajeMembresia", "Ya perteneces a este club.");
	            } else if (estadoMembresia == 3) { // Estado 3 = En revisión
	                if (clubMembresia == id) {
	                    model.addAttribute("mensajeMembresia", "Tu solicitud se encuentra en revisión.");
	                } else {
	                    model.addAttribute("mensajeMembresia", "Enviaste solicitud a otro club.");
	                }
	            } else {
	                model.addAttribute("mensajeMembresia", "Ya perteneces a otro club.");
	            }
	        } else {
	            model.addAttribute("mostrarBotonInscripcion", true);
	        }
	    }

	    Club clubDet = clubService.listarID(id);
	    model.addAttribute("clubDet", clubDet);

	    Page<Membresia_club> jugadoresPage = membresiaClubService.listarJugadoresAprobadosPorClubPorFecha(id, page, size);
	    model.addAttribute("jugadoresActivos", jugadoresPage.getContent());
	    model.addAttribute("currentPage", jugadoresPage.getNumber());
	    model.addAttribute("totalPages", jugadoresPage.getTotalPages());
	    model.addAttribute("size", jugadoresPage.getSize());

	    int cantidadUsuarios = membresiaClubService.contarUsuariosEnClub(id);
	    model.addAttribute("cantidadUsuarios", cantidadUsuarios);

	    Integer cantidadVictorias = membresiaClubService.contarVictoriasEnClub(id);
	    model.addAttribute("cantidadVictorias", cantidadVictorias);

	    return "home/clubDetalles";
	}


	// ENVIAR SOLICITUD A UN CLUB
	@PostMapping("/clubes/{id}/inscribirse")
	public String inscribirseClub(@PathVariable("id") int clubId, HttpSession session,
	                              RedirectAttributes redirectAttributes) {
	    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

	    if (usuarioSesion == null) {
	        redirectAttributes.addFlashAttribute("mensajeError", "Debe iniciar sesión para inscribirse en un club.");
	        return "redirect:/home/clubes/" + clubId;
	    }

	    // Buscar si el usuario ya tiene una membresía rechazada en algún club
	    Membresia_club membresiaExistente = membresiaClubService.obtenerMembresiaRechazadaPorUsuario(usuarioSesion.getId());

	    System.out.println("Usuario ID: " + usuarioSesion.getId());
	    System.out.println("Club ID: " + clubId);
	    System.out.println("Membresía existente: " + (membresiaExistente != null ? "Encontrada en club " + membresiaExistente.getClubId().getId() : "No encontrada"));

	    if (membresiaExistente != null) {
	        // Actualizar club y cambiar estado a "Pendiente"
	        membresiaExistente.setClubId(clubService.listarID(clubId)); 
	        membresiaExistente.setEstadoMembresiaId(estadoMembresiaService.listarID(3)); // Estado "Pendiente"
	        membresiaExistente.setFechaInscripcion(LocalDateTime.now());
	        membresiaClubService.guardarMembresiaClub(membresiaExistente);

	        redirectAttributes.addFlashAttribute("mensajeExito", "Se ha reenviado tu solicitud de inscripción.");
	        return "redirect:/home/clubes/" + clubId;
	    }

	    // Si no existe membresía rechazada, crear una nueva
	    Club club = clubService.listarID(clubId);
	    if (club == null) {
	        redirectAttributes.addFlashAttribute("mensajeError", "El club seleccionado no existe.");
	        return "redirect:/home/clubes";
	    }

	    Membresia_club nuevaMembresia = new Membresia_club();
	    nuevaMembresia.setUsuarioId(usuarioSesion);
	    nuevaMembresia.setClubId(club);
	    nuevaMembresia.setEstadoMembresiaId(estadoMembresiaService.listarID(3)); // Estado "Pendiente"
	    nuevaMembresia.setFechaInscripcion(LocalDateTime.now());

	    membresiaClubService.guardarMembresiaClub(nuevaMembresia);

	    redirectAttributes.addFlashAttribute("mensajeExito", "Solicitud de inscripción enviada correctamente.");
	    return "redirect:/home/clubes/" + clubId;
	}


///////////////////////////////////////////////////////////////////////
///////////				COMUNIDAD						//////////////
//////////////////////////////////////////////////////////////////////

	// VER COMUNIDAD
	@GetMapping("/comunidad")
	public String listarComunidad(@RequestParam(value = "tema", required = false) String tema,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "5") int size, Model model, HttpSession session) {

		Page<Comunidad> comunidad;
		if (tema != null && !tema.isEmpty()) {
			comunidad = comunidadService.buscarPorTemaPaginado(tema, PageRequest.of(page, size));
		} else {
			comunidad = comunidadService.listarComunidadPaginado(PageRequest.of(page, size));
		}

		// Lista de temas para el modal
		String[] temas = { "Robots de pelea", "Estrategias", "Nuevas tecnologías", "Competencias", "Otros" };

		model.addAttribute("comunidad", comunidad);
		model.addAttribute("tema", tema);
		model.addAttribute("temas", temas); // Enviamos la lista de temas
		return "home/comunidad";
	}

	// POST : AGREGAR COMENTARIO
	@PostMapping("/comunidad/addcomentario")
	public String guardarComentario(@RequestParam("tema") String tema, @RequestParam("comentario") String comentario,
			@RequestParam(value = "img", required = false) MultipartFile img, RedirectAttributes ra,
			HttpSession session) {

		Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
		if (usuarioSesion == null) {
			ra.addFlashAttribute("error", "Debes estar registrado para comentar.");
			return "redirect:/home/comunidad";
		}

		// Validación de longitud del comentario
		if (comentario.length() > 255) {
			ra.addFlashAttribute("longComentario", comentario.length()); // Pasamos la longitud
			return "redirect:/home/comunidad";
		}
		try {
			Comunidad com = new Comunidad();
			com.setUsuarioId(usuarioSesion);
			com.setTema(tema);
			com.setComentario(comentario);

			if (img != null && !img.isEmpty()) {
				String imgPath = uploadService.saveImage(img);
				com.setImg(imgPath);
			} else {
				com.setImg(null);
			}

			comunidadService.guardarComunidad(com);
			ra.addFlashAttribute("success", "El comentario se agregó correctamente.");
		} catch (Exception e) {
			ra.addFlashAttribute("error", "Hubo un problema al agregar el comentario.");
		}

		return "redirect:/home/comunidad";
	}

///////////////////////////////////////////////////////////////////////
///////////				CLASIFICACION					//////////////
//////////////////////////////////////////////////////////////////////

	// VER TABLA DE MÉRITO
	@GetMapping("/clasificacion")
	public String listarTablaMerito(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "6") int size, Model model) {

		Page<Victorias> victoriasPage = victoriasService.listarVictoriasDescPageable(PageRequest.of(page, size));

		model.addAttribute("victorias", victoriasPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("size", size);
		model.addAttribute("totalPages", victoriasPage.getTotalPages());
		model.addAttribute("totalElements", victoriasPage.getTotalElements());

		return "home/clasificacion";
	}

	// VER TOP3
	@GetMapping("clasificacion/top3")
	public String listarTablaMeritoTop3(Model model) {

		List<Victorias> victorias = victoriasService.listarTop3Victorias();
		model.addAttribute("victorias", victorias);

		return "home/top3";
	}

	// VER INFORMACIÓN DEL PERFIL DE UN USUARIO X
	@GetMapping("/TopUsuario/{id}")
	public String listarTopUsuario(@PathVariable("id") Integer id, Model model) {

		Usuario usuario = usuarioService.listarID(id);
		if (usuario != null) {
			model.addAttribute("usuario", usuario);
			return "home/detalleUsuario";
		}
		return "redirect:/home";
	}

///////////////////////////////////////////////////////////////////////
///////////					PERFIL						//////////////
//////////////////////////////////////////////////////////////////////

	// VER PERFIL
	@GetMapping("/perfil")
	public String perfilUsuario(Model model, HttpSession session) {

		//System.out.println("Accessing /perfil");

		Usuario usuario = (Usuario) session.getAttribute("usuario");
		if (usuario == null) {
			return "redirect:/login"; // Redirigir al login si no hay usuario en sesión
		}
		
		Victorias victorias = victoriasDao.findByUsuarioId(usuario);
		Integer cantidadVictorias = victorias !=null ? victorias.getCantidad() : 0;
		
		model.addAttribute("usuario", usuario); // Pasar la información del usuario al modelo
		model.addAttribute("cantidadVictorias", cantidadVictorias);
		return "home/verPerfil";

	}

	// EDITAR USUARIO
	@GetMapping("/perfil/editar")
	public String ActualizarPerfilUsuario(Model model, HttpSession session, @ModelAttribute("error") String error) {

		Usuario usuario = (Usuario) session.getAttribute("usuario");
		if (usuario == null) {
			return "redirect:/login"; // Redirigir al login si no hay usuario en sesión
		}

		model.addAttribute("usuario", usuario);

		// Pasar el mensaje de error al modelo (si existe)
		if (error != null && !error.isEmpty()) {
			model.addAttribute("error", error);
		}

		return "home/perfilUpdate";
	}

	 //Actualización del perfil
	@PostMapping("/perfil/editar")
	public String actualizarPerfil(@RequestParam("username") String username,
	                               @RequestParam(value = "img", required = false) MultipartFile fotoPerfil,
	                               HttpSession session, RedirectAttributes redirectAttributes) {
	    Usuario usuario = (Usuario) session.getAttribute("usuario");
	    if (usuario == null) {
	        return "redirect:/login";
	    }

	    if (!usuario.getUsername().equals(username) && usuarioService.existeUsername(username)) {
	        redirectAttributes.addFlashAttribute("error", "El username ya está en uso. Por favor, elige otro.");
	        return "redirect:/home/perfil";
	    }

	    usuario.setUsername(username);

	    if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
	        try {
	            String nombreImagen = uploadService.saveImage(fotoPerfil);
	            usuario.setFoto_perfil(nombreImagen);
	        } catch (IOException e) {
	            redirectAttributes.addFlashAttribute("error", "Error al subir la imagen");
	            return "redirect:/home/perfil";
	        }
	    }

	    usuarioService.guardarUsuario(usuario);
	    session.setAttribute("usuario", usuario);

	    redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
	    return "redirect:/home/perfil";
	}

	
}
