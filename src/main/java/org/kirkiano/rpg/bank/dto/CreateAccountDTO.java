package org.kirkiano.rpg.bank.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import org.kirkiano.rpg.bank.model.CharId;
import org.kirkiano.rpg.bank.model.Money;


/**
 * Data transfer object submitted by client to request creation of an account
 *
 * @param charId desired owner ID
 * @param balance desired initial balance
 */
public record CreateAccountDTO(

    @NotNull
    @NonNull
    CharId charId,

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Min(0)
    Money balance
)
{}