package com.skynetauth.auth_service.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skynetauth.auth_service.Enum.CustomHttpStatus;
import com.skynetauth.auth_service.dto.dto.SoldToDTO;
import com.skynetauth.auth_service.dto.response.ApiResponse;
import com.skynetauth.auth_service.service.SoldToService;
import com.skynetauth.auth_service.utils.HashIdUtil;

@RestController
@RequestMapping("/api/soldto")
public class SoldToController extends BaseController {

    private final SoldToService soldToService;
    private final HashIdUtil hashIdUtil;

    public SoldToController(SoldToService soldToService, HashIdUtil hashIdUtil) {
        this.soldToService = soldToService;
        this.hashIdUtil = hashIdUtil;
    }

    @GetMapping("/all-soldtos/{id}")
    public ResponseEntity<ApiResponse<Page<SoldToDTO>>> getAllSoldTosUnderDistribution(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction, @PathVariable String id) {
        Sort sort = direction.equals("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<SoldToDTO> soldToPage = soldToService.getAllSoldTos(pageRequest, hashIdUtil.decodeId(id));

        return this.buildResponse(soldToPage, true, HttpStatus.OK, CustomHttpStatus.S_FETCH_U);
    }

}
