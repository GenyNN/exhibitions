package org.fally.einvite.resource;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
    
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.fally.einvite.model.AwardEntity;
import org.fally.einvite.model.Participant;
import org.fally.einvite.model.Asset;
import org.fally.einvite.model.transport.AwardAssetChangeRequest;
import org.fally.einvite.model.transport.EventAddParticipantRequest;
import org.fally.einvite.repository.AssetRepository;
import org.fally.einvite.repository.AwardEntityRepository;
import org.fally.einvite.repository.ParticipantRepository;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/v1/participant/{id}/award")
public class ParticipantAwardResource {

    private ParticipantRepository participantRepository;
    private AssetRepository assetRepository;
    private AwardEntityRepository awardEntityRepository;

    @Inject
    public ParticipantAwardResource(ParticipantRepository participantRepository, AssetRepository assetRepository, AwardEntityRepository awardEntityRepository) {
        this.participantRepository = participantRepository;
        this.assetRepository = assetRepository;
        this.awardEntityRepository = awardEntityRepository;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public void assignAward(@PathVariable("id") UUID participantId, 
        
        @Valid AwardAssetChangeRequest awardAssetChangeRequest,
        /*AwardAssetChangeRequest changeRequest,*/ 
        Principal principal,
        HttpServletRequest httpServletRequest) {
        AwardEntity awardEntity = new AwardEntity();

        Participant participant = participantRepository
            .findById(participantId)
            .orElseThrow(() -> new NullPointerException("No such participant found."));

        Asset asset = assetRepository
            .findById(awardAssetChangeRequest.getAssetId())
            .orElseThrow(() -> new NullPointerException("No such asset found."));

                
        awardEntity.setParticipant(participant);
        awardEntity.setAsset(asset);

        awardEntityRepository.save(awardEntity);
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addAsset(@Valid EventAddParticipantRequest eventAddParticipantRequest,
                                              BindingResult bindingResult,
                                              @PathVariable("id") UUID eventId,
                                              Principal principal,
                                              HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        return response;
    }
}
