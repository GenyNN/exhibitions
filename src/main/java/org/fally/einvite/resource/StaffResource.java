package org.fally.einvite.resource;

import org.fally.einvite.exception.InsufficientPermissionException;
import org.fally.einvite.loader.StaffLoader;
import org.fally.einvite.model.Permission;
import org.fally.einvite.model.Staff;
import org.fally.einvite.model.transport.StaffCreateRequest;
import org.fally.einvite.model.transport.StaffEditRequest;
import org.fally.einvite.repository.StaffRepository;
import org.fally.einvite.service.AuditLogService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/staff")
public class StaffResource {
    private StaffRepository staffRepository;
    private StaffLoader staffLoader;
    private AuditLogService auditLogService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Inject
    public StaffResource(StaffRepository staffRepository,
                         StaffLoader staffLoader,
                         AuditLogService auditLogService,
                         BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.staffRepository = staffRepository;
        this.staffLoader = staffLoader;
        this.auditLogService = auditLogService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @RequestMapping("")
    @ResponseBody
    public List<Staff> fetchStaff(Principal principal) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.LIST_STAFF.name()))) {
            throw new InsufficientPermissionException("Not allowed to list staff accounts.");
        }

        List<Staff> results = staffRepository.findAll();

        // mask the password hashes
        results.forEach(staff -> staff.setPassword("********"));

        return results;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Staff createStaff(@Valid StaffCreateRequest staffCreateRequest, BindingResult bindingResult, Principal principal, HttpServletRequest request) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.CREATE_STAFF.name()))) {
            throw new InsufficientPermissionException("Not allowed to create staff accounts.");
        }

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            throw new ValidationException(errorMessages);
        }

        Staff staff = new Staff();

        staff.setUsername(staffCreateRequest.getUsername());
        staff.setPassword(bCryptPasswordEncoder.encode(staffCreateRequest.getPassword()));
        staffCreateRequest.getPermissions().forEach(p -> staff.addPermission(Permission.valueOf(p.replace("-", "_").toUpperCase())));

        Staff savedStaff = staffRepository.save(staff);
        staffLoader.evaluateSecurity();

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Created staff member: %s (%s)",
                        savedStaff.getUsername(),
                        savedStaff.getId()));

        return savedStaff;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Staff updateStaff(@PathVariable String id, @Valid StaffEditRequest staffEditRequest, BindingResult bindingResult, Principal principal, HttpServletRequest request) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_STAFF.name()))) {
            throw new InsufficientPermissionException("Not allowed to edit staff accounts.");
        }

        UUID uuid = UUID.fromString(id);
        Staff staff = staffRepository
                .findById(uuid)
                .orElseThrow(() -> new NullPointerException("No such staff account found."));

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            throw new ValidationException(errorMessages);
        }

        staff.setUsername(staffEditRequest.getUsername());

        Arrays.stream(Permission.values()).forEach(staff::removePermission);
        staffEditRequest.getPermissions().forEach(p -> staff.addPermission(Permission.valueOf(p.replace("-", "_").toUpperCase())));

        if (!StringUtils.isEmpty(staffEditRequest.getPassword())) {
            staff.setPassword(bCryptPasswordEncoder.encode(staffEditRequest.getPassword()));
        }

        Staff savedStaff = staffRepository.save(staff);
        staffLoader.evaluateSecurity();

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Edited staff member: %s (%s)",
                        staff.getUsername(),
                        staff.getId()));

        return savedStaff;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Staff deleteStaff(@PathVariable String id, Principal principal, HttpServletRequest request) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.DELETE_STAFF.name()))) {
            throw new InsufficientPermissionException("Not allowed to delete staff accounts.");
        }

        UUID uuid = UUID.fromString(id);
        Staff staff = staffRepository
                .findById(uuid)
                .orElseThrow(() -> new NullPointerException("No such staff account found."));

        staffRepository.delete(staff);
        staffLoader.evaluateSecurity();

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Deleted staff member: %s (%s)",
                        staff.getUsername(),
                        staff.getId()));

        return staff;
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
}
