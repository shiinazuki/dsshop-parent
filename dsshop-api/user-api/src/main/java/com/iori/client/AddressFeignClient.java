package com.iori.client;

import com.iori.dto.AddressDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("user-server")
public interface AddressFeignClient {

    @GetMapping("/address/query")
    List<AddressDTO> query(@RequestParam("username") String username);

}
