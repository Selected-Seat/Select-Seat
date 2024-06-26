package io.nbc.selectedseat.web.domain.ticket.admin;

import io.nbc.selectedseat.domain.ticket.dto.TicketInfo;
import io.nbc.selectedseat.domain.ticket.service.command.TicketWriter;
import io.nbc.selectedseat.domain.ticket.service.query.TicketReader;
import io.nbc.selectedseat.web.common.dto.ResponseDTO;
import io.nbc.selectedseat.web.domain.ticket.dto.request.TicketCreateRequestDTO;
import io.nbc.selectedseat.web.domain.ticket.dto.response.NumberOfTicketResponseDTO;
import io.nbc.selectedseat.web.domain.ticket.dto.response.TicketInfoResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketAdminController {

    private final TicketWriter ticketWriter;
    private final TicketReader ticketReader;

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping
    public ResponseEntity<ResponseDTO<List<TicketInfoResponseDTO>>> getTickets() {
        List<TicketInfoResponseDTO> tickets = ticketReader.getTickets().stream()
            .map(TicketInfoResponseDTO::from)
            .toList();

        return ResponseEntity.ok(ResponseDTO.<List<TicketInfoResponseDTO>>builder()
            .statusCode(HttpStatus.OK.value())
            .message("전체 티켓이 조회되었습니다")
            .data(tickets)
            .build()
        );
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{ticketId}")
    public ResponseEntity<ResponseDTO<TicketInfoResponseDTO>> getTicket(
        @PathVariable("ticketId") Long ticketId
    ) {
        TicketInfo ticket = ticketReader.getTicket(ticketId);

        return ResponseEntity.ok(ResponseDTO.<TicketInfoResponseDTO>builder()
            .statusCode(HttpStatus.OK.value())
            .message("티켓이 조회되었습니다")
            .data(TicketInfoResponseDTO.from(ticket))
            .build()
        );
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<ResponseDTO<NumberOfTicketResponseDTO>> createTicket(
        @Validated @RequestBody TicketCreateRequestDTO requestDTO
    ) {
        NumberOfTicketResponseDTO responseDTO = NumberOfTicketResponseDTO.from(
            ticketWriter.createTicket(requestDTO.concertId())
        );

        return ResponseEntity.ok(ResponseDTO.<NumberOfTicketResponseDTO>builder()
            .statusCode(HttpStatus.OK.value())
            .message("티켓이 등록되었습니다")
            .data(responseDTO)
            .build()
        );
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<ResponseDTO<Void>> deleteTicket(
        @PathVariable("ticketId") Long ticketId
    ) {
        ticketWriter.deleteTicket(ticketId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
