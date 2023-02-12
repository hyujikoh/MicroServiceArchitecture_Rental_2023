package com.my.rental.service.impl;

import com.my.rental.service.RentedItemService;
import com.my.rental.domain.RentedItem;
import com.my.rental.repository.RentedItemRepository;
import com.my.rental.web.rest.dto.RentedItemDTO;
import com.my.rental.web.rest.mapper.RentedItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link RentedItem}.
 */
@Service
@Transactional
public class RentedItemServiceImpl implements RentedItemService {

    private final Logger log = LoggerFactory.getLogger(RentedItemServiceImpl.class);

    private final RentedItemRepository rentedItemRepository;

    private final RentedItemMapper rentedItemMapper;

    public RentedItemServiceImpl(RentedItemRepository rentedItemRepository, RentedItemMapper rentedItemMapper) {
        this.rentedItemRepository = rentedItemRepository;
        this.rentedItemMapper = rentedItemMapper;
    }

    /**
     * Save a rentedItem.
     *
     * @param rentedItem the entity to save.
     * @return the persisted entity.
     */
    @Override
    public RentedItem save(RentedItem rentedItem) {
        log.debug("Request to save RentedItem : {}", rentedItem);
        return rentedItemRepository.save(rentedItem);
    }

    /**
     * Get all the rentedItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RentedItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RentedItems");
        return rentedItemRepository.findAll(pageable)
            .map(rentedItemMapper::toDto);
    }


    /**
     * Get one rentedItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RentedItemDTO> findOne(Long id) {
        log.debug("Request to get RentedItem : {}", id);
        return rentedItemRepository.findById(id)
            .map(rentedItemMapper::toDto);
    }

    /**
     * Delete the rentedItem by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete RentedItem : {}", id);
        rentedItemRepository.deleteById(id);
    }
}
