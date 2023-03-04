package com.my.rental.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.my.rental.adaptor.BookClient;
import com.my.rental.domain.Rental;
import com.my.rental.service.RentalService;
import com.my.rental.web.rest.dto.BookInfoDTO;
import com.my.rental.web.rest.errors.BadRequestAlertException;
import com.my.rental.web.rest.dto.RentalDTO;

import com.my.rental.web.rest.mapper.RentalMapper;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.my.rental.domain.Rental}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RentalResource {

    private final Logger log = LoggerFactory.getLogger(RentalResource.class);

    private static final String ENTITY_NAME = "rentalRental";

    private final BookClient bookClient;
    private final UserClient userClient;
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RentalService rentalService;
    private final RentalMapper rentalMapper;
    public RentalResource(RentalService rentalService,RentalMapper rentalMapper, BookClient bookClient, UserClient userClient) {
        this.rentalMapper = rentalMapper;
        this.rentalService = rentalService;
        this.bookClient = bookClient;
        this.userClient = userClient;
    }

    /**
     * {@code POST  /rentals} : Create a new rental.
     *
     * @param rentalDTO the rentalDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rentalDTO, or with status {@code 400 (Bad Request)} if the rental has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rentals")
    public ResponseEntity<RentalDTO> createRental(@RequestBody RentalDTO rentalDTO) throws URISyntaxException {
        log.debug("REST request to save Rental : {}", rentalDTO);
        if (rentalDTO.getId() != null) {
            throw new BadRequestAlertException("A new rental cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RentalDTO result = rentalMapper.toDto(rentalService.save(rentalMapper.toEntity(rentalDTO)));
        return ResponseEntity.created(new URI("/api/rentals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /rentals} : Updates an existing rental.
     *
     * @param rentalDTO the rentalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rentalDTO,
     * or with status {@code 400 (Bad Request)} if the rentalDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rentalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rentals")
    public ResponseEntity<RentalDTO> updateRental(@RequestBody RentalDTO rentalDTO) throws URISyntaxException {
        log.debug("REST request to update Rental : {}", rentalDTO);
        if (rentalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RentalDTO result = rentalMapper.toDto(rentalService.save(rentalMapper.toEntity(rentalDTO)));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rentalDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /rentals} : get all the rentals.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rentals in body.
     */
    @GetMapping("/rentals")
    public ResponseEntity<List<RentalDTO>> getAllRentals(Pageable pageable) {
        log.debug("REST request to get a page of Rentals");
        Page<RentalDTO> page = rentalService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rentals/:id} : get the "id" rental.
     *
     * @param id the id of the rentalDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rentalDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rentals/{id}")
    public ResponseEntity<RentalDTO> getRental(@PathVariable Long id) {
        log.debug("REST request to get Rental : {}", id);
        Optional<RentalDTO> rentalDTO = rentalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rentalDTO);
    }

    /**
     * {@code DELETE  /rentals/:id} : delete the "id" rental.
     *
     * @param id the id of the rentalDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rentals/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        log.debug("REST request to delete Rental : {}", id);
        rentalService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * 도서 대출 하기
     * @param userId
     * @param bookId
     * @return
     * @throws InterruptedException

     * @throws JsonProcessingException
     */
    @PostMapping("/rentals/{userId}/RentedItem/{book}")
    public ResponseEntity<RentalDTO> rentBooks(@PathVariable("userId") Long userId,
                                               @PathVariable("book") Long bookId) throws Exception {
        log.debug("rent book request");
        // 도서 서비스 호출해 도서 정보 가져오기(동기 호출 방식인 페인을 사용)
        ResponseEntity<BookInfoDTO> bookInfoResult = bookClient.findBookInfo(bookId); //feign - 책 정보 가져오기
        BookInfoDTO bookInfoDTO = bookInfoResult.getBody();
        log.debug("book info list", bookInfoDTO.toString());

        Rental rental= rentalService.rentBook(userId, bookInfoDTO.getId(), bookInfoDTO.getTitle());
        RentalDTO rentalDTO = rentalMapper.toDto(rental);
        return ResponseEntity.ok().body(rentalDTO);
    }

    /**
     * 도서 반납 하기
     *
     * @param userId
     * @param book
     * @return
     */
    @DeleteMapping("/rentals/{userId}/RentedItem/{book}")
    public ResponseEntity returnBooks(@PathVariable("userId") Long userId, @PathVariable("book") Long book) throws  Exception {
        Rental rental = rentalService.returnBooks(userId, book); // 도서반납 서비스 로직 실행
        log.debug("returned books");
        log.debug("SEND BOOKIDS for Book: {}", book);

        RentalDTO result = rentalMapper.toDto(rental);
        return ResponseEntity.ok().body(result);
    }
}
