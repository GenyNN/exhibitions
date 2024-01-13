package org.fally.einvite.repository;

import java.util.List;
import java.util.UUID;

import org.fally.einvite.model.Asset;
import org.fally.einvite.model.AwardEntity;
import org.fally.einvite.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardEntityRepository extends JpaRepository<AwardEntity, UUID> {

    AwardEntity findByParticipantAndAsset(Participant participant, Asset asset);

    //List<AwardEntity> findByAwardIdentity_Participant(Participant participant);
}
