package sk.pelikan.postsservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

    private Integer id;

    @NotNull(message = "User Id je povinný údaj")
    private Integer userId;

    @NotBlank(message = "Title je povinný údaj")
    private String title;

    @NotEmpty(message = "Body je povinný údaj")
    private String body;
}
