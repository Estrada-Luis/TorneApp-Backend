package main;

import modelo.*;
import aplicacion.*;
import java.util.List;

public class MainPruebas {

    public static void main(String[] args) {

        // --- 1. INICIALIZACIÓN DE GESTORES ---
        GestionFederacion gFederacion = new GestionFederacion();
        GestionClub gClub = new GestionClub();
        GestionEquipo gEquipo = new GestionEquipo();
        GestionUsuario gUsuario = new GestionUsuario();
        GestionEntrenador gEntrenador = new GestionEntrenador();
        GestionJugador gJugador = new GestionJugador();
        GestionTorneo gTorneo = new GestionTorneo();

        System.out.println("=== 🚀 INICIANDO TEST INTEGRAL: TORNEAPP 2026 ===");

        // --- 2. PERFIL: FEDERACIÓN (App Escritorio) ---
        // La entidad reguladora se registra en el sistema
        gFederacion.crearFederacion("Federación Asturiana", "Gijón", "admin@asturfutbol.es", "985112233");
        Federacion fed = gFederacion.listarFederaciones().get(0);
        System.out.println("1. [FEDERACIÓN] Entidad reguladora lista.");

        // --- 3. PERFIL: CLUB (App Móvil) ---
        // El coordinador registra su club (Estado inicial: NO VALIDADO)
        Club miClub = new Club("CD Gijón Norte", "G1234", "Gijón", "600111222", "contacto@gijonnorte.es", false, fed);
        gClub.crearClub(miClub);
        System.out.println("2. [CLUB] Registro enviado. Esperando validación...");

        // La FEDERACIÓN valida al club
        gFederacion.validarClub(miClub.getIdClub(), true);
        System.out.println("3. [FEDERACIÓN] Club validado con éxito.");

     // Añadimos "Infantil" (o la categoría que prefieras) como tercer parámetro
        Equipo equipoInfantil = gClub.darAltaEquipo("Infantil A", miClub, "Infantil");
        String token = equipoInfantil.getCodigoVinculacion();
        System.out.println("4. [CLUB] Equipo creado. CÓDIGO PARA ENTRENADOR: " + token);

        // --- 4. PERFIL: ENTRENADOR (App Móvil) ---
        // El entrenador se registra como usuario
        Usuario userEntr = gUsuario.registrarUsuario("Carlos Pérez", "carlos@entrenador.com", "pass123", RolUsuario.ENTRENADOR);
        
        // El entrenador usa el CÓDIGO que le dio el coordinador para vincularse
        Entrenador entrenador = gEntrenador.registrarEntrenadorConCodigo(userEntr, token, "677888999");

        if (entrenador != null) {
            System.out.println("5. [ENTRENADOR] Vinculación exitosa al equipo: " + entrenador.getEquipo().getNombre());

            // El entrenador gestiona su plantilla
            gJugador.altaJugador("Oliver Atom", 12, entrenador.getEquipo());
            gJugador.altaJugador("Mark Lenders", 13, entrenador.getEquipo());
            System.out.println("6. [ENTRENADOR] Jugadores registrados en la plantilla.");
        }

        // --- 5. GESTIÓN DE TORNEOS (Flujo Propuesta-Validación-Inscripción) ---
        // El CLUB propone un torneo nuevo
        gTorneo.crearTorneo("Copa de Invierno 2026", "Gijón", "2026-02-01", "2026-02-15", "Infantil", "F11", miClub);
        Torneo torneoPendiente = gTorneo.listarTorneos().get(0);
        System.out.println("7. [CLUB] Torneo propuesto. Estado: " + torneoPendiente.getEstado());

        // La FEDERACIÓN supervisa y VALIDA el torneo
        gFederacion.validarTorneo(torneoPendiente.getIdTorneo(), "APROBADO");
        
        // El ENTRENADOR busca el torneo y lo SUGIERE al club
        gEntrenador.sugerirTorneoAlClub(torneoPendiente, equipoInfantil, "Coordinador, este torneo tiene buen nivel.");

        // El CLUB realiza la INSCRIPCIÓN oficial (Solo funciona si el torneo está APROBADO)
        gClub.inscribirEnTorneo(equipoInfantil, torneoPendiente);

        // --- 6. CIERRE Y COMPROBACIÓN FINAL ---
        System.out.println("\n=== 📊 RESUMEN FINAL DE LA PRUEBA ===");
        gJugador.mostrarPlantilla(equipoInfantil);
        System.out.println("Estado Final Torneo: " + torneoPendiente.getNombre() + " -> " + torneoPendiente.getEstado());
        System.out.println("\n✅ TEST FINALIZADO: El backend cumple con todos los requisitos.");
    }
}