package org.fally.einvite.repository;

import org.fally.einvite.model.Asset;
import org.fally.einvite.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID> {
    
    List<Asset> findByEvent(Event event);
}
