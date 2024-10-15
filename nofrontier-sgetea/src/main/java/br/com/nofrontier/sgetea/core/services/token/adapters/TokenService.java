package br.com.nofrontier.sgetea.core.services.token.adapters;

public interface TokenService {

    String generateAccessToken(String subject);

    String getSubjetDoAccessToken(String accessToken);

    String generateRefreshToken(String subject);

    String getSubjectDoRefreshToken(String refreshToken);

}
