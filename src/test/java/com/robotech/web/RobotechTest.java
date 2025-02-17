package com.robotech.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;

import com.robotech.web.models.Categoria;
import com.robotech.web.models.Estado_usuario;
import com.robotech.web.models.Tipo_usuario;
import com.robotech.web.models.Usuario;
import com.robotech.web.services.CategoriaService;
import com.robotech.web.services.Estado_usuarioService;
import com.robotech.web.services.Tipo_usuarioService;
import com.robotech.web.services.UsuarioService;

@SpringBootTest
@AutoConfigureMockMvc
public class RobotechTest {
	
	@Autowired private MockMvc mockMvc;
	@Mock private UsuarioService usuarioService;
	@Autowired private Tipo_usuarioService tipoUsuarioService;
	@Autowired private Estado_usuarioService estadoUsuarioService;
	@Autowired private CategoriaService categoriaService;
	
	private Usuario usuarioDePrueba;
	
	private Usuario usuarioDePruebaLogin;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@BeforeEach
	public void setUp() {
		usuarioDePrueba = new Usuario();
	    usuarioDePrueba.setId(68);
	    usuarioDePrueba.setNombre("Gian");
	    usuarioDePrueba.setUsername("gian");
	    usuarioDePrueba.setCorreo("gian@gmail.com");
	    usuarioDePrueba.setPassword(passwordEncoder.encode("123456"));
	    usuarioDePrueba.setEdad(25);
	    usuarioDePrueba.setTrayectoria(5);
	    usuarioDePrueba.setFoto_robot("robot.jpg");
	    usuarioDePrueba.setFoto_perfil("perfil.jpg");
	    
	    usuarioDePrueba.setTipoUsuario(tipoUsuarioService.listarID(2)); 
	    usuarioDePrueba.setEstadoUsuario(estadoUsuarioService.listarID(1));
	    usuarioDePrueba.setCategoriaId(categoriaService.listarID(1));
	    
	}
	
	@BeforeEach
	public void setUpLogin() {
	    // Crear el usuario de prueba
		usuarioDePruebaLogin = new Usuario();
		usuarioDePruebaLogin.setId(84);
		usuarioDePruebaLogin.setNombre("Gian1");
		usuarioDePruebaLogin.setUsername("gian1");
		usuarioDePruebaLogin.setCorreo("gian1@gmail.com");
		usuarioDePruebaLogin.setPassword(passwordEncoder.encode("123456"));
		usuarioDePruebaLogin.setEdad(25);
		usuarioDePruebaLogin.setTrayectoria(5);
		usuarioDePruebaLogin.setFoto_robot("robot.jpg");
		usuarioDePruebaLogin.setFoto_perfil("perfil.jpg");
	    
		usuarioDePruebaLogin.setTipoUsuario(tipoUsuarioService.listarID(2)); 
		usuarioDePruebaLogin.setEstadoUsuario(estadoUsuarioService.listarID(1));
		usuarioDePruebaLogin.setCategoriaId(categoriaService.listarID(1));
	    
	    // Guardar el usuario en la base de datos de prueba
	    usuarioService.guardarUsuario(usuarioDePruebaLogin);  // Asegúrate de que el servicio está disponible para guardar el usuario
	}
	
	// TEST CU2: VER COMUNIDAD SIN TEMA
	@Test
    @WithMockUser(username = "gian@gmail.com", roles = "USUARIO") // Simula un usuario autenticado
    public void testVerComunidad_SinTema() throws Exception {
        mockMvc.perform(get("/home/comunidad"))
            .andExpect(status().isOk()) // Ahora debería devolver 200 OK en lugar de 302
            .andExpect(view().name("home/comunidad"))
            .andExpect(model().attributeExists("comunidad"))
            .andExpect(model().attributeExists("temas"))
            .andExpect(model().attribute("tema", (String) null));
    
	}
	
