package jungle.ovengers.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuditorHolder {

    public Long get() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal()
                                 .equals("anonymousUser")) {
            return 0L;
        }

        return Long.valueOf(String.valueOf(authentication.getPrincipal()));
    }
}
