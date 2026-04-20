package meugeninua.masterdetails.mappers;

import meugeninua.masterdetails.dto.MasterDto;
import meugeninua.masterdetails.entities.Master;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = DetailEntityMapper.class)
public interface MasterEntityMapper {
    @Mapping(target = "details", ignore = true)
    Master mapToEntity(MasterDto dto);
    MasterDto mapToDto(Master entity);
}
