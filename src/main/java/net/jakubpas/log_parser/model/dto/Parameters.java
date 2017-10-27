package net.jakubpas.log_parser.model.dto;

import lombok.Data;
import org.springframework.boot.ApplicationArguments;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Data
public class Parameters {
    private Long threshold;
    private Date startDate;
    private Date endDate;
    private String accessLog;
    private ApplicationArguments args;

    public Parameters(ApplicationArguments args) throws ParseException {
        this.args = args;
        if (args.containsOption("startDate")) {
            String date = args.getOptionValues("startDate").get(0);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd.hh:mm:ss");
            this.startDate = formatter.parse(date);
            this.endDate = startDate;
        }
        if (args.containsOption("duration")) {
            String duration = args.getOptionValues("duration").get(0);
            if (duration.equals("hourly")) {
                this.endDate = new Date(this.startDate.getTime() + TimeUnit.HOURS.toMillis( 1 ));
            } else if (duration.equals("daily")) {
                this.endDate = new Date(this.startDate.getTime() + TimeUnit.DAYS.toMillis( 1 ));
            }
        }
        if (args.containsOption("threshold")) {
            this.threshold = new Long(args.getOptionValues("threshold").get(0));
        }
        if (args.containsOption("accesslog")) {
            this.accessLog = args.getOptionValues("accesslog").get(0);
        }
    }

    public boolean requiredPresent() {
        return  (this.args.containsOption("startDate") && this.args.containsOption("duration") && this.args.containsOption("threshold"));
    }
}
