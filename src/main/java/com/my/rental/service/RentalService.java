package com.my.rental.service;

import com.my.rental.domain.Rental;
import com.my.rental.web.rest.dto.RentalDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.my.rental.domain.Rental}.
 */
public interface RentalService {

    /**
     * Save a rental.
     *
     * @param rental the entity to save.
     * @return the persisted entity.
     */
    Rental save(Rental rental);

    /**
     * Get all the rentals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RentalDTO> findAll(Pageable pageable);


    /**
     * Get the "id" rental.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RentalDTO> findOne(Long id);

    /**
     * Delete the "id" rental.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * 도서 대출
     * @param userId
     * @param bookId
     * @param bookTitle
     * @return
     */
    Rental rentBook(Long userId, Long bookId, String bookTitle) throws Exception;

    Rental returnBooks(Long userId, Long bookId) throws Exception;

    Long beOverdueBook(Long rentalId, Long bookId);

    Rental returnOverdueBook(Long userId, Long bookId) throws Exception;

    Rental releaseOverdue(Long userId);
}
