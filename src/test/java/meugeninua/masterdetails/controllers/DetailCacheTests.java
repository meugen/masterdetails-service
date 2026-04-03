package meugeninua.masterdetails.controllers;

import meugeninua.masterdetails.caching.CachingConstants;
import meugeninua.masterdetails.entities.Detail;
import meugeninua.masterdetails.entities.Master;
import meugeninua.masterdetails.repositories.DetailRepository;
import meugeninua.masterdetails.repositories.MasterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static meugeninua.masterdetails.util.TestUtil.buildClient;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DetailCacheTests implements CachingConstants {

    @MockitoBean
    private MasterRepository masterRepository;

    @MockitoBean
    private DetailRepository detailRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final Random random = new Random();
    private final long masterId = random.nextLong();

    @Test
    void whenDetailGetAllTwice_thenRepositoryFindAllOnce() {
        var master = new Master();
        master.setId(masterId);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);
        var detail = new Detail();
        detail.setId(1L);
        detail.setName("Detail 1 in Master 1");
        detail.setMaster(master);

        when(detailRepository.findAllByMasterId(masterId)).thenReturn(List.of(detail));
        when(masterRepository.existsById(masterId)).thenReturn(true);

        redisTemplate.delete(detailsListKey(masterId));
        buildClient(port).get()
            .uri("/masters/{masterId}/details", masterId)
            .exchangeSuccessfully(); // Results cached
        buildClient(port).get()
            .uri("/masters/{masterId}/details", masterId)
            .exchangeSuccessfully(); // No need to retrieve again

        verify(detailRepository, times(1)).findAllByMasterId(masterId);
    }

    @Test
    void whenDetailGetTwice_thenRepositoryFindByIdOnce() {
        var detailId = random.nextLong();

        var master = new Master();
        master.setId(masterId);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);
        var detail = new Detail();
        detail.setId(detailId);
        detail.setName("Detail 1 in Master 1");
        detail.setMaster(master);

        when(detailRepository.findByMasterIdAndId(masterId, detailId)).thenReturn(Optional.of(detail));

        redisTemplate.delete(detailByIdKey(masterId, detailId));
        buildClient(port).get()
            .uri("/masters/{masterId}/details/{detailId}", masterId, detailId)
            .exchangeSuccessfully(); // Entity cached
        buildClient(port).get()
            .uri("/masters/{masterId}/details/{detailId}", masterId, detailId)
            .exchangeSuccessfully(); // No need to retrieve entity again

        verify(detailRepository, times(1)).findByMasterIdAndId(masterId, detailId);
    }

    @Test
    void whenDetailCreateAndGet_thenRepositoryFindByIdNever() {
        var detailId = random.nextLong();

        var master = new Master();
        master.setId(masterId);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);
        var detail = new Detail();
        detail.setId(detailId);
        detail.setName("Detail 1 in Master 1");
        detail.setMaster(master);

        when(detailRepository.save(any())).thenReturn(detail);
        when(detailRepository.findByMasterIdAndId(masterId, detailId)).thenReturn(Optional.of(detail));
        when(masterRepository.findById(masterId)).thenReturn(Optional.of(master));

        var body = """
            {
              "name": "Detail 1 in Master 1"
            }
            """;

        redisTemplate.delete(detailByIdKey(masterId, detailId));
        buildClient(port).post()
            .uri("/masters/{masterId}/details", masterId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeSuccessfully(); // Entity cached
        buildClient(port).get()
            .uri("/masters/{masterId}/details/{detailId}", masterId, detailId)
            .exchangeSuccessfully(); // No need to retrieve entity again

        verify(detailRepository, never()).findByMasterIdAndId(masterId, detailId);
    }

    @Test
    void whenUpdateAndGet_thenRepositoryFindByIdNever() {
        var detailId = random.nextLong();

        var master = new Master();
        master.setId(masterId);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);
        var detail = new Detail();
        detail.setId(detailId);
        detail.setName("Detail 1 in Master 1");
        detail.setMaster(master);

        when(detailRepository.save(any())).thenReturn(detail);
        when(detailRepository.findByMasterIdAndId(masterId, detailId)).thenReturn(Optional.of(detail));
        when(detailRepository.existsByMasterIdEqualsAndIdEquals(masterId, detailId)).thenReturn(true);
        when(masterRepository.findById(masterId)).thenReturn(Optional.of(master));

        var body = """
            {
              "name": "Detail 1 in Master 1"
            }
            """;

        redisTemplate.delete(detailByIdKey(masterId, detailId));
        buildClient(port).put()
            .uri("/masters/{masterId}/details/{detailId}", masterId, detailId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeSuccessfully(); // Entity cached
        buildClient(port).get()
            .uri("/masters/{masterId}/details/{detailId}", masterId, detailId)
            .exchangeSuccessfully(); // No need to retrieve entity again

        verify(detailRepository, never()).findByMasterIdAndId(masterId, detailId);
    }

    @Test
    void whenDetailCreateAndGetAll_thenRepositoryFindAllAgain() {
        var detailId = random.nextLong();

        var master = new Master();
        master.setId(masterId);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);
        var detail = new Detail();
        detail.setId(detailId);
        detail.setName("Detail 1 in Master 1");
        detail.setMaster(master);

        when(detailRepository.save(any())).thenReturn(detail);
        when(masterRepository.findById(masterId)).thenReturn(Optional.of(master));
        when(detailRepository.findAllByMasterId(masterId)).thenReturn(List.of(detail));
        when(masterRepository.existsById(masterId)).thenReturn(true);

        var body = """
            {
              "name": "Detail 1 in Master 1"
            }
            """;

        redisTemplate.delete(detailsListKey(masterId));
        buildClient(port).get()
            .uri("/masters/{masterId}/details", masterId)
            .exchangeSuccessfully(); // Results cached
        buildClient(port).post()
            .uri("/masters/{masterId}/details", masterId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeSuccessfully(); // Cache evicted
        buildClient(port).get()
            .uri("/masters/{masterId}/details", masterId)
            .exchangeSuccessfully(); // Need to retrieve results again

        verify(detailRepository, times(2)).findAllByMasterId(masterId);
    }

    @Test
    void whenDetailUpdateAndGetAll_thenRepositoryFindAllAgain() {
        var detailId = random.nextLong();

        var master = new Master();
        master.setId(masterId);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);
        var detail = new Detail();
        detail.setId(detailId);
        detail.setName("Detail 1 in Master 1");
        detail.setMaster(master);

        when(detailRepository.save(any())).thenReturn(detail);
        when(masterRepository.findById(masterId)).thenReturn(Optional.of(master));
        when(detailRepository.findAllByMasterId(masterId)).thenReturn(List.of(detail));
        when(detailRepository.existsByMasterIdEqualsAndIdEquals(masterId, detailId)).thenReturn(true);
        when(masterRepository.existsById(masterId)).thenReturn(true);

        var body = """
            {
              "name": "Detail 1 in Master 1"
            }
            """;

        redisTemplate.delete(detailsListKey(masterId));
        buildClient(port).get()
            .uri("/masters/{masterId}/details", masterId)
            .exchangeSuccessfully(); // Results cached
        buildClient(port).put()
            .uri("/masters/{masterId}/details/{detailId}", masterId, detailId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeSuccessfully(); // Cache evicted
        buildClient(port).get()
            .uri("/masters/{masterId}/details", masterId)
            .exchangeSuccessfully(); // Need to retrieve results again

        verify(detailRepository, times(2)).findAllByMasterId(masterId);
    }

    @Test
    void whenDetailDeleteAndGetAll_thenRepositoryFindAllAgain() {
        var detailId = random.nextLong();

        var master = new Master();
        master.setId(masterId);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);
        var detail = new Detail();
        detail.setId(detailId);
        detail.setName("Detail 1 in Master 1");
        detail.setMaster(master);

        when(detailRepository.findByMasterIdAndId(masterId, detailId)).thenReturn(Optional.of(detail));
        when(detailRepository.findAllByMasterId(masterId)).thenReturn(List.of(detail));
        when(masterRepository.existsById(masterId)).thenReturn(true);

        redisTemplate.delete(detailsListKey(masterId));
        buildClient(port).get()
            .uri("/masters/{masterId}/details", masterId)
            .exchangeSuccessfully(); // Results cached
        buildClient(port).delete()
            .uri("/masters/{masterId}/details/{detailId}", masterId, detailId)
            .exchangeSuccessfully(); // Cache evicted
        buildClient(port).get()
            .uri("/masters/{masterId}/details", masterId)
            .exchangeSuccessfully(); // Need to retrieve results again

        verify(detailRepository, times(2)).findAllByMasterId(masterId);
    }

    @Test
    void whenDetailDeleteAndGet_thenRepositoryFindByIdAgain() {
        var detailId = random.nextLong();

        var master = new Master();
        master.setId(masterId);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);
        var detail = new Detail();
        detail.setId(detailId);
        detail.setName("Detail 1 in Master 1");
        detail.setMaster(master);

        when(detailRepository.findByMasterIdAndId(masterId, detailId)).thenReturn(Optional.of(detail));

        redisTemplate.delete(detailByIdKey(masterId, detailId));
        buildClient(port).get()
            .uri("/masters/{masterId}/details/{detailId}", masterId, detailId)
            .exchangeSuccessfully(); // Entity cached
        buildClient(port).delete()
            .uri("/masters/{masterId}/details/{detailId}", masterId, detailId)
            .exchangeSuccessfully(); // Cache evicted
        buildClient(port).get()
            .uri("/masters/{masterId}/details/{detailId}", masterId, detailId)
            .exchangeSuccessfully(); // Need to retrieve entity again

        // One more call in delete endpoint
        verify(detailRepository, times(3)).findByMasterIdAndId(masterId, detailId);
    }

    @Test
    void whenDetailDeleteAndGetOther_thenRepositoryFindByIdOnce() {
        var deletedDetailId = random.nextLong(10);
        var otherDetailId = random.nextLong(10, 100);

        var master = new Master();
        master.setId(masterId);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);
        var deletedDetail = new Detail();
        deletedDetail.setId(deletedDetailId);
        deletedDetail.setName("Detail 1 in Master 1");
        deletedDetail.setMaster(master);
        var otherDetail = new Detail();
        otherDetail.setId(otherDetailId);
        otherDetail.setName("Detail 2 in Master 1");
        otherDetail.setMaster(master);

        when(detailRepository.findByMasterIdAndId(masterId, deletedDetailId)).thenReturn(Optional.of(deletedDetail));
        when(detailRepository.findByMasterIdAndId(masterId, otherDetailId)).thenReturn(Optional.of(otherDetail));

        buildClient(port).get()
            .uri("/masters/{masterId}/details/{detailId}", masterId, otherDetailId)
            .exchangeSuccessfully(); // Entity cached
        buildClient(port).delete()
            .uri("/masters/{masterId}/details/{detailId}", masterId, deletedDetailId)
            .exchangeSuccessfully(); // Evicted deleted entity cache only
        buildClient(port).get()
            .uri("/masters/{masterId}/details/{detailId}", masterId, otherDetailId)
            .exchangeSuccessfully(); // No need to retrieve other entity again

        verify(detailRepository, times(1)).findByMasterIdAndId(masterId, otherDetailId);
    }

}
