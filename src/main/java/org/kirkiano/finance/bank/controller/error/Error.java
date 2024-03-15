package org.kirkiano.finance.bank.controller.error;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import org.kirkiano.finance.bank.model.CharId;


/**
 * Error intended for the client, typically as JSON
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode
public abstract class Error {


    /**
     * Client referred to a {@link CharId} not found in the database.
     * This {@link Error} has {@link ErrorCode#CharIdNotFound}.
     */
    @Getter @Setter @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class CharIdNotFound extends Error {

        /**
         * Constructor
         * @param charId the nonexistent character ID
         */
        public CharIdNotFound(CharId charId) {
            super(ErrorCode.CharIdNotFound);
            this.charId = charId.value();
        }

        private long charId;
    }


    /**
     * Client attempted an operation that would have left a negative
     * balance in an account.
     * This {@link Error} has {@link ErrorCode#InsufficientFunds}.
     */
    @EqualsAndHashCode(callSuper = true)
    public static class InsufficientFunds extends Error {
        /**
         * Constructor
         */
        public InsufficientFunds() {
            super(ErrorCode.InsufficientFunds);
        }
    }


    /**
     * Client tried to create an account that already exists.
     * This {@link Error} has {@link ErrorCode#AccountAlreadyExists}.
     */
    @Getter @Setter @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class AccountAlreadyExists extends Error {
        /**
         * Constructor
         *
         * @param charId character ID
         */
        public AccountAlreadyExists(CharId charId) {
            super(ErrorCode.AccountAlreadyExists);
            this.charId = charId.value();
        }

        private long charId;
    }


    /**
     * Client referred to a nonexistent account.
     * This {@link Error} has {@link ErrorCode#NoSuchAccountId}.
     */
    @Getter @Setter @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class NoSuchAccountId extends Error {
        /**
         * Constructor
         * @param id ID of the nonexistent account
         */
        public NoSuchAccountId(long id) {
            super(ErrorCode.NoSuchAccountId);
            this.id = id;
        }

        private long id;
    }


    /**
     * Client did not provide a required {@link CharId}.
     * This {@link Error} has {@link ErrorCode#CharIdRequired}.
     */
    @Getter @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class CharIdRequired extends Error {

        /**
         * Constructor
         */
        public CharIdRequired() {
            super(ErrorCode.CharIdRequired);
        }
    }


    /**
     * Client's submission entails a type error.
     * This {@link Error} has {@link ErrorCode#TypeError}.
     */
    @Getter @Setter @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class Type extends Error {

        /**
         * Create a {@link Type} {@link Error}. If {@code expectedType}
         * is a {@code CharId.class}, it will be understood as
         * {@link CharId}'s internal type, eg, {@link Long}.
         *
         * @param field name of the wrongly typed value
         * @param value the wrongly typed value
         * @param expectedType the type that {@code value} should have had
         * @return the type error
         */
        public static Type create(String field,
                                  @Nullable Object value,
                                  Class<?> expectedType)
        {
            if (expectedType.equals(CharId.class)) {
                Field charInnerField = CharId.class.getDeclaredFields()[0];
                charInnerField.setAccessible(true);
                expectedType = charInnerField.getType(); // clobber!
            }
            return new Type(field, value, expectedType);
        }

        /**
         * Create a {@link Type} {@link Error} out of an
         * {@link InvalidFormatException}.
         *
         * @param ife the exception
         * @return the type error
         */
        public static Type create(InvalidFormatException ife) {
            List<String> ps = ife.getPath()
                .stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.toList());
            String p = String.join(" -> ", ps);
            return new Type(p, ife.getValue(), ife.getTargetType());
        }

        ///////////////////////////////////////////////////
        // private

        /**
         * Constructor
         *
         * @param field name of the wrongly typed value
         * @param value the wrongly typed value
         * @param expectedType the type that {@code value} was supposed to have
         */
        private Type(String field,
                     @Nullable Object value,
                     Class<?> expectedType)
        {
            super(ErrorCode.TypeError);
            this.field = field;
            this.value = value;
            this.expectedType = expectedType.equals(Long.class) ? "long"
                : expectedType.equals(Integer.class) ? "int"
                : expectedType.getSimpleName();
        }

        private String field;

        @Nullable
        private Object value;

        private String expectedType;
    }


    /**
     * Client referred to an unknown field or property.
     * This {@link Error} will have {@link ErrorCode#UnknownProperty}.
     */
    @Getter @Setter @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class UnknownProperty extends Error {
        /**
         * Construct an {@link UnknownProperty} @{link Error}.
         *
         * @param propertyPath the prefix path of the unknown property
         * @param property the final segment of the property's path
         */
        public UnknownProperty(@Nullable String propertyPath, String property) {
            super(ErrorCode.UnknownProperty);
            this.propertyPath = propertyPath;
            this.property = property;
        }

        public UnknownProperty(UnrecognizedPropertyException upe) {
            this(upe.getPath()
                    .stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining(" -> ")),
                 upe.getPropertyName());
        }

        private String property;

        @Nullable
        private String propertyPath;
    }


    /**
     * Client submitted data that could not be parsed into JSON.
     * This {@link Error} has {@link ErrorCode#Unparseable}.
     */
    @Getter @Setter @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class UnparseableJson extends Error {
        /**
         * Construct an {@link UnparseableJson} {@link Error} out of
         * a {@link JsonLocation}.
         *
         * @param loc the JSON location
         */
        public UnparseableJson(JsonLocation loc) {
            this(loc.getLineNr(),
                 loc.getColumnNr(),
                 loc.getCharOffset(),
                 loc.getByteOffset());
        }

        /**
         * Construct an {@link UnparseableJson} {@link Error}.
         *
         * @param lineNumber line number (nullable)
         * @param columnNumber column number (nullable)
         * @param charOffset character offset (nullable)
         * @param byteOffset byte offset (nullable)
         */
        public UnparseableJson(int lineNumber, int columnNumber,
                               long charOffset, long byteOffset)
        {
            super(ErrorCode.Unparseable);
            this.lineNumber = lineNumber;
            this.columnNumber = columnNumber;
            this.charOffset = charOffset;
            this.byteOffset = byteOffset;
        }

        private Long charOffset;
        private Integer lineNumber;
        private Integer columnNumber;
        private Long byteOffset;
    }


    /**
     * General client error.
     * This {@link Error} has {@link ErrorCode#General}.
     */
    @Getter @Setter @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class General extends Error {
        /**
         * Constructor
         *
         * @param msg error message
         */
        public General(String msg) {
            super(ErrorCode.General);
            this.message = msg;
        }

        private String message;
    }

    ///////////////////////////////////////////////////////
    // protected

    /**
     * Base constructor
     *
     * @param ec error code
     */
    protected Error(ErrorCode ec) {
        this.error = ec.getName();
        this.errorNumber = ec.getNumber();
    }

    ///////////////////////////////////////////////////////
    // private

    private String error;
    private int errorNumber;

}