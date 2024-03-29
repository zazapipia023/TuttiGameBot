package com.zaza.cleanerexceptionsbot.services;

import com.zaza.cleanerexceptionsbot.models.mongo.Club;
import com.zaza.cleanerexceptionsbot.repositories.mongo.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final ClubRepository clubRepository;


    public Club findOne(String id) {
        Optional<Club> club = clubRepository.findByClubId(id);
        return club.orElse(null);
    }


    @Transactional
    public void save(Club club) {
        clubRepository.save(club);
    }

}
