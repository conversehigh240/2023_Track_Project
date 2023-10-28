package com.server.track_project.user;

import com.server.track_project.user.Object.userVO;
import com.server.track_project.user.Mapper.userMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@RequiredArgsConstructor
public class userService implements UserDetailsService {

    private final userMapper userMapper;

    @Transactional
    public void signUp(userVO uservo) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        uservo.setPassword(passwordEncoder.encode(uservo.getPassword()));
        uservo.setAuth("ROLE_USER");
        userMapper.saveUser(uservo);
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        userVO user = userMapper.getUserAccount(userId);
        if (user == null)
        {
            throw new UsernameNotFoundException("Unauthorized");
        }
        return user;
    }
}
