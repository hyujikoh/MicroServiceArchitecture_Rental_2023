package com.my.rental.web.rest.mapper;


import com.my.rental.domain.*;
import com.my.rental.web.rest.dto.RentalDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Rental} and its DTO {@link RentalDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RentalMapper extends EntityMapper<RentalDTO, Rental> {



    default Rental fromId(Long id) {
        if (id == null) {
            return null;
        }
        Rental rental = new Rental();
        rental.setId(id);
        return rental;
    }
}
