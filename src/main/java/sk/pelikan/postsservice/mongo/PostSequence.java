package sk.pelikan.postsservice.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sequences")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostSequence {
    @Id
    private String _id;
    private int seq;

}
