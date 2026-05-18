package iuh.fit.userservice.service;



import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import iuh.fit.userservice.dto.request.AuthenticationRequest;
import iuh.fit.userservice.dto.request.RegisterDTO;
import iuh.fit.userservice.dto.response.AuthenticationResponse;
import iuh.fit.userservice.dto.response.UserDTO;
import iuh.fit.userservice.exception.AppException;
import iuh.fit.userservice.exception.ErrorCode;
import iuh.fit.userservice.client.NotificationClient;
import iuh.fit.userservice.dto.request.SendEmailRequest;
import iuh.fit.userservice.mapper.UserMapper;
import iuh.fit.userservice.model.*;
import iuh.fit.userservice.repository.AdminRepository;
import iuh.fit.userservice.repository.CandidateRepository;
import iuh.fit.userservice.repository.EmployerRepository;
import iuh.fit.userservice.repository.InvalidatedTokenRepository;
import iuh.fit.userservice.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;


import org.springframework.beans.factory.annotation.Autowired;


import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AdminRepository adminRepository;
    private final CandidateRepository candidateRepository;
    private final EmployerRepository employerRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final NotificationClient notificationClient;




    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        String email = request.getUsername();
        String password = request.getPassword();

        switch (request.getType()) {

            case "ADMIN":
                Admin admin =  adminRepository.findByEmail(email)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                if (!passwordEncoder.matches(password, admin.getPassword())) {
                    throw new AppException(ErrorCode.UNAUTHENTICATED);
                }
                System.out.println(admin.getPassword());
                return new AuthenticationResponse(
                        admin.getAdminId(),
                        generateToken(admin.getAdminId(), admin.getFullName(), "ADMIN"),
                        true
                );

            case "CANDIDATE":
                Candidate c = candidateRepository.findByEmail(email)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                if (!passwordEncoder.matches(password, c.getPassword())) {
                    throw new AppException(ErrorCode.UNAUTHENTICATED);
                }

                return new AuthenticationResponse(
                        c.getCandidateId(),
                        generateToken(c.getCandidateId(), c.getFullName(), "CANDIDATE"),
                        true
                );

            case "EMPLOYER":
                Employer e = employerRepository.findByEmail(email)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                if (!passwordEncoder.matches(password, e.getPassword())) {
                    throw new AppException(ErrorCode.UNAUTHENTICATED);
                }

                return new AuthenticationResponse(
                        e.getEmployerId(),
                        generateToken(e.getEmployerId(), e.getFullName(), "EMPLOYER"),
                        true
                );

            default:
                throw new AppException(ErrorCode.INVALID_REQUEST);
        }
    }
    public Object getCurrentUser(String token) throws JOSEException, ParseException, java.text.ParseException {
        var claims = verify(token);
        String userId = claims.getStringClaim("userId");
        String role = claims.getStringClaim("scope");


        switch (role) {
            case "ADMIN":
               Admin admin= adminRepository.findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            return UserMapper.fromAdmin(admin);
               case "CANDIDATE":
               Candidate candidate= candidateRepository.findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            return UserMapper.fromCandidate(candidate);
               case "EMPLOYER":
                Employer employer= employerRepository.findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                return UserMapper.fromEmployer(employer);
                default:
                throw new AppException(ErrorCode.INVALID_REQUEST);
        }
    }

    public void logout(String token) {
        try {
            var claims = verify(token);
            String jti = claims.getJWTID();
            Date expiryTime = claims.getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jti)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (Exception e) {
        }
    }



    public JWTClaimsSet verify(String token) throws JOSEException, ParseException, java.text.ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (exp.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        if (jti != null && invalidatedTokenRepository.existsById(jti)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT.getJWTClaimsSet();
    }



        public String generateToken(String UserId, String FullName,String role) {
            JWSHeader header=new JWSHeader(JWSAlgorithm.HS512);
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(UserId)
                    .issuer("carrreconnect")
                    .issueTime(new Date())
                    .expirationTime(new Date(
                            Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                    ))
                    .jwtID(java.util.UUID.randomUUID().toString())
                    .claim("userId", UserId)
                    .claim("fullname", FullName)
                    .claim("scope", role)
                    .build();
            Payload payload = new Payload(jwtClaimsSet.toJSONObject());

            JWSObject jwsObject = new JWSObject(header, payload);

            try {
                jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
                return jwsObject.serialize();
            } catch (JOSEException e) {

                throw new RuntimeException(e);
            }
        }
        @Transactional
        public UserDTO register(RegisterDTO registerDTO) {
            // 1. Xác thực OTP trước khi làm bất cứ việc gì
            if (!otpService.consumeVerifiedRegistration(registerDTO.getEmail())) {
                throw new AppException(ErrorCode.OTP_NOT_VERIFIED);
            }

            String password = passwordEncoder.encode(registerDTO.getPassword());

            switch (registerDTO.getType().toUpperCase()) {
                case "CANDIDATE": {
                    if (candidateRepository.existsByEmail(registerDTO.getEmail())) {
                        throw new AppException(ErrorCode.USEREMAIL_EXISTED);
                    }
                    Candidate candidate = new Candidate();
                    candidate.setCandidateId(IdGenerator.generatorIdCandidate());
                    candidate.setEmail(registerDTO.getEmail());
                    candidate.setFullName(registerDTO.getFullName());
                    candidate.setPhone(registerDTO.getPhone());
                    candidate.setPassword(password);
                    candidate.setCreatedAt(LocalDate.now());
                    candidate.setUpdatedAt(LocalDate.now());
                    candidate.setStatus(Status.ACTIVE);
                    return UserMapper.fromCandidate(candidateRepository.save(candidate));
                }

                case "EMPLOYER": {
                    if (employerRepository.existsByEmail(registerDTO.getEmail())) {
                        throw new AppException(ErrorCode.USEREMAIL_EXISTED);
                    }
                    Employer employer = new Employer();
                    employer.setEmployerId(IdGenerator.generatorIdEmployer());
                    employer.setEmail(registerDTO.getEmail());
                    employer.setFullName(registerDTO.getFullName());
                    employer.setPhone(registerDTO.getPhone());
                    employer.setPassword(password);
                    employer.setCreatedAt(LocalDate.now());
                    employer.setUpdatedAt(LocalDate.now());
                    employer.setStatus(Status.ACTIVE);
                    return UserMapper.fromEmployer(employerRepository.save(employer));
                }

                default:
                    throw new AppException(ErrorCode.INVALID_REQUEST);
            }
        }

    public void sendOtp(String email, String type) {
        switch (type.toUpperCase()) {
            case "CANDIDATE" -> {
                if (candidateRepository.existsByEmail(email)) {
                    throw new AppException(ErrorCode.USEREMAIL_EXISTED);
                }
            }
            case "EMPLOYER" -> {
                if (employerRepository.existsByEmail(email)) {
                    throw new AppException(ErrorCode.USEREMAIL_EXISTED);
                }
            }
            default -> throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        String otp = otpService.generateOtp(email);
        
        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .to(email)
                .type("OTP")
                .otp(otp)
                .build();
                
        notificationClient.sendEmail(emailRequest);
    }

    public void verifyOtp(String email, String otp) {
        if (!otpService.verifyOtp(email, otp)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
    }
}
