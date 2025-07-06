package com.properException.handle.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.properException.handle.dto.UserDto;
import com.properException.handle.entity.User;
import com.properException.handle.exceptions.ResourceNotFoundException;
import com.properException.handle.exceptions.ResourceValidationException;
import com.properException.handle.reposetory.UserRepository;
import com.properException.handle.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	@Override
	public UserDto createUser(UserDto userDto) {
		Map<String, String> dupErrors = new HashMap<>();
		if (userRepository.existsByEmail(userDto.getEmail())) {
			dupErrors.put("email", "Email is already in use");
		}
		if (userRepository.existsByContact(userDto.getContact())) {
			dupErrors.put("contact", "Contact number is already in use");
		}
		if (!dupErrors.isEmpty()) {
			throw new ResourceValidationException(dupErrors);
		}

		User user = modelMapper.map(userDto, User.class);
		User saved = userRepository.save(user);
		return modelMapper.map(saved, UserDto.class);
	}

	@Override
	public UserDto getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
		return modelMapper.map(user, UserDto.class);
	}

	@Override
	public List<UserDto> getAllUsers() {
		return userRepository.findAll().stream().map(u -> modelMapper.map(u, UserDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public UserDto updateUser(Long id, UserDto userDto) {
		User existing = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
		// map only the updatable fields
		existing.setName(userDto.getName());
		existing.setEmail(userDto.getEmail());
		existing.setContact(userDto.getContact());
		User updated = userRepository.save(existing);
		return modelMapper.map(updated, UserDto.class);
	}

	@Override
	public void deleteUser(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
		userRepository.delete(user);
	}
}
