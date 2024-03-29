package org.fally.einvite.resource;

import org.fally.einvite.exception.InsufficientPermissionException;
import org.fally.einvite.loader.StaffLoader;
import org.fally.einvite.model.Asset;
import org.fally.einvite.model.Award;
import org.fally.einvite.model.AwardEntity;
import org.fally.einvite.model.Event;
import org.fally.einvite.model.Participant;
import org.fally.einvite.model.Permission;
import org.fally.einvite.model.Staff;
import org.fally.einvite.repository.AssetRepository;
import org.fally.einvite.repository.AwardEntityRepository;
import org.fally.einvite.repository.AwardRepository;
import org.fally.einvite.repository.EventRepository;
import org.fally.einvite.repository.ParticipantRepository;
import org.fally.einvite.repository.StaffRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class IndexResource {
    private String applicationVersion;
    private StaffLoader staffLoader;
    private StaffRepository staffRepository;
    private AssetRepository assetRepository;
    private ParticipantRepository participantRepository;
    private EventRepository eventRepository;
    private AwardRepository awardRepository;
    private AwardEntityRepository awardEntityRepository;

    @Inject
    public IndexResource(String applicationVersion,
                         StaffLoader staffLoader,
                         StaffRepository staffRepository,
                         AssetRepository assetRepository,
                         ParticipantRepository participantRepository,
                         EventRepository eventRepository,
                         AwardRepository awardRepository,
                         AwardEntityRepository awardEntityRepository) {
        this.applicationVersion = applicationVersion;
        this.staffLoader = staffLoader;
        this.staffRepository = staffRepository;
        this.assetRepository = assetRepository;
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
        this.awardRepository = awardRepository;
        this.awardEntityRepository = awardEntityRepository;
    }

    @RequestMapping("/")
    public String index(Principal principal, Model model) {
        if (principal != null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("version", applicationVersion);

        return "index";
    }

    @RequestMapping("/login")
    public String login(@RequestParam(required = false) String logout,
                        @RequestParam(required = false) String error,
                        Model model) {

        if (logout != null) {
            model.addAttribute("message", "You have been logged out.");
        }

        if (error != null) {
            model.addAttribute("message", "Invalid credentials.");
        }

        model.addAttribute("version", applicationVersion);

        return "index";
    }

    @RequestMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        model.addAttribute("version", applicationVersion);
        model.addAttribute("secure", staffLoader.isSecure());

        Staff staff = staffRepository
                .findByUsername(principal.getName())
                .orElse(null);

        if (staff != null) {
            List<String> activePermissions = new ArrayList<>();

            Arrays.stream(Permission.values()).forEach(p -> {
                if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(p.name()))) {

                    model.addAttribute(p.getUnique(), true);
                    activePermissions.add(p.getUnique());
                }
            });

            model.addAttribute("staff", staff);
            model.addAttribute("permissions", activePermissions);
        }

        return "dashboard";
    }

    @RequestMapping("/staff")
    public String createStaff(Principal principal, Model model) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.CREATE_STAFF.name()))) {
            throw new InsufficientPermissionException("Not allowed to create staff accounts.");
        }

        model.addAttribute("version", applicationVersion);
        model.addAttribute("permissions", Permission.values());

        return "staffcreate";
    }

    @RequestMapping("/staff/{id}")
    public String editStaff(Principal principal, Model model, @PathVariable String id) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_STAFF.name()))) {
            throw new InsufficientPermissionException("Not allowed to edit staff accounts.");
        }

        UUID uuid = UUID.fromString(id);
        Staff staff = staffRepository
                .findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("No staff member with provided ID"));

        model.addAttribute("version", applicationVersion);
        model.addAttribute("permissions", Permission.values());
        model.addAttribute("staff", staff);

        return "staffedit";
    }

    @RequestMapping("/asset")
    public String createAsset(Principal principal, Model model) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.CREATE_ASSET.name()))) {
            throw new InsufficientPermissionException("Not allowed to create assets.");
        }

        List<Event> events = eventRepository.findAll();

        model.addAttribute("version", applicationVersion);
        model.addAttribute("permissions", Permission.values());
        model.addAttribute("events", events);

        return "assetcreate";
    }

    @RequestMapping("/asset/{id}")
    public String editAsset(Principal principal, Model model, @PathVariable String id) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_ASSET.name()))) {
            throw new InsufficientPermissionException("Not allowed to list assets.");
        }

        UUID uuid = UUID.fromString(id);
        Asset asset = assetRepository
                .findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("No asset with provided ID"));

        List<Event> events = eventRepository.findAll();

        model.addAttribute("version", applicationVersion);
        model.addAttribute("permissions", Permission.values());
        model.addAttribute("events", events);
        model.addAttribute("asset", asset);

        return "assetedit";
    }

    @RequestMapping("/event")
    public String createEvent(Principal principal, Model model) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.CREATE_EVENT.name()))) {
            throw new InsufficientPermissionException("Not allowed to create events.");
        }

        model.addAttribute("version", applicationVersion);
        model.addAttribute("permissions", Permission.values());

        return "eventcreate";
    }

    @RequestMapping("/event/{id}")
    public String editEvent(Principal principal, Model model, @PathVariable String id) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_EVENT.name()))) {
            throw new InsufficientPermissionException("Not allowed to list events.");
        }
        UUID uuid = UUID.fromString(id);
        Event event = eventRepository
                .findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("No event with provided ID"));

                
        List<Asset> assets = assetRepository.findAll();// new ArrayList<Asset>();; findByEvent(event)
        List<Award> awards = awardRepository.findAll();// new ArrayList<Award>(); findByAwardIdentity_Event(event)

        model.addAttribute("version", applicationVersion);
        model.addAttribute("permissions", Permission.values());
        model.addAttribute("event", event);
        model.addAttribute("assets", assets);
        model.addAttribute("awards", awards);

        return "eventedit";
    }

    @RequestMapping("/participant")
    public String createParticipant(Principal principal, Model model) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.CREATE_PARTICIPANT.name()))) {
            throw new InsufficientPermissionException("Not allowed to create participants.");
        }

        List<Asset> assets = assetRepository.findAll();

        model.addAttribute("assets", assets);
        model.addAttribute("version", applicationVersion);
        model.addAttribute("permissions", Permission.values());

        return "participantcreate";
    }

    @RequestMapping("/participant/{id}")
    public String editParticipant(Principal principal, Model model, @PathVariable String id) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_PARTICIPANT.name()))) {
            throw new InsufficientPermissionException("Not allowed to list participants.");
        }

        UUID uuid = UUID.fromString(id);
        Participant participant = participantRepository
                .findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("No participant with provided ID"));

        List<Award> awards = new ArrayList<>();
        List<Asset> assets = assetRepository.findAll();
        List<AwardEntity> awardEntities = new ArrayList<>();//awardEntityRepository.findByAwardIdentity_Participant(participant);

        
        
        model.addAttribute("awards", awards);
        model.addAttribute("version", applicationVersion);
        model.addAttribute("permissions", Permission.values());
        model.addAttribute("participant", participant);
        model.addAttribute("assets", assets);
        model.addAttribute("awardEntities", awardEntities);
        

        return "participantedit";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(IllegalArgumentException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }
}
