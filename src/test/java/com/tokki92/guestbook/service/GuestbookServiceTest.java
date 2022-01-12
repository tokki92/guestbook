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
    @DisplayName("목록 데이터 페이징 테스트")
    void testList() {
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
        assertFalse(list.isPrev());
        assertTrue(list.isNext());
        assertEquals(30, list.getTotalPage());
        assertIterableEquals(IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList()), list.getPageList());
    }

    @Test
    @DisplayName("등록 서비스 테스트")
    void testRegisterGuestbook() {
        // given
        GuestbookDTO dto = GuestbookDTO.builder().gno(1L).build();

        // mocking
        given(repository.save(any())).willReturn(service.dtoToEntity(dto));

        // when
        Long gno = service.register(dto);

        // then
        assertEquals(1L, gno);
    }
}