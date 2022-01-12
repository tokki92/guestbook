package com.tokki92.guestbook.service;

import com.tokki92.guestbook.dto.GuestbookDTO;
import com.tokki92.guestbook.dto.PageRequestDTO;
import com.tokki92.guestbook.dto.PageResultDTO;
import com.tokki92.guestbook.entity.Guestbook;

public interface GuestbookService {

    PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO);

    default Guestbook dtoToEntity(GuestbookDTO dto) {
        return Guestbook.builder()
                .gno(dto.getGno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();
    }

    default GuestbookDTO entityToDto(Guestbook entity) {
        return GuestbookDTO.builder()
                .gno(entity.getGno())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build();
    }
}
