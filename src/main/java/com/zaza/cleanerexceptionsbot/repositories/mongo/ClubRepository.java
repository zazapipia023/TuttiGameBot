package com.zaza.cleanerexceptionsbot.repositories.mongo;

import com.zaza.cleanerexceptionsbot.models.mongo.Club;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClubRepository extends MongoRepository<Club, String> {

    Optional<Club> findByClubId(String clubId);
}
