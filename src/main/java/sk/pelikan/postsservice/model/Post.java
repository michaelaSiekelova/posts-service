package sk.pelikan.postsservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.pelikan.postsservice.mongo.SequenceGeneratorService;

@Document(collection = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    private Integer id;
    private Integer userId;
    private String title;
    private String body;

    public static final String SEQUENCE_ID= "posts";

}
