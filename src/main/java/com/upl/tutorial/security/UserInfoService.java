package com.upl.tutorial.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.upl.tutorial.model.Users;
import com.upl.tutorial.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
//@RequiredArgsConstructor
public class UserInfoService implements UserDetailsService {
    
    private final UserRepository userRepository;    

    public UserInfoService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        log.info("********UserDetailsService *****************");

        return new UserInfoDetails(user);
    }
    
}
