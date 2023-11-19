package org.kirkiano.rpg.bank.model;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;


/**
 * Converts plain string into a {@link CharId}. Eases extraction of
 * a {@link CharId} from URL, for example.
 */
@Component
public class StringToCharIdConverter implements Converter<String, CharId> {

    /**
     * Constructor
     */
    public StringToCharIdConverter() {}

    @Override
    @Nullable
    public CharId convert(@NonNull String source) {
        var val = Long.parseLong(source);
        return new CharId(val);
    }
}
