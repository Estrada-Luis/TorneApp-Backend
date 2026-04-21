package util;

import java.io.IOException;
import java.net.ServerSocket;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    // ✅ CONFIGURACIÓN PARA CLEVER CLOUD (LA NUBE)
    private static final String HOST = "bmgyqgpiytixcmnovr8g-mysql.services.clever-cloud.com";
    private static final String DB = "bmgyqgpiytixcmnovr8g";
    private static final String USER = "uuh5lv0z7lkuqegx";
    private static final String PASS = "X04B3oWLty3SIGd4SMN4"; 
    private static final int PORT = 3306;
    
    private static final String SUBPROTOCOL = "jdbc:mysql://";
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public static Session getCurrentSession() {
        if (sessionFactory == null) {
            creaSessionFactory();
        }
        return sessionFactory.getCurrentSession();
    }

    private static void creaSessionFactory() {
        try {
            // Cargamos la configuración base del archivo XML
            Configuration config = new Configuration().configure("hibernate.cfg.xml");

            // Sobrescribimos con los datos de la nube
            incluyePropiedades(config);
            incluyeClases(config);

            sessionFactory = config.buildSessionFactory();
            System.out.println("☁️ CONEXIÓN NUBE: Trabajando sobre Clever Cloud para Pepe y Manolo.");

        } catch (Throwable e) {
            System.err.println("❌ ERROR EN LA SESIÓN REMOTA: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void incluyePropiedades(Configuration config) {
        // Montamos la URL con los nuevos datos del HOST y la DB
        String URL = String.format("%s%s:%d/%s?serverTimezone=UTC", 
                                   SUBPROTOCOL, HOST, PORT, DB);

        config.setProperty(AvailableSettings.URL, URL);
        config.setProperty(AvailableSettings.USER, USER);
        config.setProperty(AvailableSettings.PASS, PASS);
        
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        config.setProperty("hibernate.show_sql", "true");
        config.setProperty("hibernate.current_session_context_class", "thread");
    }

    private static void incluyeClases(Configuration config) {
        config.addAnnotatedClass(modelo.Usuario.class);
        config.addAnnotatedClass(modelo.Federacion.class);
        config.addAnnotatedClass(modelo.Club.class);
        config.addAnnotatedClass(modelo.Entrenador.class);
        config.addAnnotatedClass(modelo.Equipo.class);
        config.addAnnotatedClass(modelo.Jugador.class);
        config.addAnnotatedClass(modelo.Torneo.class);
        config.addAnnotatedClass(modelo.Partido.class);
        config.addAnnotatedClass(modelo.Participa.class);
        config.addAnnotatedClass(modelo.ParticipaId.class);
        config.addAnnotatedClass(modelo.Juega.class);
        config.addAnnotatedClass(modelo.JuegaId.class);
    }
}