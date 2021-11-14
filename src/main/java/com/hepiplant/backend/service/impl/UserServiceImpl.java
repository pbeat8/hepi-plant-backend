package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.UserDto;
import com.hepiplant.backend.dto.UserStatisticsDto;
import com.hepiplant.backend.entity.Role;
import com.hepiplant.backend.entity.User;
import com.hepiplant.backend.exception.ImmutableFieldException;
import com.hepiplant.backend.mapper.DtoMapper;
import com.hepiplant.backend.repository.PostCommentRepository;
import com.hepiplant.backend.repository.RoleRepository;
import com.hepiplant.backend.repository.SalesOfferCommentRepository;
import com.hepiplant.backend.repository.UserRepository;
import com.hepiplant.backend.service.UserService;
import com.hepiplant.backend.validator.BeanValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hepiplant.backend.mapper.DtoMapper.mapToDto;

@Service
public class UserServiceImpl implements UserService {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PostCommentRepository postCommentRepository;
    private final SalesOfferCommentRepository salesOfferCommentRepository;
    private final BeanValidator beanValidator;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(final UserRepository userRepository,
                           final RoleRepository roleRepository,
                           final PostCommentRepository postCommentRepository,
                           final SalesOfferCommentRepository salesOfferCommentRepository,
                           final BeanValidator beanValidator,
                           final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.postCommentRepository = postCommentRepository;
        this.salesOfferCommentRepository = salesOfferCommentRepository;
        this.beanValidator = beanValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(DtoMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(final Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found for id "+id));
        return mapToDto(user);
    }

    @Override
    public UserDto add(final UserDto userDto) {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if(optionalUser.isPresent())
            return mapToDto(optionalUser.get());

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setUid(passwordEncoder.encode(userDto.getUid()));
        user.setEmail(userDto.getEmail());
        user.setRoles(Set.of(roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("Role not found for name " + ROLE_USER))));
        user.setNotifications(true);

        beanValidator.validate(user);
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public UserDto update(final Long id, final UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found for id "+id));
        if(userDto.getUsername()!=null)
            user.setUsername(userDto.getUsername());
        if(userDto.getUid()!=null){
            throw new ImmutableFieldException("Field uid in User is immutable!");
        }
        if(userDto.getEmail()!=null) {
            user.setEmail(userDto.getEmail());
        }
        user.setNotifications(userDto.isNotifications());
        if(userDto.getRoles()!=null) {
            throw new ImmutableFieldException("Field roles in User can be altered using grant-role endpoint!");
        }
        beanValidator.validate(user);
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    @Override
    public String delete(final Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            return "No user with id = " + id;
        }
        postCommentRepository.findAll().stream()
                .filter(c -> c.getUser().getId().equals(id))
                .forEach(postCommentRepository::delete);
        salesOfferCommentRepository.findAll().stream()
                .filter(c -> c.getUser().getId().equals(id))
                .forEach(salesOfferCommentRepository::delete);
        userRepository.delete(user.get());
        return "Successfully deleted the user with id = "+ id;
    }

    @Override
    public UserStatisticsDto getUserStatistics(final Long id) {
        final User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found for id "+id));
        final UserStatisticsDto statistics = new UserStatisticsDto();
        statistics.setUser(mapToDto(user));
        statistics.setPlantsAmount(user.getPlantList().size());
        statistics.setPostsAmount(user.getPostList().size());
        statistics.setSalesOffersAmount(user.getSalesOfferList().size());
        statistics.setCommentsAmount(userRepository.getCommentsCountByUserId(user.getId()));
        return statistics;
    }

    @Override
    public String grantRole(final Long id, final String roleName) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found for id " + id));
        Set<Role> roles = user.getRoles();
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new EntityNotFoundException("Role not found for name " + ROLE_USER));
        if(roles.contains(role)){
            return "User with id " + id + " has already been granted role " + roleName;
        }
        roles.add(role);
        user.setRoles(roles);
        beanValidator.validate(user);
        userRepository.save(user);
        return "Successfully granted " + roleName + " to user with id " + id;
    }
}
