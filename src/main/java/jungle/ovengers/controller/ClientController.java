package jungle.ovengers.controller;

import jungle.ovengers.model.request.ClientAddRequest;
import jungle.ovengers.model.response.ClientAddResponse;
import jungle.ovengers.service.ClientService;
import jungle.ovengers.support.ApiResponse;
import jungle.ovengers.support.ApiResponseGenerator;
import jungle.ovengers.support.MessageCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@RestController
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    public ApiResponse<ApiResponse.SuccessBody<ClientAddResponse>> add(@RequestBody @Valid ClientAddRequest request) {
        return ApiResponseGenerator.success(clientService.saveFcmToken(request), HttpStatus.CREATED, MessageCode.RESOURCE_CREATED);
    }
}
