package assessment.batch3.authserver.Exception;

public class AuthErrorException extends RuntimeException {
    public AuthErrorException(String msg) {
        super(msg);
    }
}
