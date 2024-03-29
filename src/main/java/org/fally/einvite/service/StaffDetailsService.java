package org.fally.einvite.service;

import org.fally.einvite.model.Permission;
import org.fally.einvite.model.Staff;
import org.fally.einvite.repository.StaffRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StaffDetailsService implements UserDetailsService {
    private StaffRepository staffRepository;

    @Inject
    public StaffDetailsService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Staff staff = staffRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found."));

        if (!staff.hasPermission(Permission.LOGIN)) {
            throw new UsernameNotFoundException("User is not allowed to log in.");
        }

        List<GrantedAuthority> roles = staff.getPermissions().stream().map(p -> new SimpleGrantedAuthority(p.name())).collect(Collectors.toList());

        return new User(username, staff.getPassword(), roles);
    }
}
