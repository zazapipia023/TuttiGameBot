package com.zaza.cleanerexceptionsbot.models.mongo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "club")
@Setter
@Getter
@NoArgsConstructor
public class Club {

    @Id
    private ObjectId id;

    private String clubId;

    private Long connectedClient;

    private LocalDateTime subDate;

    private Map<String, String> steamGames;

    private List<String> egsGames;

    private List<String> vkGames;

    private List<String> ubisoftGames;

    private List<String> battleNetGames;

}
