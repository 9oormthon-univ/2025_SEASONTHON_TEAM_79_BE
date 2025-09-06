package com.seasontone.service;

import com.seasontone.Entity.User;
import com.seasontone.dto.user.EmailCodeRequest;
import com.seasontone.dto.user.EmailCodeResponse;
import com.seasontone.dto.user.EmailCodeVerifyRequest;
import com.seasontone.dto.user.EmailCodeVerifyResponse;
import com.seasontone.dto.user.LoginRequest;
import com.seasontone.dto.user.LoginResponse;
import com.seasontone.dto.user.LoginResult;
import com.seasontone.dto.user.ProfilesResponse;
import com.seasontone.dto.user.RegisterRequest;
import com.seasontone.dto.user.RegisterResponse;
import com.seasontone.dto.user.UpdateProfilesRequest;
import com.seasontone.dto.user.UpdateProfilesResponse;
import com.seasontone.jwt.JwtUtil;
import com.seasontone.repository.user.UserRepository;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/*
@Service
@RequiredArgsConstructor
public class UserService {
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	@Value("${jwt.refresh-token.expiration-time}")
	private Long refreshTokenExpirationTime;
	private final EmailCodeRepository emailCodeRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JavaMailSender mailSender;
	@Value("${spring.mail.username}")
	private String fromEmail;

	@Transactional
	public RegisterResponse register(RegisterRequest request) {

		// 이메일 중복 체크
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
		}

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		// 사용자 생성
		User user = User.builder()
				.email(request.getEmail())
				.password(encodedPassword)
				.username(request.getName())
				.emailVerified(false)
				.region(request.getRegion())
				.build();
		// 저장
		userRepository.save(user);

		return RegisterResponse.builder()
				.isEmailVerified(user.getEmailVerified())
				.userId(user.getId())
				.build();
	}

	public void checkEmail(String email) {
		boolean isEmailDuplicate = userRepository.existsByEmail(email);

		if (isEmailDuplicate) {
			throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
		}
	}

	public EmailCodeResponse requestEmailCode(EmailCodeRequest request) {
		String code = String.format("%06d", new SecureRandom().nextInt(1000000));
		EmailCode emailCode = new EmailCode(request.getEmail(), code, 300L);
		emailCodeRepository.save(emailCode);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(request.getEmail());
		message.setFrom(fromEmail);
		message.setSubject("PointCareer Email Verification");
		message.setText("Your verification code is " + code);
		mailSender.send(message);

		return new EmailCodeResponse("Verification code sent");
	}

	public EmailCodeVerifyResponse verifyEmailCode(EmailCodeVerifyRequest request) {
		EmailCode savedCode = emailCodeRepository.findById(request.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("인증 코드가 올바르지 않습니다."));

		if (!savedCode.getCode().equals(request.getCode())) {
			throw new IllegalArgumentException("인증 코드가 올바르지 않습니다.");
		}

		emailCodeRepository.deleteById(request.getEmail());

		EmailCodeVerifyResponse response = new EmailCodeVerifyResponse();
		response.setIsEmailVerified(true);
		return response;
	}

	@Transactional
	public LoginResult login(LoginRequest request) {
		// 1. 사용자 조회
		User findUser = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new NullPointerException("존재하지 않는 회원입니다."));

		// 2. 비밀번호 확인
		if (!passwordEncoder.matches(request.getPassword(), findUser.getPassword())) {
			throw new IllegalArgumentException("유효하지 않은 비밀번호입니다.");
		}

		String accessToken = jwtUtil.generateAccessToken(findUser.getId(), findUser.getUsername());
		String refreshToken = jwtUtil.generateRefreshToken(findUser.getId(), findUser.getUsername());

		RefreshToken addRefreshToken = new RefreshToken(findUser.getId(), refreshToken, refreshTokenExpirationTime);
		refreshTokenRepository.save(addRefreshToken);

		LoginResponse loginResponse = LoginResponse.builder()
				.userId(findUser.getId())
				.build();

		return LoginResult.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.loginResponse(loginResponse)
				.build();
	}

	public ProfilesResponse getProfiles(User user) {
		User findUser = userRepository.findById(user.getId()).orElseThrow(()->new NullPointerException("존재하지 않는 회원입니다."));

		return ProfilesResponse.builder()
				.userId(findUser.getId())
				.name(findUser.getUsername())
				.region(findUser.getRegion())
				.build();
	}

	@Transactional
	public UpdateProfilesResponse updateProfiles(UpdateProfilesRequest request, User user) {
		User findUser = userRepository.findById(user.getId()).orElseThrow(()->new NullPointerException("존재하지 않는 회원입니다."));

		findUser.updateRegion(request.getRegion());

		return UpdateProfilesResponse.builder()
				.userId(findUser.getId())
				.build();
	}
}

 */
