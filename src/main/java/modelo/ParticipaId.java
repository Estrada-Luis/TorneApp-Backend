package modelo;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ParticipaId implements Serializable {

    private int idEquipo;
    private int idTorneo;

    public ParticipaId() {
    }

    public ParticipaId(int idEquipo, int idTorneo) {
        this.idEquipo = idEquipo;
        this.idTorneo = idTorneo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipaId)) return false;
        ParticipaId that = (ParticipaId) o;
        return idEquipo == that.idEquipo && idTorneo == that.idTorneo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEquipo, idTorneo);
    }
}
