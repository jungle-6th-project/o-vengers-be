package jungle.ovengers.exception;

public class RefreshTokenInvalidException extends RuntimeException {
    private final String token;

    public RefreshTokenInvalidException(String token) {
        this.token = token;
    }

    @Override
    public String getMessage() {
        return "유효하지 않은 refresh token :" + token;
    }
}
