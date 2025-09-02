package com.williammedina.forohub.domain.response.service;

import com.williammedina.forohub.domain.response.dto.CreateResponseDTO;
import com.williammedina.forohub.domain.response.dto.ResponseDTO;
import com.williammedina.forohub.domain.response.dto.UpdateResponseDTO;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ResponseService {

    ResponseDTO createResponse(@Valid CreateResponseDTO data) throws MessagingException;
    Page<ResponseDTO> getAllResponsesByUser(Pageable pageable);
    ResponseDTO updateResponse(@Valid UpdateResponseDTO data, Long responseId) throws MessagingException;
    void deleteResponse(Long responseId) throws MessagingException;
    ResponseDTO getResponseById(Long responseId);
    ResponseDTO setCorrectResponse(Long responseId) throws MessagingException;

}
