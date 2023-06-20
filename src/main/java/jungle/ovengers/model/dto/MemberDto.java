package jungle.ovengers.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberDto {
    private String name;
    private String profile;
    private String email;
    private Long targetId;
}
