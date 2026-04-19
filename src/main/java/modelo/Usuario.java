package modelo;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private int idUsuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    // --- AÑADE ESTO PARA QUE DEJE DE SALIR ROJO EN EL SERVICIO ---
    @Column(nullable = false)
    private boolean validado = false; 

    public Usuario() {}

    public Usuario(String nombre, String email, String password, RolUsuario rol) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // --- GETTERS Y SETTERS ---
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    // --- AÑADE ESTOS DOS MÉTODOS NUEVOS ---
    public boolean isValidado() { return validado; }
    public void setValidado(boolean validado) { this.validado = validado; }

    @Override
    public String toString() {
        return "Usuario [Nombre=" + nombre + ", Email=" + email + ", Rol=" + rol + "]";
    }
}