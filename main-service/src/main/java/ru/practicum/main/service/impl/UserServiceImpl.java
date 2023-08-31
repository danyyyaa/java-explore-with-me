package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.entity.User;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.UserService;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User %s not found", userId)));

        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> getUsersByIds(Set<Long> ids, Pageable page) {
        return ids == null || ids.isEmpty()
                ? userRepository.findAll(page).getContent()
                : userRepository.findAllByIdIn(ids, page);
    }
}