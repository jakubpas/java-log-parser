package net.jakubpas.log_parser.service;

import net.jakubpas.log_parser.model.dto.Parameters;
import net.jakubpas.log_parser.model.entity.CommentLog;
import net.jakubpas.log_parser.model.entity.Log;
import net.jakubpas.log_parser.repository.CommentLogRepository;
import net.jakubpas.log_parser.repository.LogRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;
    @Autowired
    private CommentLogRepository commentLogRepository;

    @Autowired
    private EntityManager em;

    public void saveLogs(List<Log> logs) {
        logRepository.save(logs);
    }

    public String printMessage() {
        return "Usage: java -jar target/log_parser-0.0.1-SNAPSHOT.jar --accesslog=access.log" +
                "  --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=200";
    }

    public List<Log> getLogFromFile(String file) throws IOException, ParseException {
        List<Log> logs = new ArrayList<>();
        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withDelimiter('|').parse(in);
        for (CSVRecord record : records) {
            Log log = new Log();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            String dateString = record.get(0);
            dateString = dateString.replace("\uFEFF", ""); // remove UTF BOM
            Date date = formatter.parse(dateString);
            log.setDate(date);

            log.setIp(record.get(1));
            log.setMethod(record.get(2));
            log.setResponse(record.get(3));
            log.setUserAgent(record.get(4));
            logs.add(log);
        }
        return logs;
    }

    public List<Log> find(Parameters params) throws ParseException{

        TypedQuery<Log> queryString = em.createQuery("SELECT l FROM log l " +
                        " WHERE l.date BETWEEN :startDate AND :endDate " +
                "GROUP BY l.ip HAVING count(l.ip) >= :threshold"
                , Log.class).
                setParameter("startDate", params.getStartDate()).
                setParameter("endDate", params.getEndDate()).
                setParameter("threshold", params.getThreshold());
        return queryString.getResultList();
    }

    public List<CommentLog> saveComments(Parameters params, List<Log> logs) {
        List<CommentLog> commentLogs = new ArrayList<>();
        logs.forEach(log -> {
            CommentLog commentLog = new CommentLog();
            commentLog.setIp(log.getIp());
            commentLog.setComment("The IP: " + log.getIp() + " has reached more than " + params.getThreshold().toString() +
            " requests between " + params.getStartDate().toString() + " and " + params.getEndDate().toString());
            commentLogs.add(commentLog);
        });
        commentLogRepository.save(commentLogs);
     return commentLogs;
    }
}
