package jungle.ovengers.config;

import jungle.ovengers.config.security.filter.token.TokenResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class TokenAuthenticationInterceptor implements ChannelInterceptor {

    private static final Pattern PATTERN_AUTHORIZATION_HEADER = Pattern.compile("^[Bb]earer (.*)$");
    private static final String ROLE_USER = "ROLE_USER";
    private static final String PREAUTH_TOKEN_CREDENTIAL = "";
    private final TokenResolver tokenResolver;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Long memberId = tokenResolver.resolveToken(resolveAccessToken(accessor))
                                         .get();
            Authentication authentication = new PreAuthenticatedAuthenticationToken(memberId, PREAUTH_TOKEN_CREDENTIAL, Collections.singleton(new SimpleGrantedAuthority(ROLE_USER)));
            accessor.setUser(authentication);
            SecurityContextHolder.getContext()
                                 .setAuthentication(authentication);
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }

    private String resolveAccessToken(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            return null;
        }
        Matcher matcher = PATTERN_AUTHORIZATION_HEADER.matcher(authorization);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }
}
