package net.jakubpas.log_parser;

import net.jakubpas.log_parser.model.entity.CommentLog;
import net.jakubpas.log_parser.model.entity.Log;
import net.jakubpas.log_parser.service.LogService;
import net.jakubpas.log_parser.model.dto.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class LogParserApplication implements ApplicationRunner {

    @Autowired
    LogService logService;

    public static void main(String... args) throws Exception {
        SpringApplication.run(LogParserApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Parameters parameters = new Parameters(args);

        if (parameters.getAccessLog() != null) {
            try {
                logService.saveLogs(logService.getLogFromFile(parameters.getAccessLog()));
                System.out.println("Data loaded successfully");
            } catch (Exception e) {
                System.out.println("Problem while loading data. " + e.getMessage());
            }
        }
        if (parameters.requiredPresent()) {
            List<Log> logs = logService.find(parameters);
            List<CommentLog> comments = logService.saveComments(parameters, logs);
            comments.forEach(commentLog -> System.out.println(commentLog.getComment()));
        } else {
            System.out.println(logService.printMessage());
        }
    }
}