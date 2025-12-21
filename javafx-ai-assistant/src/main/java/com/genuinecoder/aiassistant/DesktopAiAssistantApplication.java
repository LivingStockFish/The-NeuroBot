package com.genuinecoder.aiassistant;

import com.genuinecoder.aiassistant.gui.MainApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class DesktopAiAssistantApplication {

    public static void main(String[] args) {
        System.out.println("\n" +
    " _   _                        ____        _   \n" +
    "| \\ | |  ___  _   _ _ __ ___ | __ )  ___ | |_ \n" +
    "|  \\| | / _ \\| | | | '__/ _ \\|  _ \\ / _ \\| __|\n" +
    "| |\\  ||  __/| |_| | | | (_) | |_) | (_) | |_ \n" +
    "|_| \\_|\\____| \\__,_|_|  \\___/|____/ \\___/ \\__|\n");
        System.out.println("\u001B[32m" + "🚀 Application is running!" + "\u001B[0m");
        System.out.println("\u001B[36m" + "🔗 Access the web interface at: http://localhost:8080/project" + "\u001B[0m\n");
        SpringApplication.run(DesktopAiAssistantApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void handleAppStart() {
        MainApplication.launchApplication();
    }
}
