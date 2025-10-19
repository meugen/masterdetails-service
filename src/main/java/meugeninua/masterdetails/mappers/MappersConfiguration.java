package meugeninua.masterdetails.mappers;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfiguration {
    @Bean
    public DetailEntityMapper detailMapper() {
        return Mappers.getMapper(DetailEntityMapper.class);
    }

    @Bean
    public MasterEntityMapper masterMapper() {
        return Mappers.getMapper(MasterEntityMapper.class);
    }
}
