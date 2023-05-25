package services;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    private FileWriter fw;
    private CSVWriter writer;
    private static AuditService instance = null;

    public static AuditService getInstance() {
        if (AuditService.instance == null) {
            AuditService.instance = new AuditService();
        }

        return AuditService.instance;
    }



    private AuditService() {
        boolean needToAddHeader = !(new File("audit.csv").isFile());
        try {
            this.fw = new FileWriter("audit.csv", !needToAddHeader);
            this.writer = new CSVWriter(this.fw);

            if (needToAddHeader) {
                String[] header = {"action", "timestamp"};
                this.writer.writeNext(header);
            }
        } catch (IOException e) {
            System.out.println("Error opening audit file.");
            System.out.println(e.getMessage());
        }
    }

    public void writeAction(String action) {
        String[] entry = new String[]{action, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now())};
        try {
            this.writer.writeNext(entry);
            this.writer.flush();
        } catch (IOException e) {
            System.out.println("Error auditing action: \"" + action + "\".");
            System.out.println(e.getMessage());
        }
    }
}
