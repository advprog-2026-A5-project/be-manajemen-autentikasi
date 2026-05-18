package id.ac.ui.cs.advprog.bemanagemenautentikasi.service;

public interface GoogleAuthService {
    String verifyAndAuthenticateGoogleToken(String idToken, String role);
}
