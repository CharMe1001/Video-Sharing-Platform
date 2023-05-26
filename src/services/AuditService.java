package services;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    // An object that is used to write into csv files.
    private CSVWriter writer;
    private static AuditService instance = null;

    public static AuditService getInstance() {
        if (AuditService.instance == null) {
            AuditService.instance = new AuditService();
        }

        return AuditService.instance;
    }


    // If the audit file exists already, open it with the append flag; otherwise, create it and write the header.
    private AuditService() {
        boolean needToAddHeader = !(new File("audit.csv").isFile());
        try {
            FileWriter fileWriter = new FileWriter("audit.csv", !needToAddHeader);
            this.writer = new CSVWriter(fileWriter);

            if (needToAddHeader) {
                String[] header = {"action", "timestamp"};
                this.writer.writeNext(header);
            }

            this.writer.flush();
        } catch (IOException e) {
            System.out.println("Error opening audit file.");
            System.out.println(e.getMessage());
        }
    }

    // Writes the given string with the writer and automatically flushes it.
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
