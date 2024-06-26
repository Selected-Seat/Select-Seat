package io.nbc.selectedseat.domain.concert.service.query;

import io.nbc.selectedseat.common.WebPage;
import io.nbc.selectedseat.domain.concert.dto.ConcertDateResponseDTO;
import io.nbc.selectedseat.domain.concert.dto.ConcertInfo;
import io.nbc.selectedseat.domain.concert.dto.ConcertSearchMapper;
import io.nbc.selectedseat.domain.concert.dto.GetConcertRatingResponseDTO;
import io.nbc.selectedseat.domain.concert.dto.GetConcertResponseDTO;
import io.nbc.selectedseat.domain.concert.exception.ConcertDateExistException;
import io.nbc.selectedseat.domain.concert.exception.ConcertExistException;
import io.nbc.selectedseat.domain.concert.exception.ConcertRatingExistException;
import io.nbc.selectedseat.domain.concert.model.Concert;
import io.nbc.selectedseat.domain.concert.model.ConcertDate;
import io.nbc.selectedseat.domain.concert.model.ConcertRating;
import io.nbc.selectedseat.domain.concert.repository.ConcertRepository;
import io.nbc.selectedseat.domain.concert.service.dto.ConcertDetailInfo;
import io.nbc.selectedseat.domain.concert.service.dto.ConcertSearchRequestDTO;
import io.nbc.selectedseat.domain.concert.service.dto.SearchSuggestionResponseDTO;
import io.nbc.selectedseat.elasticsearch.domain.concert.document.ConcertDocument;
import io.nbc.selectedseat.elasticsearch.domain.concert.dto.ConcertSearchMapperDTO;
import io.nbc.selectedseat.elasticsearch.domain.concert.mapper.ConcertSearchQueryMapper;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConcertReader {

    private final ConcertRepository concertRepository;
    private final ConcertSearchQueryMapper concertSearchQueryMapper;

    public GetConcertResponseDTO getConcert(final Long concertId) {
        Concert concert = concertRepository.findById(concertId)
            .orElseThrow(() -> new ConcertExistException("해당 콘서트가 존재하지 않습니다"));
        return GetConcertResponseDTO.from(concert);
    }

    public List<GetConcertResponseDTO> getConcerts() {
        return concertRepository.getConcerts().stream()
            .map(GetConcertResponseDTO::from)
            .toList();
    }

    public List<Concert> getConcertsByConcertIds(final List<Long> concertIds) {
        return concertRepository.getConcertsByConcertIds(concertIds);
    }

    public WebPage<List<ConcertDetailInfo>> searchConcertByTextAndFilter(
        final ConcertSearchRequestDTO requestDTO,
        final int page,
        final int size
    ) throws IOException {
        ConcertSearchMapperDTO concertSearchMapperDTO = new ConcertSearchMapperDTO(
            requestDTO.text(),
            requestDTO.regions(),
            requestDTO.categories(),
            requestDTO.states(),
            requestDTO.concertRatings());

        Page<ConcertDocument> concertDocumentPage = concertSearchQueryMapper.searchConcertByTextAndFilter(
            concertSearchMapperDTO, page, size);

        List<ConcertDetailInfo> results = concertDocumentPage.getContent().stream()
            .map(ConcertDetailInfo::from)
            .toList();

        return new WebPage<>(
            results.size(),
            concertDocumentPage.getTotalElements(),
            concertDocumentPage.getNumber(),
            concertDocumentPage.getTotalPages(),
            results);
    }

    //todo : Temporary settings for performance measurement
    public List<ConcertInfo> searchConcertByTextAndFilterInDB(
        final ConcertSearchRequestDTO requestDTO,
        final int page,
        final int size
    ) throws IOException {

        String region = requestDTO.regions() == null ? null : requestDTO.regions().get(0);
        String category = requestDTO.categories() == null ? null : requestDTO.categories().get(0);
        String state = requestDTO.states() == null ? null : requestDTO.states().get(0);
        String concertRating =
            requestDTO.concertRatings() == null ? null : requestDTO.concertRatings().get(0);

        ConcertSearchMapper concertSearchMapper = new ConcertSearchMapper(
            requestDTO.text(),
            region,
            category,
            state,
            concertRating
        );

        return concertRepository.searchConcertByTextAndFilter(concertSearchMapper, page, size)
            .stream()
            .map(ConcertInfo::from)
            .toList();
    }

    public List<SearchSuggestionResponseDTO> searchSuggestions(final String text)
        throws IOException {
        return concertSearchQueryMapper.searchSuggestions(text).stream()
            .map(SearchSuggestionResponseDTO::from)
            .toList();
    }

    public GetConcertRatingResponseDTO getConcertRating(
        final Long concertRatingId
    ) {
        ConcertRating concertRating = concertRepository.getConcertRating(
                concertRatingId)
            .orElseThrow(ConcertRatingExistException::new);

        return GetConcertRatingResponseDTO.from(concertRating);
    }

    public List<GetConcertResponseDTO> getConcertsByCategory(
        final Long categoryId
    ) {
        return concertRepository.getConcertsByCategory(categoryId).stream()
            .map(GetConcertResponseDTO::from)
            .toList();
    }

    public List<ConcertDateResponseDTO> getConcertDates(final Long concertId) {
        return concertRepository.getConcertDates(concertId).stream()
            .map(ConcertDateResponseDTO::from)
            .toList();
    }

    public ConcertDateResponseDTO getConcertDate(final Long concertId) {
        ConcertDate concertDate = concertRepository.getConcertDate(concertId)
            .orElseThrow(ConcertDateExistException::new);
        return ConcertDateResponseDTO.from(concertDate);
    }

    public void getConcertPerformers(final Long concertId) {
        concertRepository.findById(concertId)
            .orElseThrow(() -> new ConcertExistException("해당 콘서트가 존재하지 않습니다"));
    }
}
