package com.robotech.web.controllers;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.robotech.web.models.Categoria;
import com.robotech.web.models.Club;
import com.robotech.web.models.Enfrentamiento;
import com.robotech.web.models.Estado_torneo;
import com.robotech.web.models.Estado_usuario;
import com.robotech.web.models.Fase;
import com.robotech.web.models.Inscripciones;
import com.robotech.web.models.Torneo;
import com.robotech.web.models.Usuario;
import com.robotech.web.services.CategoriaService;
import com.robotech.web.services.ClubService;
import com.robotech.web.services.EnfrentamientoService;
import com.robotech.web.services.Estado_torneoService;
import com.robotech.web.services.Estado_usuarioService;
import com.robotech.web.services.FaseService;
import com.robotech.web.services.InscripcionesService;
import com.robotech.web.services.TorneoService;
import com.robotech.web.services.UploadFileService;
import com.robotech.web.services.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private Estado_usuarioService estadoUsuService;
	@Autowired
	private ClubService clubService;
	@Autowired
	private UploadFileService uploadService;
	@Autowired
	private TorneoService torneoService;
	@Autowired
	private CategoriaService categoriaService;
	@Autowired
	private Estado_torneoService estadoTorneoService;
	@Autowired
	private EnfrentamientoService enfrentamientoService;
	@Autowired
	private InscripcionesService inscripcionesService;
	@Autowired
	private FaseService faseService;

	// INICIO DEL PANEL DE CONTROL ADMINISTRADOR
	@GetMapping("")
	public String inicioPanel(Model model, HttpSession session) {
		return "admin/panel";
	}

	
