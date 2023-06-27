package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionDao subscriptionDao;
    @Mock
    private CreateSubscriptionMapper createSubscriptionMapper;
    @Mock
    private CreateSubscriptionValidator createSubscriptionValidator;
    @Mock
    private Clock clock;
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void upsertExistingSubscription() {
        CreateSubscriptionDto createSubscriptionDto = CreateSubscriptionDto.builder()
                .userId(99)
                .name("Ivan")
                .provider(Provider.APPLE.name())
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .build();
        var subscription = Subscription.builder()
                .id(1)
                .userId(99)
                .name("Ivan")
                .provider(Provider.APPLE)
                .expirationDate(null)
                .status(null)
                .build();
        doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(createSubscriptionDto);
        doReturn(List.of(subscription)).when(subscriptionDao).findByUserId(createSubscriptionDto.getUserId());
        doReturn(subscription).when(subscriptionDao).upsert(subscription);

        var actualResult = subscriptionService.upsert(createSubscriptionDto);

        assertThat(subscription).isEqualTo(actualResult);
        verify(subscriptionDao).findByUserId(createSubscriptionDto.getUserId());
        verify(subscriptionDao).upsert(subscription);
        verifyNoInteractions(createSubscriptionMapper);
    }

    @Test
    void upsertNotExistingSubscription() {
        CreateSubscriptionDto createSubscriptionDto = CreateSubscriptionDto.builder()
                .userId(99)
                .name("Ivan")
                .provider(Provider.APPLE.name())
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .build();
        var subscription = Subscription.builder()
                .id(1)
                .userId(99)
                .name("Ivan")
                .provider(Provider.APPLE)
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .status(null)
                .build();
        doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(createSubscriptionDto);
        doReturn(subscription).when(createSubscriptionMapper).map(createSubscriptionDto);
        doReturn(subscription).when(subscriptionDao).upsert(subscription);

        var actualResult = subscriptionService.upsert(createSubscriptionDto);

        assertThat(subscription).isEqualTo(actualResult);
        verify(subscriptionDao).findByUserId(createSubscriptionDto.getUserId());
//        verify(createSubscriptionMapper).map(createSubscriptionDto);
//        verify(subscriptionDao).insert(subscription);
    }

    @Test
    void shouldThrowExceptionIfSubscriptionInvalid() {
        CreateSubscriptionDto createSubscriptionDto = getCreateSubscriptionDto();
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of(100, "userId is invalid"));
        doReturn(validationResult).when(createSubscriptionValidator).validate(createSubscriptionDto);

        assertThrows(ValidationException.class, () -> subscriptionService.upsert(createSubscriptionDto));
        verifyNoInteractions(subscriptionDao, createSubscriptionMapper);
    }

    @Test
    void cancel() {
        var subscription = Subscription.builder()
                .id(99)
                .userId(1)
                .name("Ivan")
                .provider(Provider.APPLE)
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .status(Status.ACTIVE)
                .build();
        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());
        doReturn(subscription).when(subscriptionDao).update(subscription);

        subscriptionService.cancel(subscription.getId());

        verify(subscriptionDao).findById(subscription.getId());
        verify(subscriptionDao).update(subscription);
    }

    @Test
    void expire() {
        var subscription = Subscription.builder()
                .id(99)
                .userId(1)
                .name("Ivan")
                .provider(Provider.APPLE)
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .status(Status.ACTIVE)
                .build();
        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());
        doReturn(subscription).when(subscriptionDao).update(subscription);

        subscriptionService.expire(subscription.getId());

        verify(subscriptionDao).findById(subscription.getId());
        verify(subscriptionDao).update(subscription);
    }

//    @Test
//    void expireIfExpire() {
//        var subscription = Subscription.builder()
//                .id(99)
//                .userId(1)
//                .name("Ivan")
//                .provider(Provider.APPLE)
//                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
//                .status(Status.EXPIRED)
//                .build();
//        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());
//        doReturn(subscription).when(subscriptionDao).update(subscription);
//
//        subscriptionService.expire(subscription.getId());
//
//        assertThrows(SubscriptionException.class, () -> subscriptionDao.findById(subscription.getId()));
//        verify(subscriptionDao).findById(subscription.getId());
//        verify(subscriptionDao).update(subscription);
//    }

    private CreateSubscriptionDto getCreateSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.APPLE.name())
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .build();
    }

    private Subscription getSubscription() {
        return Subscription.builder()
                .id(99)
                .userId(1)
                .name("Ivan")
                .provider(Provider.APPLE)
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .status(Status.ACTIVE)
                .build();
    }
}









