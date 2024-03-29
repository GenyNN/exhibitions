package org.fally.einvite.resource;

import org.fally.einvite.exception.InsufficientPermissionException;
import org.fally.einvite.model.Asset;
import org.fally.einvite.model.Award;
import org.fally.einvite.model.AwardIdentity;
import org.fally.einvite.model.Event;
import org.fally.einvite.model.Participant;
import org.fally.einvite.model.Permission;
import org.fally.einvite.model.transport.AwardAssetChangeRequest;
import org.fally.einvite.model.transport.EventAddParticipantRequest;
import org.fally.einvite.repository.AssetRepository;
import org.fally.einvite.repository.AwardRepository;
import org.fally.einvite.repository.EventRepository;
import org.fally.einvite.repository.ParticipantRepository;
import org.fally.einvite.service.AuditLogService;
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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/event/{id}/participant")
public class EventParticipantResource {
    private EventRepository eventRepository;
    private ParticipantRepository participantRepository;
    private AssetRepository assetRepository;
    private AwardRepository awardRepository;
    private AuditLogService auditLogService;

    @Inject
    public EventParticipantResource(
            EventRepository eventRepository,
            ParticipantRepository participantRepository,
            AssetRepository assetRepository,
            AwardRepository awardRepository,
            AuditLogService auditLogService) {

        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.assetRepository = assetRepository;
        this.awardRepository = awardRepository;
        this.auditLogService = auditLogService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addParticipant(@Valid EventAddParticipantRequest eventAddParticipantRequest,
                                              BindingResult bindingResult,
                                              @PathVariable("id") UUID eventId,
                                              Principal principal,
                                              HttpServletRequest request) {

        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_EVENT.name()))) {
            throw new InsufficientPermissionException("Not allowed to edit events.");
        }

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            throw new ValidationException(errorMessages);
        }

        Participant participant = participantRepository
                .findById(eventAddParticipantRequest.getParticipantId())
                .orElseThrow(() -> new NullPointerException("No such participant found."));

        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NullPointerException("No such event found."));

        Award award = new Award();
        award.setAwardIdentity(new AwardIdentity(event, participant));

        awardRepository.save(award);

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Added participant to event: %s (%s) -> %s (%s)",
                        participant.getLastName() + ", " + participant.getFirstName(),
                        participant.getId(),
                        event.getName(),
                        event.getId()));

        Map<String, Object> response = new HashMap<>();

        response.put("participant", participant);
        response.put("eventId", event.getId());

        return response;
    }

    @RequestMapping(value = "/{participantId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeParticipant(@PathVariable("id") UUID eventId,
                                   @PathVariable("participantId") UUID participantId,
                                   Principal principal,
                                   HttpServletRequest httpServletRequest) {

                                        
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_EVENT.name()))) {
            throw new InsufficientPermissionException("Not allowed to edit events.");
        }

        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NullPointerException("No such event found."));

        Participant participant = participantRepository
                .findById(participantId)
                .orElseThrow(() -> new NullPointerException("No such participant found."));

                
        Award award = awardRepository
                .findByAwardIdentity_EventAndAwardIdentity_Participant(event, participant)
                .orElseThrow(() -> new NullPointerException("No such award found."));

        awardRepository.delete(award);

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(httpServletRequest),
                String.format("Removed participant from event: %s (%s) -> %s (%s)",
                        participant.getLastName() + ", " + participant.getFirstName(),
                        participant.getId(),
                        event.getName(),
                        event.getId()));
    }

    @RequestMapping(value = "/{participantId}/asset", method = RequestMethod.POST)
    @ResponseBody
    public void assignAsset(@PathVariable("id") UUID eventId,
                             @PathVariable("participantId") UUID participantId,
                             AwardAssetChangeRequest changeRequest,
                             Principal principal,
                             HttpServletRequest httpServletRequest) {

        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_EVENT.name()))) {
            throw new InsufficientPermissionException("Not allowed to edit events.");
        }

        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NullPointerException("No such event found."));

        Participant participant = participantRepository
                .findById(participantId)
                .orElseThrow(() -> new NullPointerException("No such participant found."));

        Asset asset = changeRequest.getAssetId() == null ? null : assetRepository
                .findById(changeRequest.getAssetId())
                .orElseThrow(() -> new NullPointerException("No such asset found."));

        
        Award award = awardRepository
                .findByAwardIdentity_EventAndAwardIdentity_Participant(event, participant)
                .orElseThrow(() -> new NullPointerException("No such award found."));

        award.setAsset(asset);

        awardRepository.save(award);

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(httpServletRequest),
                String.format("Changed award: %s (%s), %s (%s) -> %s (%s)",
                        participant.getLastName() + ", " + participant.getFirstName(),
                        participant.getId(),
                        event.getName(),
                        event.getId() ,
                        award.getAsset() == null ? "None" : award.getAsset().getName(),
                        award.getAsset() == null ? "N/A" : award.getAsset().getId()));
    }

    @ExceptionHandler(NullPointerException.class)
    public void handleNotFoundException(NullPointerException ex, HttpServletResponse response) throws Exception {
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(InsufficientPermissionException.class)
    public void handleInsufficientPermissionException(InsufficientPermissionException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public void handleValidationException(ValidationException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleDataIntegrityViolationException(@SuppressWarnings("unused") DataIntegrityViolationException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), "Could not perform the requested operation due to a constraint in the database.");
    }
}
