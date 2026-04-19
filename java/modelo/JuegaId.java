package modelo;

import java.io.Serializable;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JuegaId)) return false;
        JuegaId that = (JuegaId) o;
        return idEquipo == that.idEquipo &&
               idPartido == that.idPartido;
    }

    @Override
    public int hashCode() {
        return idEquipo + idPartido;
    }
}
