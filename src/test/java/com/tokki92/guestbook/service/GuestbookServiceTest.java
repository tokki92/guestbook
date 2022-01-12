package com.tokki92.guestbook.service;

import com.tokki92.guestbook.dto.GuestbookDTO;
import com.tokki92.guestbook.dto.PageRequestDTO;
import com.tokki92.guestbook.dto.PageResultDTO;
import com.tokki92.guestbook.entity.Guestbook;
import com.tokki92.guestbook.repository.GuestbookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class GuestbookServiceTest {

    @InjectMocks
    private GuestbookServiceImpl service;

    @Mock
    private GuestbookRepository repository;

    @Test
    @DisplayName("엔티티 객체를 DTO 객체로 변환해 반환한다.")
    public void testList() {
        // given
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();

        // mocking
        List<Guestbook> guestbooks = IntStream.rangeClosed(1, 10).asLongStream().mapToObj(i ->
                Guestbook.builder().gno(i).title("Title.." + i).content("Content.." + i).writer("Writer.." + i).build()
        ).collect(Collectors.toList());
        Page<Guestbook> page = new PageImpl(guestbooks, pageRequestDTO.getPageable(Sort.by("gno")), 300);

        given(repository.findAll(pageRequestDTO.getPageable(Sort.by("gno"))))
                .willReturn(page);

        // when
        PageResultDTO<GuestbookDTO, Guestbook> list = service.getList(pageRequestDTO);

        // then
        assertIterableEquals(new PageResultDTO<>(page, service::entityToDto).getDtoList(), list.getDtoList());
    }

}