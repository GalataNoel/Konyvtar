package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class Main {

    private final Environment env;

    public Main(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");

        log.info("🚀 Könyvtár alkalmazás sikeresen elindult!");
        log.info("📚 Elérhető címek:");
        log.info("   • Könyvek API: http://localhost:{}{}/api/books", port, contextPath);
        log.info("   • Szerzők API: http://localhost:{}{}/api/authors", port, contextPath);
        log.info("   • H2 Konzol: http://localhost:{}{}/h2-console", port, contextPath);
        log.info("💡 Használjon HTTP klienst (Postman, curl) a teszteléshez!");
    }
}