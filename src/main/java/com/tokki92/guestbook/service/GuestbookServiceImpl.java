package com.tokki92.guestbook.service;

import com.tokki92.guestbook.dto.GuestbookDTO;
import com.tokki92.guestbook.dto.PageRequestDTO;
import com.tokki92.guestbook.dto.PageResultDTO;
import com.tokki92.guestbook.entity.Guestbook;
import com.tokki92.guestbook.repository.GuestbookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class GuestbookServiceImpl implements GuestbookService {

    private final GuestbookRepository repository;

    @Override
    public Long register(GuestbookDTO dto) {
        Guestbook entity = dtoToEntity(dto);

        repository.save(entity);

        return entity.getGno();
    }

    @Override
    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("gno"));

        Page<Guestbook> result = repository.findAll(pageable);

        return new PageResultDTO<>(result, this::entityToDto);
    }

    @Override
    public GuestbookDTO read(Long gno) {
        Optional<Guestbook> result = repository.findById(gno);

        return result.isPresent() ? entityToDto(result.get()) : null;
    }

    @Override
    public void remove(Long gno) {
        repository.deleteById(gno);
    }

    @Override
    public void modify(GuestbookDTO dto) {

        Optional<Guestbook> result = repository.findById(dto.getGno());

        if (result.isPresent()) {
            Guestbook entity = result.get();

            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());

            repository.save(entity);
        }
    }
}
