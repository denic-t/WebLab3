package org.denic_t.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MainClass {
    public static void main(String[] args) {
        System.out.println("WebLab3 JAR Executable");
        String wildflyDir = "C:\\wildfly-37.0.1.Final";
        File deployDir = new File(wildflyDir, "standalone\\deployments");
        File warFile = new File(deployDir, "WebLab3.war");

        System.out.println("Деплой: Извлечение WebLab3.war из текущего JAR-архива...");
        try (InputStream is = MainClass.class.getResourceAsStream("/WebLab3.war")) {
            if (is != null) {
                Files.copy(is, warFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("WebLab3.war успешно развернут в " + warFile.getAbsolutePath());
            } else {
                System.out.println("Предупреждение: WebLab3.war не найден внутри исполняемого JAR!");
            }
        } catch (IOException e) {
            System.err.println("Ошибка при копировании файла: " + e.getMessage());
        }

        try {
            System.out.println("Запуск WildFly в независимом процессе...");
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", "\"WildFly\"", "standalone.bat");
            pb.directory(new File(wildflyDir, "bin"));
            pb.start();

            System.out.println("Сервер WildFly успешно инициирован.");
            System.out.println("Работа JAR-установщика завершена. Процесс Java уничтожается.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
