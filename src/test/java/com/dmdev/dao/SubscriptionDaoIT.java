package com.dmdev.dao;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionDaoIT extends IntegrationTestBase {

    private SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void findAll() {
        var subscription1 = subscriptionDao.insert(Subscription.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.APPLE)
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .status(Status.ACTIVE)
                .build());
        var subscription2 = subscriptionDao.insert(Subscription.builder()
                .userId(2)
                .name("Petr")
                .provider(Provider.APPLE)
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .status(Status.ACTIVE)
                .build());

        var actualResult = subscriptionDao.findAll();

        assertThat(actualResult).hasSize(2);
        var subscriptionIds = actualResult.stream()
                .map(Subscription::getId)
                .toList();
        assertThat(subscriptionIds).contains(subscription1.getId(), subscription2.getId());
    }

    @Test
    void findById() {
        var subscription = subscriptionDao.insert(getSubscription());
        var actualResult = subscriptionDao.findById(subscription.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(subscription);
    }

    @Test
    void deleteExistingSubscription() {
        var subscription = subscriptionDao.insert(getSubscription());

        var actualResult = subscriptionDao.delete(subscription.getId());

        assertTrue(actualResult);
    }

    @Test
    void deleteNotExistingSubscription() {
        var subscription = subscriptionDao.insert(getSubscription());

        var actualResult = subscriptionDao.delete(999);

        assertFalse(actualResult);
    }

    @Test
    void update() {
        var subscription = getSubscription();
        subscriptionDao.insert(subscription);
        subscription.setName("Ivan-updated");

        subscriptionDao.update(subscription);

        var updatedSubscription = subscriptionDao.findById(subscription.getId()).get();
        assertThat(updatedSubscription).isEqualTo(subscription);
    }

    @Test
    void insert() {
        var subscription = getSubscription();

        var actualResult = subscriptionDao.insert(subscription);

        assertNotNull(actualResult.getId());
    }

    @Test
    void findByUserId() {
        var subscription = subscriptionDao.insert(getSubscription());
        var actualResult = subscriptionDao.findByUserId(subscription.getUserId());

        assertThat(actualResult).hasSize(1);
        assertThat(actualResult.get(0)).isEqualTo(subscription);
    }

    @Test
    void shouldNotFindByUserIdIfSubscriptionDoesNotExist() {
        var subscription = subscriptionDao.insert(getSubscription());
        var actualResult = subscriptionDao.findByUserId(null);

        assertThat(actualResult).isEmpty();
    }

    private Subscription getSubscription() {
        return Subscription.builder()
                .userId(1)
                .name("Ivan")
                .provider(Provider.APPLE)
                .expirationDate(Clock.systemUTC().instant().plusSeconds(86400L).truncatedTo(ChronoUnit.HOURS))
                .status(Status.ACTIVE)
                .build();
    }
}