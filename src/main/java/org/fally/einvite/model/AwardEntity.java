package org.fally.einvite.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.UUID;

/*
 * Award entity
 */
@Entity
@Table(name = "participant_asset")
public class AwardEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    
    @ManyToOne
    private Asset asset;


    @ManyToOne
    private Participant participant;

    public Participant getParticipant() {
        return this.participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }


    public Asset getAsset() {
        return this.asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AwardEntity)) return false;
        AwardEntity award = (AwardEntity) o;
        return Objects.equals(getAsset(), award.getAsset()) && Objects.equals(getParticipant(), award.getParticipant()) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAsset().hashCode()+getParticipant().hashCode());
    }
    
}
