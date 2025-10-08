package com.skynetauth.auth_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.skynetauth.auth_service.dto.dto.SoldToDTO;
import com.skynetauth.auth_service.mapper.SoldToMapper;
import com.skynetauth.auth_service.repositories.SoldToRepository;

@Service
public class SoldToService {

    private final SoldToRepository soldToRepository;
    private final SoldToMapper soldToMapper;

    public SoldToService(SoldToRepository soldToRepository, SoldToMapper soldToMapper) {
        this.soldToRepository = soldToRepository;
        this.soldToMapper = soldToMapper;

    }

    public Page<SoldToDTO> getAllSoldTos(Pageable pageable, Long id) {
        return soldToRepository.findAllByDistributionsId(id, pageable).map(soldToMapper::toSoldToDTO);
    }
}
