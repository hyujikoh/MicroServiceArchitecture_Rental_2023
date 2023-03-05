package com.my.rental.service.impl;

import com.my.rental.adaptor.RentalProducer;
import com.my.rental.service.RentalService;
import com.my.rental.domain.Rental;
import com.my.rental.repository.RentalRepository;
import com.my.rental.web.rest.dto.RentalDTO;
import com.my.rental.web.rest.mapper.RentalMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Rental}.
 */
@Service
@Transactional
public class RentalServiceImpl implements RentalService {

    private final Logger log = LoggerFactory.getLogger(RentalServiceImpl.class);

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final RentalProducer rentalProducer;

    private int pointPerBooks = 30;

    public RentalServiceImpl(RentalRepository rentalRepository, RentalMapper rentalMapper, RentalProducer rentalProducer) {
        this.rentalRepository = rentalRepository;
        this.rentalMapper = rentalMapper;
        this.rentalProducer = rentalProducer;
    }

    /**
     * Save a rental.
     *
     * @param rental the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Rental save(Rental rental) {
        log.debug("Request to save Rental : {}", rental);
        return rentalRepository.save(rental);
    }

    /**
     * Get all the rentals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RentalDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Rentals");
        return rentalRepository.findAll(pageable)
            .map(rentalMapper::toDto);
    }


    /**
     * Get one rental by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RentalDTO> findOne(Long id) {
        log.debug("Request to get Rental : {}", id);
        return rentalRepository.findById(id)
            .map(rentalMapper::toDto);
    }

    /**
     * Delete the rental by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Rental : {}", id);
        rentalRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Rental rentBook(Long userId, Long bookId, String bookTitle) throws Exception{

        Rental rental = rentalRepository.findByUserId(userId).get();// 유저의 Rental 정보 조회
        rental.checkRentalAvailable();// 대출가능 상태 확인
        rental = rental.rentBook(bookId,bookTitle); // rental 도메인에 대출 처리 위임
        rentalRepository.save(rental);// rental 저장

        // 도서 서비스에 도서 재고 감소를 위해 도서 대출 이벤트 발송
        rentalProducer.updateBookStatus(bookId,"UNAVAILABLE");

        // 도서 카탈로그 서비스에 대출된 도서로 상태를 변경하기 위한 이벤트 발송
        rentalProducer.updateBookCatalogStatus(bookId,"RENT_BOOK");

        //대출로 인한 사용자 포인트 적립을 위해 사용자 서비스에 이벤트 발송
        rentalProducer.savePoints(userId,pointPerBooks);

        return rental;
    }

    @Override
    @Transactional
    public Rental returnBooks(Long userId, Long bookId) throws Exception{
        Rental rental = rentalRepository.findByUserId(userId).get();//반납 아이템 검사
        rental = rental.returnBooks(bookId);// rental 도메인에 반납 처리 위임
        rental = rentalRepository.save(rental);

        // 도서 서비스에 도서 재고 증가를 위해 도서 대출 이벤트 발송
        rentalProducer.updateBookStatus(bookId,"AVAILABLE");

        // 도서 카탈로그 서비스에 대출 가능한 도서로 상태를 변경하기 위한 이벤트 발송
        rentalProducer.updateBookCatalogStatus(bookId,"RETURN_BOOk");

        return rental;
    }

    @Override
    public Long beOverdueBook(Long rentalId, Long bookId) {
        Rental rental = rentalRepository.findById(rentalId).get();//(1) 사용자 일련번호에 해당하는 rental 조회
        rental = rental.overdueBook(bookId);//(2) 도서 연체 처리를 rental 객체에 위임해 처리한다
        rental = rental.makeRentUnable();//(3) rental 대출 가능 여부 상태를 대출 불가로 설정
        rentalRepository.save(rental);//(4) 저장
        return bookId;
    }

    @Override
    public Rental returnOverdueBook(Long userId, Long bookId) throws  Exception{
        Rental rental = rentalRepository.findByUserId(userId).get();//(1) 사용자 일련번호에 해당하는 rental 조회
        rental = rental.returnOverdueBook(bookId);//(2) 도서 연체 처리를 rental 객체에 위임해 처리한다
        rentalProducer.updateBookStatus(bookId,"AVAILABLE");
        rentalProducer.updateBookCatalogStatus(bookId, "RETURN_BOOK");//(3) rental 대출 가능 여부 상태를 대출 불가로 설정
        return rentalRepository.save(rental);//(4) 저장
    }


}
