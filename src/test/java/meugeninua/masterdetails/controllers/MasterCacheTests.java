package meugeninua.masterdetails.controllers;

import meugeninua.masterdetails.caching.CachingConstants;
import meugeninua.masterdetails.entities.Master;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MasterCacheTests implements CachingConstants {

    @MockitoBean
    private MasterRepository masterRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final Random random = new Random();

    @Test
    void whenMasterGetAllTwice_thenRepositoryFindAllOnce() {
        var master = new Master();
        master.setId(1L);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);

        when(masterRepository.findAll()).thenReturn(List.of(master));

        redisTemplate.delete(mastersListKey());
        buildClient(port).get()
            .uri("/masters")
            .exchangeSuccessfully(); // Results cached
        buildClient(port).get()
            .uri("/masters")
            .exchangeSuccessfully(); // No need retrieve results again

        verify(masterRepository, times(1)).findAll();
    }

    @Test
    void whenMasterGetTwice_thenRepositoryFindByIdOnce() {
        var id = random.nextLong();

        var master = new Master();
        master.setId(id);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);

        when(masterRepository.findById(id)).thenReturn(Optional.of(master));

        redisTemplate.delete(masterByIdKey(id));
        buildClient(port).get()
            .uri("/masters/{id}", id)
            .exchangeSuccessfully(); // Results cached
        buildClient(port).get()
            .uri("/masters/{id}", id)
            .exchangeSuccessfully(); // No need to retrieve results again

        verify(masterRepository, times(1)).findById(id);
    }

    @Test
    void whenMasterCreateAndGet_thenRepositoryFindByIdNever() {
        var id = random.nextLong();

        var master = new Master();
        master.setId(id);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);

        when(masterRepository.findById(id)).thenReturn(Optional.of(master));
        when(masterRepository.save(any())).thenReturn(master);

        var body = """
            {
              "name": "Master 1",
              "details": [],
              "count": 1
            }
            """;

        redisTemplate.delete(masterByIdKey(id));
        buildClient(port).post()
            .uri("/masters")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeSuccessfully(); // Created entity cached
        buildClient(port).get()
            .uri("/masters/{id}", id)
            .exchangeSuccessfully(); // No need to retrieve entity again

        verify(masterRepository, never()).findById(id);
    }

    @Test
    void whenMasterUpdateAndGet_thenRepositoryFindByIdNever() {
        var id = random.nextLong();

        var master = new Master();
        master.setId(id);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);

        when(masterRepository.findById(id)).thenReturn(Optional.of(master));
        when(masterRepository.save(any())).thenReturn(master);
        when(masterRepository.existsById(id)).thenReturn(true);

        var body = """
            {
              "name": "Master 1",
              "details": [],
              "count": 1
            }
            """;

        redisTemplate.delete(masterByIdKey(id));
        buildClient(port).put()
            .uri("/masters/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeSuccessfully(); // Entity cached
        buildClient(port).get()
            .uri("/masters/{id}", id)
            .exchangeSuccessfully(); // No need to retrieve entity again

        verify(masterRepository, never()).findById(id);
    }

    @Test
    void whenMasterCreateAndGetAll_thenRepositoryFindAllAgain() {
        var master = new Master();
        master.setId(1L);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);

        when(masterRepository.findAll()).thenReturn(List.of(master));
        when(masterRepository.save(any())).thenReturn(master);

        var body = """
            {
              "name": "Master 1",
              "details": [],
              "count": 1
            }
            """;

        redisTemplate.delete(mastersListKey());
        buildClient(port).get()
            .uri("/masters")
            .exchangeSuccessfully(); // Results cached
        buildClient(port).post()
            .uri("/masters")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeSuccessfully(); // Cache evicted
        buildClient(port).get()
            .uri("/masters")
            .exchangeSuccessfully(); // Need to retrieve results again

        verify(masterRepository, times(2)).findAll();
    }

    @Test
    void whenMasterUpdateAndGetAll_thenRepositoryFindAllAgain() {
        var id = random.nextLong();

        var master = new Master();
        master.setId(id);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);

        when(masterRepository.findAll()).thenReturn(List.of(master));
        when(masterRepository.save(any())).thenReturn(master);
        when(masterRepository.existsById(id)).thenReturn(true);

        var body = """
            {
              "name": "Master 1",
              "details": [],
              "count": 1
            }
            """;

        redisTemplate.delete(mastersListKey());
        buildClient(port).get()
            .uri("/masters")
            .exchangeSuccessfully(); // Results cached
        buildClient(port).put()
            .uri("/masters/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeSuccessfully(); // Cache evicted
        buildClient(port).get()
            .uri("/masters")
            .exchangeSuccessfully(); // Need to retrieve results again

        verify(masterRepository, times(2)).findAll();
    }

    @Test
    void whenDeleteAndGetAll_thenRepositoryFindAllAgain() {
        var master = new Master();
        master.setId(1L);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);

        when(masterRepository.findAll()).thenReturn(List.of(master));
        when(masterRepository.findById(any())).thenReturn(Optional.of(master));

        redisTemplate.delete(mastersListKey());
        buildClient(port).get()
            .uri("/masters")
            .exchangeSuccessfully(); // Results cached
        buildClient(port).delete()
            .uri("/masters/{id}", random.nextLong())
            .exchangeSuccessfully(); // Cache evicted
        buildClient(port).get()
            .uri("/masters")
            .exchangeSuccessfully(); // Need to retrieve results again

        verify(masterRepository, times(2)).findAll();
    }

    @Test
    void whenDeleteAndGet_thenRepositoryFindByIdAgain() {
        var id = random.nextLong();

        var master = new Master();
        master.setId(id);
        master.setName("Master 1");
        master.setDetails(new ArrayList<>());
        master.setCount(1);

        when(masterRepository.findById(any())).thenReturn(Optional.of(master));

        redisTemplate.delete(masterByIdKey(id));
        buildClient(port).get()
            .uri("/masters/{id}", id)
            .exchangeSuccessfully(); // Entity cached
        buildClient(port).delete()
            .uri("/masters/{id}", id)
            .exchangeSuccessfully(); // Cache evicted
        buildClient(port).get()
            .uri("/masters/{id}", id)
            .exchangeSuccessfully(); // Need to retrieve entity again

        // One more call during delete endpoint
        verify(masterRepository, times(3)).findById(id);
    }

    @Test
    void whenDeleteAndGetOther_thenRepositoryFindByIdOnce() {
        var deletedId = random.nextLong(10);
        var otherId = random.nextLong(10, 100);

        var deletedMaster = new Master();
        deletedMaster.setId(deletedId);
        deletedMaster.setName("Master " + deletedId);
        deletedMaster.setDetails(new ArrayList<>());
        deletedMaster.setCount(1);
        var otherMaster = new Master();
        otherMaster.setId(otherId);
        otherMaster.setName("Master " + otherId);
        otherMaster.setDetails(new ArrayList<>());
        otherMaster.setCount(1);

        when(masterRepository.findById(deletedId)).thenReturn(Optional.of(deletedMaster));
        when(masterRepository.findById(otherId)).thenReturn(Optional.of(otherMaster));

        redisTemplate.delete(masterByIdKey(otherId));
        when(masterRepository.findById(otherId)).thenReturn(Optional.of(otherMaster));
        when(masterRepository.findById(deletedId)).thenReturn(Optional.of(deletedMaster));

        buildClient(port).get()
            .uri("/masters/{id}", otherId)
            .exchangeSuccessfully(); // Entity cached
        buildClient(port).delete()
            .uri("/masters/{id}", deletedId)
            .exchangeSuccessfully(); // Cache evicted for deleted entity only
        buildClient(port).get()
            .uri("/masters/{id}", otherId)
            .exchangeSuccessfully(); // No need to retrieve other entity again

        verify(masterRepository, times(1)).findById(otherId);
    }
}
