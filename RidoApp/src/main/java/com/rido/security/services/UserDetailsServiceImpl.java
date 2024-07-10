package com.rido.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rido.entity.UserIdentity;
import com.rido.repository.UserIdentityRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserIdentityRepository userIdentityRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserIdentity user = userIdentityRepository.findByUsernameOrEmailOrPhoneNo(username, username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username/email/phone number: " + username));

        return UserDetailsImpl.build(user);
    }
}
