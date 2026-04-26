package dao;

import java.util.List;
import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import modelo.Torneo;
import modelo.Partido;
import modelo.Equipo;
import util.HibernateUtil;

public class TorneoDAO extends DAO<Torneo> {

    public TorneoDAO() {
        super(Torneo.class);
    }

    /**
     * 🔍 LEER TORNEO FORZANDO REFRESCO
     */
    public Torneo read(int id) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            Torneo t = session.get(Torneo.class, id);
            if (t != null) {
                t.getEquipos().size(); 
            }
            session.getTransaction().commit();
            return t;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return null;
        }
    }

    /**
     * 🏁 GENERAR FASE FINAL (Escritorio)
     */
    public void generarFaseFinal(int idTorneo) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            session.createNativeQuery("CALL generar_fase_final(:id)")
                   .setParameter("id", idTorneo)
                   .executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
        }
    }

    /**
     * ✅ LISTAR TORNEOS (Escritorio/Móvil)
     */
    @Override
    public List<Torneo> readAll() {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            List<Torneo> lista = session.createQuery("SELECT DISTINCT t FROM Torneo t LEFT JOIN FETCH t.equipos", Torneo.class)
                                        .getResultList();
            session.getTransaction().commit();
            return lista;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return new ArrayList<>();
        }
    }

    // --- MÉTODOS DE FILTRADO (Móvil/Escritorio) ---

    public List<Torneo> listarInscritosPorClub(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            String sql = "SELECT DISTINCT t.* FROM torneo t " +
                         "INNER JOIN inscripcion i ON t.id_torneo = i.id_torneo " +
                         "INNER JOIN equipo e ON i.id_equipo = e.id_equipo " +
                         "WHERE e.id_club = :idClub";
            List<Torneo> lista = session.createNativeQuery(sql, Torneo.class)
                                        .setParameter("idClub", idClub)
                                        .getResultList();
            session.getTransaction().commit();
            return lista;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return new ArrayList<>();
        }
    }

    public List<Torneo> listarOrganizadosPorClub(int idClub) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            List<Torneo> lista = session.createNativeQuery("SELECT * FROM torneo WHERE id_club = :idClub", Torneo.class)
                                        .setParameter("idClub", idClub)
                                        .getResultList();
            session.getTransaction().commit();
            return lista;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return new ArrayList<>();
        }
    }

    public Partido getPartidoById(int id) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            Partido p = session.get(Partido.class, id);
            session.getTransaction().commit();
            return p;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return null;
        }
    }

    public void updatePartido(Partido p) {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            session.update(p);
            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
        }
    }

    // 🚩 CORRECCIÓN: Ahora con gestión de transacciones para evitar el error de IntelliJ
    public List<Torneo> listarTorneosVisibles() {
        Session session = HibernateUtil.getCurrentSession();
        try {
            session.beginTransaction();
            String hql = "FROM Torneo WHERE estado != 'PENDIENTE' ORDER BY fechaInicio ASC";
            List<Torneo> resultados = session.createQuery(hql, Torneo.class).getResultList();
            session.getTransaction().commit();
            return resultados;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            return new ArrayList<>();
        }
    }

    // 🚩 CORRECCIÓN: Ahora con gestión de transacciones para evitar el error de IntelliJ
    
    public List<Torneo> buscarPorEstado(String estado) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            // 1. Traemos TODOS los torneos (menos los pendientes de validar)
            List<Torneo> todos = session.createQuery("FROM Torneo WHERE estado != 'PENDIENTE'", Torneo.class).getResultList();
            
            List<Torneo> filtrados = new ArrayList<>();
            java.time.LocalDate hoy = java.time.LocalDate.now();

            for (Torneo t : todos) {
                try {
                    // Limpiamos y parseamos las fechas (soporta barras y guiones)
                    java.time.LocalDate inicio = parsearFechaCualquiera(t.getFechaInicio());
                    java.time.LocalDate fin = parsearFechaCualquiera(t.getFechaFin());

                    // LÓGICA DE ESTADOS AUTOMÁTICA
                    String estadoReal;
                    if (fin.isBefore(hoy)) {
                        estadoReal = "FINALIZADO";
                    } else if (inicio.isAfter(hoy)) {
                        estadoReal = "PROXIMO";
                    } else {
                        estadoReal = "EN CURSO";
                    }

                    // 2. Si el estado que hemos calculado coincide con lo que buscas, lo añadimos
                    if (estadoReal.equals(estado.toUpperCase())) {
                        // Aprovechamos para actualizar el estado en la base de datos si ha cambiado
                        if (!t.getEstado().equals(estadoReal)) {
                            t.setEstado(estadoReal);
                            session.update(t);
                        }
                        filtrados.add(t);
                    }
                } catch (Exception e) {
                    // Si la fecha es un texto que no se puede leer, tiramos del estado escrito
                    if (t.getEstado().equalsIgnoreCase(estado)) filtrados.add(t);
                }
            }

            tx.commit();
            return filtrados;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Método "todoterreno" para entender cualquier fecha
     */
    private java.time.LocalDate parsearFechaCualquiera(String fecha) {
        if (fecha == null || fecha.isEmpty()) return java.time.LocalDate.MAX;
        
        // Quitar posibles espacios
        fecha = fecha.trim();
        
        try {
            if (fecha.contains("/")) {
                // Caso 25/04/2026
                return java.time.LocalDate.parse(fecha, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else {
                // Caso 2026-04-25
                return java.time.LocalDate.parse(fecha);
            }
        } catch (Exception e) {
            return java.time.LocalDate.MAX;
        }
    }
}