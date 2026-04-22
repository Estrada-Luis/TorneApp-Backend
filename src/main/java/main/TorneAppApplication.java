package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// Esta anotación le dice a Spring dónde buscar tus @RestController
@ComponentScan(basePackages = {"servicio", "dao", "modelo", "aplicacion", "main", "util"})
// Esta le dice a Hibernate dónde están tus clases de la base de datos
@EntityScan(basePackages = {"modelo"}) 
public class TorneAppApplication {

    public static void main(String[] args) {
        // Esto levanta el servidor web interno (Tomcat)
        SpringApplication.run(TorneAppApplication.class, args);
        
        System.out.println("----------------------------------------------");
        System.out.println("🚀 SERVIDOR TORNEAPP ARRANCADO EXITOSAMENTE");
        System.out.println("Listo para recibir datos del móvil");
        System.out.println("----------------------------------------------");
    }
}