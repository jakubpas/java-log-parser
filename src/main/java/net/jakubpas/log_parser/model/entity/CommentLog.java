package net.jakubpas.log_parser.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;


@Entity(name = "comment_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    public String ip;
    public String comment;
}