	// TEST CU2: VER COMUNIDAD CON TEMA
    @Test
    @WithMockUser(username = "gian@gmail.com", roles = "USUARIO") // Simula un usuario autenticado
    public void testVerComunidad_ConTema() throws Exception {
        mockMvc.perform(get("/home/comunidad").param("tema", "Robots de pelea"))
            .andExpect(status().isOk())
            .andExpect(view().name("home/comunidad"))
            .andExpect(model().attributeExists("comunidad"))
            .andExpect(model().attributeExists("temas"))
            .andExpect(model().attribute("tema", "Robots de pelea"));
    }
    
    // TEST CU3: AGREGAR COMENTARIO
    @Test
    @Transactional
    public void testGuardarComentario_SinAutenticacion() throws Exception {
        mockMvc.perform(post("/home/comunidad/addcomentario")
                .param("tema", "Robots de Combate")
                .param("comentario", "Este es un comentario de prueba.")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/home/comunidad"))
            .andExpect(flash().attribute("error", "Debes estar registrado para comentar."));
    }
    
 // TEST CU3: AGREGAR COMENTARIO
    @Test
    @Transactional
    @WithMockUser(username = "gian@gmail.com", roles = "USUARIO")
    public void testGuardarComentario_ExitosoConImagen() throws Exception {
        MockMultipartFile imagen = new MockMultipartFile(
            "img", "robot.jpg", "image/jpeg", "contenido de prueba".getBytes()
        );

        // Realiza la solicitud POST directamente sin necesidad de obtener manualmente el token CSRF
        mockMvc.perform(multipart("/home/comunidad/addcomentario")
                .file(imagen)
                .param("tema", "Robots de Combate")
                .param("comentario", "¡Este robot es increíble!")
                .with(csrf()) // Aquí el token CSRF se añadirá automáticamente
                .sessionAttr("usuario", usuarioDePrueba))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/home/comunidad"))
            .andExpect(flash().attribute("success", "El comentario se agregó correctamente."));
    }
    
    
 // TEST CU4 : VER TABLA DE MÉRITO
    @Test
    @WithMockUser(username = "gian@gmail.com", roles = "USUARIO") // Simula un usuario autenticado
    public void testVerTablaMerito() throws Exception {
        mockMvc.perform(get("/home/clasificacion")
                .param("page", "0")
                .param("size", "6"))
            .andExpect(status().isOk()) // Debe retornar 200 OK
            .andExpect(view().name("home/clasificacion"))
            .andExpect(model().attributeExists("victorias"))
            .andExpect(model().attributeExists("currentPage"))
            .andExpect(model().attributeExists("size"))
            .andExpect(model().attributeExists("totalPages"))
            .andExpect(model().attributeExists("totalElements"));
    }
    
    // TEST CU5: REGISTRO (VER VISTA)
    @Test
    public void testAccederVistaRegistro() throws Exception {
        mockMvc.perform(get("/registrar"))
            .andExpect(status().isOk()) // El status esperado es 200 OK
            .andExpect(view().name("registro")) // La vista esperada es "registro"
            .andExpect(model().attributeExists("usuario")); // Debe haber un atributo "usuario" en el modelo
    }
    
 // TEST CU5: REGISTRO CON CAMPOS VACÍOS O INVÁLIDOS
    @Test
    public void testRegistrarConCamposInvalidos() throws Exception {
        // Crear un archivo vacío para simular la carga de un archivo
        MockMultipartFile imagen = new MockMultipartFile(
                "file", "robot.jpg", "image/jpeg", new byte[0] // Archivo vacío
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/registrar")
        		.file(imagen) // Añadir archivo vacío
                .param("nombre", "Gian")
                .param("username", "gian")
                .param("correo", "gian@gmail.com")
                .param("password", "123456")
                .param("trayectoria", "5")
                .param("edad", "17") // Edad inválida
                .with(csrf())) // Incluir CSRF
            .andExpect(status().isOk()) // Status debe ser 200 OK
            .andExpect(view().name("registro")) // Se debe mostrar la vista de registro
            .andExpect(model().attribute("error", "Debes ser mayor de 18 años para registrarte.")); // Error de validación de edad
    }
 // TEST CU5: REGISTRO CON CORREO DUPLICADO
    @Test
    public void testRegistrarConCorreoDuplicado() throws Exception {
        // Crear un archivo vacío para simular la carga de un archivo
        MockMultipartFile imagen = new MockMultipartFile(
                "file", "robot.jpg", "image/jpeg", "contenido de prueba".getBytes() // Archivo vacío
        );

        // Simular que el correo ya está registrado
        mockMvc.perform(MockMvcRequestBuilders.multipart("/registrar")
                .file(imagen) // Archivo vacío
                .param("nombre", "Gian")
                .param("username", "gian")
                .param("correo", "gian@gmail.com") // Correo duplicado
                .param("password", "123456")
                .param("trayectoria", "5")
                .param("edad", "25")
                .param("foto_robot", "robot_default.jpg") // Parámetro de foto_robot
                .with(csrf())) // Incluir CSRF
            .andExpect(status().isOk()) // Status debe ser 200 OK
            .andExpect(view().name("registro")) // Se debe mostrar la vista de registro
            .andExpect(model().attribute("error", "El correo ya está registrado. Por favor, utliza otro correo.")); // Error de correo duplicado
    }
    
 // TEST CU5: REGISTRO CON USERNAME DUPLICADO
    public void testRegistrarConUsernameDuplicado() throws Exception {
        MockMultipartFile imagen = new MockMultipartFile(
                "file", "robot.jpg", "image/jpeg", "contenido de prueba".getBytes() // Archivo con contenido de prueba
        );

        // Simular que el username ya está registrado
        mockMvc.perform(MockMvcRequestBuilders.multipart("/registrar")
                .file(imagen) // Archivo con contenido de prueba
                .param("nombre", "Gian")
                .param("username", "gian") // Username duplicado
                .param("correo", "gian.nuevo@gmail.com") // Correo que no está registrado
                .param("password", "123456")
                .param("trayectoria", "5")
                .param("edad", "25")
                .param("foto_robot", "robot_default.jpg")
                .with(csrf())) // Incluir CSRF
            .andExpect(status().isOk()) // Status debe ser 200 OK
            .andExpect(view().name("registro")) // Se debe mostrar la vista de registro
            .andExpect(model().attribute("error", "El username ya está en uso. Por favor, elige otro username.")); // Error de username duplicado
    }
 
 // TEST CU5: REGISTRO EXITOSO
    @Transactional
    @Test
    public void testRegistrarExitoso() throws Exception {
        MockMultipartFile imagen = new MockMultipartFile(
                "file", "robot.jpg", "image/jpeg", "contenido de prueba".getBytes()
        );

        mockMvc.perform(MockMvcRequestBuilders.multipart("/registrar")
                .file(imagen) // Imagen válida
                .param("nombre", "Gian")
                .param("username", "gianNuevo")
                .param("correo", "gian.nuevo@gmail.com")
                .param("password", "123456")
                .param("trayectoria", "10")
                .param("edad", "25")
                .with(csrf())) // Incluir CSRF
            .andExpect(status().is3xxRedirection()) // Verifica que la respuesta sea una redirección (3xx)
            .andExpect(redirectedUrl("/login")) // Se espera redirección al login
            .andExpect(flash().attribute("registroExitoso", "¡Tu cuenta ha sido registrada con éxito! Inicia sesión.")); // Mensaje de éxito
    }
    
    
    // TEST CU6 : INICIAR SESION EXITOSO
    @Test
    public void testLoginExitoso() throws Exception {
        String correo = "gian1@gmail.com";
        String password = "123456";

        mockMvc.perform(post("/login")
                .param("username", correo)  // Usar "username" si no has personalizado el parámetro
                .param("password", password)
                .with(csrf())  // Incluir CSRF
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())  // Redirección esperada
            .andExpect(redirectedUrl("/home"));  // Verificar la URL de redirección
    }
    
 // TEST CU6 : INICIAR SESION FALLIDO
    @Test
    public void testLoginFallido() throws Exception {
        // Intentar hacer login con credenciales incorrectas
        String correo = "gian1@gmail.com";  // Correo correcto
        String password = "incorrecta";    // Contraseña incorrecta

        mockMvc.perform(post("/login")
                .param("username", correo)  // Usar "username" si no has personalizado el parámetro
                .param("password", password)  // Contraseña incorrecta
                .with(csrf())  // Incluir CSRF
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().is3xxRedirection())  // Redirección esperada
            .andExpect(redirectedUrl("/login?error=true"));  // Se espera redirección a login con error=true
    }
    
    // TEST CU07 : RECUPERAR CONTRASESÑA MOSTRAR VISTA

    @Test
    public void testMostrarFormularioRecuperacion() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())  // Esperamos que la respuesta sea exitosa
                .andExpect(view().name("login/forgot-password"));  // Se espera la vista "login/forgot-password"
    }

 // TEST CU07 : RECUPERAR CONTRASESÑA CORREO NO REGISTRADO
    @Test
    public void testVerificarCorreoNoRegistrado() throws Exception {
        String email = "noexiste@gmail.com";  // Correo no registrado

        mockMvc.perform(post("/forgot-password")
                .param("email", email)  // Correo a verificar
                .with(csrf()))  // Incluir CSRF
            .andExpect(status().is3xxRedirection())  // Redirección esperada
            .andExpect(redirectedUrl("/forgot-password"))  // Se espera redirección al formulario
            .andExpect(flash().attribute("error", "No se encontró un usuario con ese correo electrónico."));  // Mensaje de error
    }
    
    // TEST CU07 : RECUPERAR CONTRASESÑA ENVIAR ENLACE
    @Test
    public void testVerificarCorreoYEnviarEnlace() throws Exception {
        // Suponiendo que "gian@gmail.com" es un correo registrado
        String email = "gian@gmail.com";

        mockMvc.perform(post("/forgot-password")
                .param("email", email)  // Correo registrado
                .with(csrf()))  // Incluir CSRF
            .andExpect(status().is3xxRedirection())  // Redirección esperada
            .andExpect(redirectedUrl("/forgot-password"))  // Se espera redirección al formulario
            .andExpect(flash().attribute("success", "Se ha enviado un enlace de recuperación a tu correo."));  // Mensaje de éxito
    }
    
    // TEST CU07 : RECUPERAR CONTRASEÑA RESTABLECER CON TOKEN

    ////////////////////////////////////////////////////////////////
    //////////////////////NO FUNCIONA////////////////////////////////
    ////////////////////////////////////////////////////////////////
//    @Test
//    public void testMostrarFormularioRestablecerConTokenValido() throws Exception {
//        String token = "validToken";  // Token válido para el usuario
//
//        // Crear un usuario simulado con el token válido
//        Usuario usuarioSimulado = new Usuario();
//        usuarioSimulado.setResetToken(token);
//        usuarioSimulado.setResetTokenExpiration(LocalDateTime.now().plusHours(1));  // Token no expirado
//
//        // Simula la respuesta del servicio
//        when(usuarioService.seleccionarUsuarioPorToken(token)).thenReturn(usuarioSimulado);
//
//        // Ejecutar la solicitud GET con el token directamente en la URL
//        mockMvc.perform(get("/reset-password?token=" + token))  // Token directamente en la URL
//            .andExpect(status().isOk())  // Esperamos que la respuesta sea exitosa
//            .andExpect(view().name("login/reset-password"))  // Se espera la vista "login/reset-password"
//            .andExpect(model().attribute("token", token))  // Verificar que el token se pase al modelo
//            .andDo(print());  // Imprimir la respuesta para depuración
//    }
		////////////////////////////////////////////////////////////////
		//////////////////////NO FUNCIONA////////////////////////////////
		////////////////////////////////////////////////////////////////



}
