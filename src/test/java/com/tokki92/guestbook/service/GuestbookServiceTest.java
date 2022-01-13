package com.tokki92.guestbook.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.tokki92.guestbook.dto.GuestbookDTO;
import com.tokki92.guestbook.dto.PageRequestDTO;
import com.tokki92.guestbook.dto.PageResultDTO;
import com.tokki92.guestbook.entity.Guestbook;
import com.tokki92.guestbook.entity.QGuestbook;
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
import java.util.Optional;
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
        PageRequestDTO requestDTO = PageRequestDTO.builder().page(1).size(10).build();

        // mocking
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QGuestbook qGuestbook = QGuestbook.guestbook;
        BooleanExpression expression = qGuestbook.gno.gt(0L);
        booleanBuilder.and(expression);

        List<Guestbook> guestbooks = IntStream.rangeClosed(1, 10).asLongStream().mapToObj(i ->
                Guestbook.builder().gno(i).title("Title.." + i).content("Content.." + i).writer("Writer.." + i).build()
        ).collect(Collectors.toList());
        Page<Guestbook> page = new PageImpl(guestbooks, requestDTO.getPageable(Sort.by("gno").descending()), 300);
        given(repository.findAll(booleanBuilder, requestDTO.getPageable(Sort.by("gno").descending())))
                .willReturn(page);

        // when
        PageResultDTO<GuestbookDTO, Guestbook> list = service.getList(requestDTO);

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

    @Test
    @DisplayName("조회 서비스 테스트")
    void testRead() {
        // given
        Long gno = 1L;

        // mocking
        Guestbook entity = Guestbook.builder().gno(gno).title("title").content("content").writer("writer").build();
        GuestbookDTO expectedDto = service.entityToDto(entity);
        given(repository.findById(gno)).willReturn(Optional.of(entity));

        // when
        GuestbookDTO result = service.read(gno);

        // then
        assertEquals(expectedDto.getGno(), result.getGno());
        assertEquals(expectedDto.getTitle(), result.getTitle());
        assertEquals(expectedDto.getContent(), result.getContent());
        assertEquals(expectedDto.getWriter(), result.getWriter());

    }

    @Test
    @DisplayName("검색 서비스 테스트")
    void testSearch() {
        // given
        PageRequestDTO requestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .type("tc")
                .keyword("한글")
                .build();

        // mocking
        String keyword = "한글";
        QGuestbook qGuestbook = QGuestbook.guestbook;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanExpression expression = qGuestbook.gno.gt(0L);
        booleanBuilder.and(expression);

        BooleanBuilder conditionBuilder = new BooleanBuilder();
        conditionBuilder.or(qGuestbook.title.contains(keyword));
        conditionBuilder.or(qGuestbook.content.contains(keyword));

        booleanBuilder.and(conditionBuilder);

        List<Guestbook> guestbooks = IntStream.rangeClosed(1, 5).asLongStream().mapToObj(i -> {
            Guestbook.GuestbookBuilder builder = Guestbook.builder().gno(i);
            if (i % 2 == 0) {
                builder.title("Title.." + i);
                builder.content("한글 " + i);
            } else {
                builder.title("한글 " + i);
                builder.content("Content.." + i);
            }
            return builder.build();
        }).collect(Collectors.toList());

        PageImpl<Guestbook> page = new PageImpl<>(guestbooks, requestDTO.getPageable(Sort.by("gno").descending()), 5);

        given(repository.findAll(booleanBuilder, requestDTO.getPageable(Sort.by("gno").descending())))
                .willReturn(page);

        // when
        PageResultDTO<GuestbookDTO, Guestbook> result = service.getList(requestDTO);

        // then
        assertIterableEquals(new PageResultDTO<>(page, service::entityToDto).getDtoList(), result.getDtoList());
        assertFalse(result.isPrev());
        assertFalse(result.isNext());
        assertEquals(1, result.getTotalPage());
        assertIterableEquals(List.of(1), result.getPageList());
    }
}