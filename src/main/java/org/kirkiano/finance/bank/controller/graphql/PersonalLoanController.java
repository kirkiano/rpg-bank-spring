package org.kirkiano.finance.bank.controller.graphql;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import org.kirkiano.finance.bank.dto.PersonalLoanDTO;
import org.kirkiano.finance.bank.dto.PersonalLoanMapper;
import org.kirkiano.finance.bank.service.PersonalLoanService;


@Controller
public class PersonalLoanController {

    @Autowired
    PersonalLoanController(PersonalLoanService loanService,
                           PersonalLoanMapper mapper)
    {
        this.loanService = loanService;
        this.mapper = mapper;
    }

    @QueryMapping
    public List<PersonalLoanDTO> personalLoans() {
        return this.loanService
            .getAllPersonalLoans()
            .stream()
            .map(this.mapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
      * Personal loan service
      */
    protected final PersonalLoanService loanService;

    /**
      * Object for mapping between models and DTOs
      */
    protected final PersonalLoanMapper mapper;

}