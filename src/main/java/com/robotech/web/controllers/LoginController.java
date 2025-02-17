package com.robotech.web.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.robotech.web.models.Usuario;
import com.robotech.web.services.EmailService;
import com.robotech.web.services.Estado_usuarioService;
import com.robotech.web.services.Tipo_usuarioService;
import com.robotech.web.services.UploadFileService;
import com.robotech.web.services.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private Tipo_usuarioService tipoUsuService;
	@Autowired
	private Estado_usuarioService estadoUsuService;
	@Autowired
	private UploadFileService upload;
	@Autowired
	private EmailService emailService;

	@Value("${app.base-url}")
	private String baseUrl;

	@GetMapping("/logout")
	public String cerrarSesion() {
		return "redirect:/home";
	}

	// INICIAR SESION
	@GetMapping("/login")
	public String login(@RequestParam(value = "error", required = false) String error, Model model,
			@ModelAttribute("registroExitoso") String registroExitoso) {
		if (error != null) {
			model.addAttribute("error", true); // Enviar true si hay error
		} else {
			model.addAttribute("error", false); // Enviar false si no hay error
		}
		model.addAttribute("registroExitoso", registroExitoso);
		return "login";
	}

	// VISTA REGISTRO
	@GetMapping("/registrar")
	public String registrar(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "registro";
	}

	// POST : REGISTRO
	@PostMapping("/registrar")
	public String formRegistro(@ModelAttribute Usuario usuario, Model model, HttpSession session, RedirectAttributes ra,
			@RequestParam("file") MultipartFile file) throws IOException {
		System.out.println("Formulario de registro recibido: " + usuario);

		// Validar campos requeridos
		if (usuario.getNombre() == null || usuario.getNombre().isEmpty() || usuario.getUsername() == null
				|| usuario.getUsername().isEmpty() || usuario.getCorreo() == null || usuario.getCorreo().isEmpty()
				|| usuario.getPassword() == null || usuario.getPassword().isEmpty() || usuario.getTrayectoria() == null
				|| usuario.getTrayectoria() < 0 || usuario.getEdad() == null || usuario.getEdad() < 18
				|| file.isEmpty()) {
			// Validación para la edad y la foto
			model.addAttribute("error", usuario.getEdad() == null || usuario.getEdad() < 18
					? "Debes ser mayor de 18 años para registrarte."
					: "Todos los campos son obligatorios, incluyendo la foto del robot. Por favor, complétalos.");
			return "registro"; // Volver al formulario
		}

		// Validar si el correo ya existe
		if (usuarioService.existeCorreo(usuario.getCorreo())) {
			model.addAttribute("error", "El correo ya está registrado. Por favor, utliza otro correo.");
			return "registro";
		}

		// Validar si el username ya existe
		if (usuarioService.existeUsername(usuario.getUsername())) {
			model.addAttribute("error", "El username ya está en uso. Por favor, elige otro username.");
			return "registro";
		}
		
		// ✅ Validación de la longitud de la contraseña
	    if (usuario.getPassword().length() < 6 || usuario.getPassword().length() > 18) {
	        model.addAttribute("error", "La contraseña debe tener entre 6 y 18 caracteres.");
	        return "registro";
	    }

		// Continuar con el registro
		String passwordEncriptada = new BCryptPasswordEncoder().encode(usuario.getPassword());
		usuario.setPassword(passwordEncriptada);
		usuario.setTipoUsuario(tipoUsuService.listarID(2));
		usuario.setEstadoUsuario(estadoUsuService.listarID(3));

		// Lógica de categorías
		int trayectoria = usuario.getTrayectoria();
		if (trayectoria >= 0 && trayectoria <= 24) {
			usuario.setCategoriaId(usuarioService.AsignarCatAmateur());
		} else if (trayectoria > 24 && trayectoria <= 48) {
			usuario.setCategoriaId(usuarioService.AsignarCatSemiPro());
		} else if (trayectoria > 48) {
			usuario.setCategoriaId(usuarioService.AsignarCatProfesional());
		}

		if (usuario.getId() == null) {
			String nombreImagen = upload.saveImage(file);
			usuario.setFoto_robot(nombreImagen);
		}

		// añadir foto default a usurio recien registrado
		usuario.setFoto_perfil("default.jpg");

		usuarioService.guardarUsuario(usuario);
		session.setAttribute("usuario", usuario.getCorreo());

		ra.addFlashAttribute("registroExitoso", "¡Tu cuenta ha sido registrada con éxito! Inicia sesión.");

		return "redirect:/login";
	}

	@GetMapping("/forgot-password")
	public String mostrarFormularioRecuperacion() {
		return "login/forgot-password";
	}

	// metodo envio de link restrablecimiento de contraseña
	@PostMapping("/forgot-password")
	public String verificarCorreo(@RequestParam("email") String email, RedirectAttributes ra) {
		Usuario usuario = usuarioService.seleccionarCorreoUsuario(email);

		if (usuario == null) {
			ra.addFlashAttribute("error", "No se encontró un usuario con ese correo electrónico.");
			return "redirect:/forgot-password";
		}

		// Generar el token de recuperación
		String token = UUID.randomUUID().toString();
		usuario.setResetToken(token);
		usuario.setResetTokenExpiration(LocalDateTime.now().plusHours(1)); // Válido por 1 hora
		usuarioService.guardarUsuario(usuario);

		// Enviar correo
		String resetLink = baseUrl + "/reset-password?token=" + token; // Ajusta la URL según tu configuración

		emailService.enviarCorreo(usuario.getCorreo(), "Recuperación de contraseña",
				"Hola, " + usuario.getNombre()
						+ "\n\nHaz clic en el siguiente enlace para restablecer tu contraseña: \n" + resetLink
						+ "\n\nSi no solicitaste este cambio, ignora este mensaje.");

		System.out.println(resetLink);
		ra.addFlashAttribute("success", "Se ha enviado un enlace de recuperación a tu correo.");
		return "redirect:/forgot-password";
	}

	// Metodo restablecimiento de contraseña
	@GetMapping("/reset-password")
	public String mostrarFormularioRestablecer(@RequestParam("token") String token, Model model,
			RedirectAttributes ra) {
		Usuario usuario = usuarioService.seleccionarUsuarioPorToken(token);

		if (usuario == null || !usuario.isResetTokenValid()) {
			ra.addFlashAttribute("error", "El enlace de recuperación es inválido o ha expirado.");
			return "redirect:/forgot-password";
		}

		model.addAttribute("token", token);
		return "login/reset-password";
	}

	@PostMapping("/reset-password")
	public String restablecerPassword(@RequestParam("token") String token, @RequestParam("password") String password,
			Model model) {
		Usuario usuario = usuarioService.seleccionarUsuarioPorToken(token);

		if (usuario == null || !usuario.isResetTokenValid()) {
			model.addAttribute("error", "El enlace de recuperación es inválido o ha expirado.");
			return "login/reset-password"; // Muestra el formulario con el mensaje de error
		}
		
		// ✅ Validación de la longitud de la contraseña
	    if (password.length() < 6 || password.length() > 18) {
	        model.addAttribute("error", "La contraseña debe tener entre 6 y 18 caracteres.");
	        return "login/reset-password";
	    }

		// Cambiar la contraseña y limpiar el token
		String passwordEncriptada = new BCryptPasswordEncoder().encode(password);
		usuario.setPassword(passwordEncriptada);
		usuario.setResetToken(null);
		usuario.setResetTokenExpiration(null);
		usuarioService.guardarUsuario(usuario);

		model.addAttribute("success", "Tu contraseña ha sido restablecida exitosamente.");
		return "login/reset-password"; // Muestra el formulario con el mensaje de éxito
	}

}
