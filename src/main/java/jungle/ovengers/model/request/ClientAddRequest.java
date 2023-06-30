package jungle.ovengers.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientAddRequest {

    @NotBlank
    @Length(max = 1000)
    private String fcmToken;

    @NotBlank
    @Length(max = 1000)
    private String identificationKey;
}
