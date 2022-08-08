package app.utils;

import app.dtos.TokenResponse;
import app.dtos.TokenType;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JWTUtils {

    private static JWTUtils instance;

    private final String secret="s3cr3t";


    private final Long access_token_expiry=10L;


    private final Long refresh_token_expiry=15L;

    private final String claimName="roles";


    private final ZonedDateTime zone = LocalDateTime.now().atZone(ZoneId.systemDefault());


    private final Algorithm algorithm  = Algorithm.HMAC512(secret);

    public static final String HEADER_NAME  = HttpHeaders.AUTHORIZATION;

    public static final String TOKEN_PREFIX = "Bearer ";

    private JWTUtils(){

    }

    public static synchronized JWTUtils getInstance(){
            if (instance == null)
                instance = new JWTUtils();
        return instance;
    }

    public TokenResponse getToken(TokenType tokenType, User user){

        String username = user.getUsername();

        return getToken(tokenType,username,user.getAuthorities().stream().toList());
//
//        Date expiresAt = null;
//        JWTCreator.Builder jwtBuilder = JWT.create().withSubject(username).withIssuedAt(new Date());
//        switch(tokenType){
//            case REFRESH_TOKEN ->{
//                expiresAt = Date.from(zone.plus(Duration.of(refresh_token_expiry, ChronoUnit.DAYS)).toInstant());
//                jwtBuilder.withExpiresAt(expiresAt);
//            }
//            default -> {
//                expiresAt = Date.from(zone.plus(Duration.of(access_token_expiry, ChronoUnit.MINUTES)).toInstant());
//                List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
//                jwtBuilder.withExpiresAt(expiresAt).withClaim(claimName, roles);
//            }
//        }
//
//        String token = jwtBuilder.sign(algorithm);
//
//        return new TokenResponse(type,token,expiresAt,expiresAt.toString());

    }

    public TokenResponse getToken(TokenType tokenType,String username,List<GrantedAuthority> authorities){

        Date expiresAt = null;
        JWTCreator.Builder jwtBuilder = JWT.create().withSubject(username).withIssuedAt(new Date());
        switch(tokenType){
            case REFRESH_TOKEN ->{
                expiresAt = Date.from(zone.plus(Duration.of(refresh_token_expiry, ChronoUnit.DAYS)).toInstant());
                jwtBuilder.withExpiresAt(expiresAt);
            }
            default -> {
                expiresAt = Date.from(zone.plus(Duration.of(access_token_expiry, ChronoUnit.MINUTES)).toInstant());
                List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
                jwtBuilder.withExpiresAt(expiresAt).withClaim(claimName, roles);
            }
        }

        String token = jwtBuilder.sign(algorithm);

        return new TokenResponse(tokenType,token,expiresAt,expiresAt.toString());
    }


    private DecodedJWT decodedJWT(String token){
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public List<SimpleGrantedAuthority> getRolesFromToken(String token){
        return getClaimAsAuthorities(token,claimName);
    }

    public Claim getClaimFromToken(String token, String claimName){
        return decodedJWT(token).getClaim(claimName);
    }

    public List<SimpleGrantedAuthority> getClaimAsAuthorities(String token,String claimName){
        return getClaimFromToken(token,claimName).asList(String.class).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public String getUsernameFromToken(String token){
        return decodedJWT(token).getSubject();
    }

    public Date getExpiryDate(String token){
        return decodedJWT(token).getExpiresAt();
    }

    public boolean isTokenExpired(String token){
        return decodedJWT(token).getExpiresAt().before(new Date());
    }





}
