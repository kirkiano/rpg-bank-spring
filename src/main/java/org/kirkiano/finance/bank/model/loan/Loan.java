package org.kirkiano.finance.bank.model.loan;

import jakarta.persistence.*;
import lombok.Getter;

import org.kirkiano.finance.bank.model.BaseEntity;


/**
 * Abstract base class of loans
 */
@Getter
@MappedSuperclass
public abstract class Loan extends BaseEntity {}
