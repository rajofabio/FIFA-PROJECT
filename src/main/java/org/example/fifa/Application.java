package org.example.fifa;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        DataSource ds = new DataSource();
        try (Connection conn = ds.getConnection()) {
            System.out.println("Connexion réussie !");
        } catch (Exception e) {
            System.err.println(" Connexion échouée : " + e.getMessage());
        }
    }

}
