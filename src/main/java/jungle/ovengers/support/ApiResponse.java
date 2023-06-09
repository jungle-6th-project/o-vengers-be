package jungle.ovengers.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

public class ApiResponse<B> extends ResponseEntity<B> {
    public ApiResponse(HttpStatus status) {
        super(status);
    }

    public ApiResponse(B body, HttpStatus status) {
        super(body, status);
    }

    @Getter
    @AllArgsConstructor
    public static class FailureBody implements Serializable {

        private String code;
        private String message;

        public FailureBody(final String message) {
            this.message = message;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class SuccessBody<D> implements Serializable {
        private D data;
        private String message;
        private String code;
    }
}
