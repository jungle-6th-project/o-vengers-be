package jungle.ovengers.controller;

import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.enums.MemberStatus;
import jungle.ovengers.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final MemberRepository memberRepository;

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        if (method.getName()
                  .equals("deleteAssociations")) {
            MemberEntity memberEntity = (MemberEntity) params[0];
            memberRepository.save(memberEntity.toBuilder()
                                              .status(MemberStatus.SEPARATE)
                                              .build());
            log.error("member withdraw process exception ocurred ! method = {}, message = {}", method.getName(), ex.getMessage());
            return;
        }
        log.error("unknown async exception occured ! memthod = {}, message = {}", method.getName(), ex.getMessage());
    }
}
