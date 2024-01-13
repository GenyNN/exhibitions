package org.fally.einvite.repository;

import org.fally.einvite.model.Award;
import org.fally.einvite.model.Event;
import org.fally.einvite.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AwardRepository extends JpaRepository<Award, UUID> {
    List<Award> findByAwardIdentity_Event(Event event);
    Optional<Award> findByAwardIdentity_EventAndAwardIdentity_Participant(Event event, Participant participant);
}
