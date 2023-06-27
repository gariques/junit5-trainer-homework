package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubscriptionServiceIT extends IntegrationTestBase {

    private SubscriptionDao subscriptionDao;
    private SubscriptionService subscriptionService;

    @BeforeEach
    void init() {
        subscriptionDao = SubscriptionDao.getInstance();
        subscriptionService = new SubscriptionService(
                subscriptionDao,
                CreateSubscriptionMapper.getInstance(),
                CreateSubscriptionValidator.getInstance(),
                Clock.systemUTC()
        );
    }

    @Test
    void upsert() {
        CreateSubscriptionDto createSubscriptionDto = getCreateSubscriptionDto();

        var actualResult = subscriptionService.upsert(createSubscriptionDto);

        assertNotNull(actualResult.getId());

    }

    private CreateSubscriptionDto getCreateSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.APPLE.name())
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .build();
    }
}
