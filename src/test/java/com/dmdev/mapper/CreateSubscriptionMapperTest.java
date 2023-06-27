package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper mapper = CreateSubscriptionMapper.getInstance();

    @Test
    void map() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.APPLE.name())
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .build();

        var actualResult = mapper.map(dto);
        Subscription expectedResult = Subscription.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.APPLE)
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .status(Status.ACTIVE)
                .build();

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}