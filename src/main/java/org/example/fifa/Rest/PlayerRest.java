package org.example.fifa.Rest;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerRest {
    private String id;
    private String name;
    private int number;
    private String position;
    private String nationality;
    private int age;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ClubRest club;
}

