package com.zaza.tuttifruttibot.services;

import com.zaza.tuttifruttibot.models.IceShop;
import com.zaza.tuttifruttibot.models.Player;
import com.zaza.tuttifruttibot.repositories.IceShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IceShopService {

    private final IceShopRepository iceShopRepository;

    public IceShop findOne(Long id) {
        Optional<IceShop> foundShop = iceShopRepository.findById(id);

        if (foundShop.isPresent()) {
            IceShop iceShop = foundShop.get();
            return iceShop;
        } else {
            return null;
        }
    }

    @Transactional
    public void save(IceShop iceShop) {
        try {
            IceShop savedIceShop = iceShopRepository.save(iceShop);
        } catch (Exception e) {
            throw new RuntimeException("Error saving shop to database", e);
        }
    }

    @Transactional
    public void saveAll(List<IceShop> iceShopList) {
        iceShopRepository.saveAll(iceShopList);
    }

    public List<IceShop> findAllByPlayerId(Player player) {
        return iceShopRepository.findAllByPlayer(player);
    }

    public List<IceShop> findAllShops() {
        return iceShopRepository.findAll();
    }
}
