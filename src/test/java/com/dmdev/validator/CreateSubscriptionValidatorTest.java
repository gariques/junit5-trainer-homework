package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionValidatorTest {

    private final CreateSubscriptionValidator validator = CreateSubscriptionValidator.getInstance();

    @Test
    void shouldPassValidation() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.APPLE.name())
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L))
                .build();

        var actualResult = validator.validate(dto);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    void invalidUserId() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("Ivan")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.MAX)
                .build();

        var actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(100);
    }

    @Test
    void invalidName() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name(null)
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.MAX)
                .build();

        var actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(101);
    }

    @Test
    void invalidProvider() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider("fake")
                .expirationDate(Instant.MAX)
                .build();

        var actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(102);
    }

    @Test
    void invalidExpirationDate() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.APPLE.name())
                .expirationDate(Clock.systemUTC().instant().minusSeconds(86400L))
                .build();

        var actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(103);
    }

    @Test
    void invalidUserIdNameProviderExpirationDate() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(null)
                .name(" ")
                .provider("fake")
                .expirationDate(Clock.systemUTC().instant().minusSeconds(86400L))
                .build();

        var actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(4);
        var errorCodes = actualResult.getErrors().stream()
                .map(Error::getCode)
                .toList();
        assertThat(errorCodes).contains(100, 101, 102, 103);
    }
}










