package jungle.ovengers.exception;

public class TokenInvalidException extends RuntimeException{
    private final String accessToken;

    public TokenInvalidException(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getMessage() {
        return "유효하지 않은 토큰 입니다. accessToken: " + accessToken;
    }
}
