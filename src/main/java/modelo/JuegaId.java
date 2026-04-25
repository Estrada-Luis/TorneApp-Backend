package modelo;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

@Embeddable
public class JuegaId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "id_equipo")
    private int idEquipo;

    @Column(name = "id_partido")
    private int idPartido;

    public JuegaId() {}

    public JuegaId(int idEquipo, int idPartido) {
        this.idEquipo = idEquipo;
        this.idPartido = idPartido;
    }

    // Getters y Setters (Necesarios para el mapeo correcto)
    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }
    public int getIdPartido() { return idPartido; }
    public void setIdPartido(int idPartido) { this.idPartido = idPartido; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JuegaId that = (JuegaId) o;
        return idEquipo == that.idEquipo && idPartido == that.idPartido;
    }

    @Override
    public int hashCode() {
        // Usar Objects.hash genera un valor mucho más único y seguro para Hibernate
        return Objects.hash(idEquipo, idPartido);
    }
}