///////////////////////////////////////////////////////////////////////
///////////					USUARIOS					//////////////
//////////////////////////////////////////////////////////////////////
	
	// muestra los usuarios Aprobados
	@GetMapping("/usuarios-aprobados")
	public String mostratUsuariosAprobados(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "5") int size, Model model, HttpSession session) {

		Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
		if (usuarioSesion == null) {
			return "redirect:/login";
		}

		// Validar que el índice de página no sea menor que 0
		if (page < 0) {
			page = 0;
		}

		Page<Usuario> usuariosAprobados = usuarioService.listtarUsuariosAprobados(page, size);

		if (usuariosAprobados.isEmpty()) {
			model.addAttribute("usuarios", new ArrayList<>()); // Lista vacía para evitar errores en la vista
			model.addAttribute("currentPage", 0);
			model.addAttribute("totalPages", 0);
			model.addAttribute("size", size);
			model.addAttribute("noUsersMessage", "No hay usuarios aprobados para mostrar.");
		} else {
			model.addAttribute("usuarios", usuariosAprobados.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", usuariosAprobados.getTotalPages());
			model.addAttribute("size", size);
		}

		return "admin/usuApproved";
	}

	// muestra los usuarios desaprobados
	@GetMapping("/usuarios-desaprobados")
	public String mostratUsuariosDesaprobados(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "5") int size, Model model, HttpSession session) {

		Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
		if (usuarioSesion == null) {
			return "redirect:/login";
		}

		// Validar que el índice de página no sea menor que 0
		if (page < 0) {
			page = 0;
		}

		Page<Usuario> usuariosDesaprobados = usuarioService.listtarUsuariosDesaprobados(page, size);

		if (usuariosDesaprobados.isEmpty()) {
			model.addAttribute("usuarios", new ArrayList<>()); // Lista vacía para evitar errores en la vista
			model.addAttribute("currentPage", 0);
			model.addAttribute("totalPages", 0);
			model.addAttribute("size", size);
			model.addAttribute("noUsersMessage", "No hay usuarios desaprobados para mostrar.");
		} else {
			model.addAttribute("usuarios", usuariosDesaprobados.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", usuariosDesaprobados.getTotalPages());
			model.addAttribute("size", size);
		}

		return "admin/usuDisapproved";
	}

	// muestra a los Usuarios En espera
	@GetMapping("/usuarios-enespera")
	public String mostrarUsuariosEnEspera(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "5") int size, Model model, HttpSession session) {

		Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
		if (usuarioSesion == null) {
			return "redirect:/login";
		}

		if (page < 0) {
			page = 0;
		}

		Page<Usuario> usuarioEnEspera = usuarioService.listtarUsuariosEnEspera(page, size);
		if (usuarioEnEspera.isEmpty()) {
			model.addAttribute("usuarios", new ArrayList<>()); // Lista vacía para evitar errores en la vista
			model.addAttribute("currentPage", 0);
			model.addAttribute("totalPages", 0);
			model.addAttribute("size", size);
			model.addAttribute("noUsersMessage", "No hay usuarios en espera para mostrar.");
		} else {
			model.addAttribute("usuarios", usuarioEnEspera.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", usuarioEnEspera.getTotalPages());
			model.addAttribute("size", size);
		}
		return "admin/usuariosAdmin";
	}

	@PostMapping("/usuarios/cambiarEstado")
	public String cambiarEstadoUsuarios(@RequestParam("usuarioId") List<Integer> usuarioIds,
			@RequestParam("estadoId") List<Integer> estadoIds) {
		// Iterar sobre los índices de las listas de IDs
		for (int i = 0; i < usuarioIds.size(); i++) {
			Integer usuarioId = usuarioIds.get(i);
			Integer estadoId = estadoIds.get(i);

			// Obtener el usuario
			Usuario usuario = usuarioService.listarID(usuarioId);
			if (usuario == null) {
				throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
			}

			// Obtener el estado
			Estado_usuario estado = estadoUsuService.listarID(estadoId);
			if (estado == null) {
				throw new IllegalArgumentException("Estado no encontrado con ID: " + estadoId);
			}

			// Actualizar el estado del usuario
			usuario.setEstadoUsuario(estado);

			// Guardar el usuario con el nuevo estado
			usuarioService.guardarUsuario(usuario);
		}

		// Redirigir de nuevo a la página de usuarios
		return "redirect:/admin/usuarios-enespera";
	}
	
///////////////////////////////////////////////////////////////////////
///////////					CLUBS						//////////////
//////////////////////////////////////////////////////////////////////

	// CLUBS
	@GetMapping("/clubs")
	public String mostrarClubsAdmin(Model model, HttpSession session) {

		Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
		if (usuarioSesion == null) {
			return "redirect:/login";
		}

		List<Club> allClubs = clubService.listarClub();
		model.addAttribute("clubs", allClubs);

		return "admin/clubs/clubsAdmin";
	}

	@GetMapping("/clubs/crear")
	public String mostrarFormCLAdd(Model model) {

		List<Usuario> usu = usuarioService.listarUsuario();

		model.addAttribute("usuarios", usu);

		return "admin/clubs/clubsAdd";
	}

	// AGREGAR CLUB
	@PostMapping("/clubs/crear")
	public String crearClub(@RequestParam String nombre, @RequestParam String descripcion,
			@RequestParam MultipartFile img, RedirectAttributes ra) {
		try {
			
			if (clubService.existePorNombre(nombre)) {
				ra.addFlashAttribute("error1", "El nombre del club ya esta en uso. Intente con otro.");
				return "redirect:/admin/clubs/crear";
			}
			
			// Crear el club
			Club nuevoClub = new Club();
			nuevoClub.setNombre(nombre);
			nuevoClub.setDescripcion(descripcion);

			Usuario ultimoUsuarioT3 = usuarioService.obtenerUltimoUsuario3(3);

			if (ultimoUsuarioT3 != null) {
				nuevoClub.setUsuarioId(ultimoUsuarioT3);
			} else {
				ra.addFlashAttribute("error", "no se encontro ningun usuario");
				return "redirect:/admin/clubs";
			}

			// Guardar la imagen del club (necesitarás un servicio para gestionar las
			// imágenes)
			String imgPath = uploadService.saveImage(img);
			nuevoClub.setImg(imgPath);

			// Guardar el club en la base de datos
			clubService.guardarClub(nuevoClub);

			ra.addFlashAttribute("success", "Club creado correctamente.");
			return "redirect:/admin/clubs"; // Redirigir a la lista de clubes
		} catch (Exception e) {
			ra.addFlashAttribute("error", "Hubo un error al crear el club.");
			return "redirect:/admin/clubs"; // Redirigir a la lista de clubes
		}
	}

	@GetMapping("clubs/addusuarios")
	public String mostrarAgDuenoClub() {

		return "admin/clubs/addDuenoClub";
	}

	// AGREGAR DUEÑO DE CLUB
	@PostMapping("/clubs/addusuarios")
	public String agregarDuenoClub(
	        @RequestParam String username, 
	        @RequestParam String password,
	        @RequestParam String nombre, 
	        @RequestParam String email, 
	        @RequestParam Integer edad,
	        @RequestParam MultipartFile img, 
	        @RequestParam Integer trayectoria, 
	        RedirectAttributes ra) {

	    try {
	        // 🔍 1️⃣ Validar campos obligatorios
	        if (username.isEmpty() || password.isEmpty() || nombre.isEmpty() || email.isEmpty() 
	            || edad == null || trayectoria == null || img.isEmpty()) {
	            ra.addFlashAttribute("error", "Todos los campos son obligatorios, incluyendo la foto.");
	            return "redirect:/admin/clubs/addusuarios";
	        }

	        // 🔍 2️⃣ Validar edad mínima
	        if (edad < 18) {
	            ra.addFlashAttribute("error", "Debes ser mayor de 18 años para registrarte.");
	            return "redirect:/admin/clubs/addusuarios";
	        }

	        // 🔍 3️⃣ Validar si el correo ya está registrado
	        if (usuarioService.existeCorreo(email)) {
	            ra.addFlashAttribute("error", "El correo ya está registrado. Usa otro correo.");
	            return "redirect:/admin/clubs/addusuarios";
	        }

	        // 🔍 4️⃣ Validar si el username ya está registrado
	        if (usuarioService.existeUsername(username)) {
	            ra.addFlashAttribute("error", "El username ya está en uso. Elige otro username.");
	            return "redirect:/admin/clubs/addusuarios";
	        }

	        // 🔍 5️⃣ Validar la longitud de la contraseña
	        if (password.length() < 6 || password.length() > 18) {
	            ra.addFlashAttribute("error", "La contraseña debe tener entre 6 y 18 caracteres.");
	            return "redirect:/admin/clubs/addusuarios";
	        }

	        // 🆗 Si todo está bien, continuar con la creación del usuario
	        Usuario newDuenoClub = new Usuario();

	        // Guardar la imagen
	        String nombImg = uploadService.saveImage(img);

	        newDuenoClub.setUsername(username);
	        newDuenoClub.setPassword(new BCryptPasswordEncoder().encode(password));
	        newDuenoClub.setNombre(nombre);
	        newDuenoClub.setCorreo(email);
	        newDuenoClub.setEdad(edad);
	        newDuenoClub.setTrayectoria(trayectoria);
	        newDuenoClub.setFoto_perfil(nombImg);
	        newDuenoClub.setFoto_robot("default.png");

	        // 🔥 Asignación de categoría según la trayectoria
	        if (trayectoria <= 24) {
	            newDuenoClub.setCategoriaId(usuarioService.AsignarCatAmateur());
	        } else if (trayectoria <= 48) {
	            newDuenoClub.setCategoriaId(usuarioService.AsignarCatSemiPro());
	        } else {
	            newDuenoClub.setCategoriaId(usuarioService.AsignarCatProfesional());
	        }

	        newDuenoClub.setTipoUsuario(usuarioService.AsignarTipoUsu3());
	        newDuenoClub.setEstadoUsuario(usuarioService.obtenerEstadoDuenoC());

	        // Guardar en la BD
	        usuarioService.guardarUsuario(newDuenoClub);

	        System.out.println("Se agregó el usuario correctamente");
	        ra.addFlashAttribute("success", "Dueño de club creado correctamente");
	        return "redirect:/admin/clubs/addusuarios";

	    } catch (Exception e) {
	        ra.addFlashAttribute("error", "Hubo un error al crear el Dueño del club");
	        System.out.println("No se pudo crear el usuario.");
	        return "redirect:/admin/clubs/addusuarios";
	    }
	}

///////////////////////////////////////////////////////////////////////
///////////					TORNEOS						//////////////
//////////////////////////////////////////////////////////////////////

	@GetMapping("/torneos")
	public String verTorneos(Model model, HttpSession session,
							@RequestParam(value = "page", defaultValue = "0") int page,
							@RequestParam(value = "size", defaultValue = "6") int size) {

		Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
		if (usuarioSesion == null) {
			return "redirect:/login";
		}
		
		Page<Torneo> torneos = torneoService.listarTorneoPaginado(PageRequest.of(page, size));
		
		model.addAttribute("torneos", torneos.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("size", size);
		model.addAttribute("totalPages", torneos.getTotalPages());

		return "admin/torneos/verTorneos";
	}

	@GetMapping("/torneos/crearTorneos")
	public String mostrarCrearTorneo(Model model) {

		model.addAttribute("categorias", categoriaService.listarCategoria());
		return "admin/torneos/crearTorneo";
	}

	@PostMapping("/torneos/crearTorneos")
	public String crearTorneos(@RequestParam String nombre, @RequestParam String descripcion,
			@RequestParam MultipartFile img, @RequestParam String centro, @RequestParam LocalDate fecha,
			@RequestParam Integer jugReq, @RequestParam Integer categoriaId, RedirectAttributes ra) {

		try {
			
			if(torneoService.exitePorNombre(nombre)) {
				ra.addFlashAttribute("error1", "El nombre del torneo ya está en uso. Ingrese otro");
				System.out.println("el nombre del torneo ya esta en uso");
				return "redirect:/admin/torneos/crearTorneos";
			}
			
			Torneo newTorneo = new Torneo();

			String nomImg = uploadService.saveImage(img);
			newTorneo.setImg(nomImg);

			newTorneo.setNombre(nombre);
			newTorneo.setDescripcion(descripcion);
			newTorneo.setCentro(centro);
			newTorneo.setFecha(fecha);
			newTorneo.setJugReq(jugReq);

			Categoria categoria = categoriaService.listarID(categoriaId);
			newTorneo.setCategoriaId(categoria);

			Estado_torneo estadoTorneo = estadoTorneoService.listarID(1);
			newTorneo.setEstadoTorneoId(estadoTorneo);
			torneoService.guardarTorneo(newTorneo);

			System.out.println(newTorneo.getNombre() + " se ha creado correctamente.");
			ra.addFlashAttribute("success", "Torneo creado correctamente");
			return "redirect:/admin/torneos";

		} catch (Exception e) {
			e.printStackTrace();
			ra.addFlashAttribute("error", "Hubo un error al crear el torneo");
		}

		return "redirect:/admin/torneos/crearTorneos";
	}
	

	@GetMapping("/verTorneo/{id}")
	public String verTorneoId(Model model, @PathVariable("id") int id) {
		Torneo torneoDet = torneoService.listarID(id);

		// Obtener inscripciones activas (estado = 2)
		List<Inscripciones> inscripciones = inscripcionesService.obtenerPorTorneo(id, 2);
		inscripciones = inscripciones.stream().filter(ins -> ins != null && ins.getUsuarioId() != null)
				.collect(Collectors.toList());

		// Obtener todos los enfrentamientos del torneo
		List<Enfrentamiento> enfrentamientos = enfrentamientoService.obtenerPorTorneo(id);

		// Obtener enfrentamientos de cuartos (fase 1) y semifinal (fase 2)
		List<Enfrentamiento> enfrentamientosCuartos = enfrentamientos.stream()
				.filter(e -> e.getFaseId() != null && e.getFaseId().getId() == 1).collect(Collectors.toList());

		List<Enfrentamiento> enfrentamientosSemifinal = enfrentamientos.stream()
				.filter(e -> e.getFaseId() != null && e.getFaseId().getId() == 2).collect(Collectors.toList());

		List<Enfrentamiento> enfrentamientosFinal = enfrentamientos.stream()
				.filter(e -> e.getFaseId() != null && e.getFaseId().getId() == 3).collect(Collectors.toList());

		boolean enfrentamientosGenerados = !enfrentamientos.isEmpty();

		// Determinar la fase más alta alcanzada
		int faseActual = enfrentamientos.stream().filter(e -> e.getFaseId() != null)
				.mapToInt(e -> e.getFaseId().getId()).max().orElse(1);

		model.addAttribute("inscripciones", inscripciones);
		model.addAttribute("torneoDet", torneoDet);
		model.addAttribute("enfrentamientos", enfrentamientos); // 🔹 Agregar enfrentamientos
		model.addAttribute("enfrentamientosCuartos", enfrentamientosCuartos);
		model.addAttribute("enfrentamientosSemifinal", enfrentamientosSemifinal);
		model.addAttribute("enfrentamientosFinal", enfrentamientosFinal);

		model.addAttribute("faseActual", faseActual);
		model.addAttribute("enfrentamientosGenerados", enfrentamientosGenerados);

		return "admin/torneos/torneoDetalles";
	}

	@GetMapping("/torneos/generar")
	public String generarEnfrentamiento(@RequestParam Integer torneoId, @RequestParam Integer faseId,
			RedirectAttributes ra) {
		List<Enfrentamiento> enfrentamientosGenerados = enfrentamientoService.obtenerPorTorneo(torneoId);
		System.out.println("Enfrentamientos generados: " + enfrentamientosGenerados.size());

		try {
			Fase fase = new Fase();
			fase.setId(faseId);

			enfrentamientoService.generarEnfrentamientosAleatorios(torneoId, fase);

			ra.addFlashAttribute("success", "Enfrentamientos generados exitosamente para el torneo ID " + torneoId);
		} catch (Exception e) {
			ra.addFlashAttribute("error", "Error al generar enfrentamientos: " + e.getMessage());
		}

		return "redirect:/admin/verTorneo/" + torneoId;
	}

	@PostMapping("/torneos/guardarGanadores")
	public String guardarGanadores(@RequestParam Map<String, String> ganadoresSeleccionados) {
		Integer torneoId = null; // Variable para almacenar el ID del torneo

		for (Map.Entry<String, String> entry : ganadoresSeleccionados.entrySet()) {
			if (entry.getKey().startsWith("ganador_")) {
				int enfrentamientoId = Integer.parseInt(entry.getKey().split("_")[1]);
				int ganadorId = Integer.parseInt(entry.getValue());

				Enfrentamiento enfrentamiento = enfrentamientoService.listarID(enfrentamientoId);
				Inscripciones ganador = inscripcionesService.listarID(ganadorId);

				enfrentamiento.setGanador(ganador);
				enfrentamientoService.guardarEnfrentamiento(enfrentamiento);

				// Obtiene el torneo ID del último enfrentamiento procesado
				torneoId = enfrentamientoService.obtenerTorneoId(enfrentamientoId);
			}
		}

		// Si no hay torneoId, redirigir a la lista general
		if (torneoId == null) {
			return "redirect:/admin/torneos";
		}

		return "redirect:/admin/verTorneo/" + torneoId;
	}

	@PostMapping("/torneos/generarSemifinal")
	public String generarSemifinal(@RequestParam Integer torneoId, RedirectAttributes redirectAttributes) {
		try {
			Fase faseSemifinal = faseService.listarID(2); // Fase 2 = SEMIFINAL
			enfrentamientoService.generarEnfrentamientosSemifinal(torneoId, faseSemifinal);
			redirectAttributes.addFlashAttribute("success", "Enfrentamientos de SEMIFINAL generados con éxito.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/verTorneo/" + torneoId;
	}

	@PostMapping("/torneos/guardarGanadoresSemifinal")
	public String guardarGanadoresSemifinal(@RequestParam Map<String, String> ganadoresSeleccionados) {
		Integer torneoId = null; // Variable para almacenar el ID del torneo

		for (Map.Entry<String, String> entry : ganadoresSeleccionados.entrySet()) {
			if (entry.getKey().startsWith("ganador_")) {
				int enfrentamientoId = Integer.parseInt(entry.getKey().split("_")[1]);
				int ganadorId = Integer.parseInt(entry.getValue());

				Enfrentamiento enfrentamiento = enfrentamientoService.listarID(enfrentamientoId);
				Inscripciones ganador = inscripcionesService.listarID(ganadorId);

				enfrentamiento.setGanador(ganador);
				enfrentamientoService.guardarEnfrentamiento(enfrentamiento);

				// Obtiene el torneo ID del último enfrentamiento procesado
				torneoId = enfrentamientoService.obtenerTorneoId(enfrentamientoId);
			}
		}

		// Si no hay torneoId, redirigir a la lista general
		if (torneoId == null) {
			return "redirect:/admin/torneos";
		}

		return "redirect:/admin/verTorneo/" + torneoId;
	}

	@PostMapping("/torneos/generarFinal")
	public String generarFinal(@RequestParam Integer torneoId, RedirectAttributes redirectAttributes) {
		try {
			Fase faseFinal = faseService.listarID(3); // Fase 3 = FINAL
			enfrentamientoService.generarFinal(torneoId, faseFinal);
			redirectAttributes.addFlashAttribute("success", "Enfrentamiento de la FINAL generado con éxito.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/verTorneo/" + torneoId;
	}

	@PostMapping("/torneos/guardarGanadorFinal")
	public String guardarGanadorFinal(@RequestParam Map<String, String> ganadorSeleccionado) {
	    Integer torneoId = null; // Variable para almacenar el ID del torneo

	    for (Map.Entry<String, String> entry : ganadorSeleccionado.entrySet()) {
	        if (entry.getKey().startsWith("ganador_")) {
	            int enfrentamientoId = Integer.parseInt(entry.getKey().split("_")[1]);
	            int ganadorId = Integer.parseInt(entry.getValue());

	            Enfrentamiento enfrentamiento = enfrentamientoService.listarID(enfrentamientoId);
	            Inscripciones ganador = inscripcionesService.listarID(ganadorId);

	            enfrentamiento.setGanador(ganador);
	            enfrentamientoService.guardarEnfrentamiento(enfrentamiento);

	            // **Registrar victoria solo si es la final**
	            enfrentamientoService.registrarVictoriaFinal(enfrentamientoId);

	            // **Obtener torneoId**
	            torneoId = enfrentamientoService.obtenerTorneoId(enfrentamientoId);
	        }
	    }

	    // **Si se obtuvo el torneoId, cambiar su estado a "Terminado" (id 3)**
	    if (torneoId != null) {
	        Torneo torneo = torneoService.listarID(torneoId);
	        torneo.setEstadoTorneoId(estadoTorneoService.listarID(3));  // Estado "Terminado"
	        torneoService.guardarTorneo(torneo);
	    }

	    return "redirect:/admin/verTorneo/" + torneoId;
	}


}
